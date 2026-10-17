package com.stellarelite.admin.data

import com.stellarelite.admin.platform.readUriBytes
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import com.stellarelite.admin.platform.nowMillis

/** 企业 KYC 证件桶（私有桶；上传路径必须是 "{userId}/文件名"） */
private const val BUCKET_KYC = "se_kyc-companies"

/** 一份待上传的企业证件 */
data class KycDocument(
    /** se_company_documents.type —— 'ssm' / 'director_ic' / 'director_license' / 'staff_ic' */
    val type: String,
    /** 本地 uri（没选文件时为 null） */
    val uri: String?,
    /** 原始文件名（用来猜扩展名） */
    val fileName: String? = null,
)

/**
 * 后台端注册页收集的全部资料 —— 每个字段落哪张表见《后台端注册页接线表》。
 * 昵称/邮箱/手机号/微信号/官网/持有人居住地 → se_users
 * 企业字段（名称/BRN/注册日期/开始日期/营业地址/企业联系方式）→ se_accounts + se_enterprise_profiles / se_sdn_bhd_profiles
 * 创建密码 → 只给 Supabase Auth；确认密码 → 只在前端校验
 * 企业 KYC 证件 → Storage se_kyc-companies + se_company_documents
 */
data class RegisterPayload(
    val email: String,
    val password: String,
    val nickname: String,
    /** 'enterprise'（独资企业）或 'sdn_bhd'（融资企业） */
    val accountType: String,
    /** 有邀请码 → 只挂到邀请码所属账户，不建企业档案 */
    val inviteCode: String? = null,
    // ── 持有人（个人）部分 ──
    val phone: String = "",
    val wechatId: String = "",
    val website: String = "",
    val ownerAddrLine1: String = "",
    val ownerGarden: String = "",
    val ownerPostcode: String = "",
    val ownerCity: String = "",
    val ownerState: String = "",
    // ── 企业部分（无邀请码时才有值） ──
    val displayName: String = "",
    val brn: String = "",
    val registrationDate: String = "",
    val operationStartDate: String = "",
    val addrLine1: String = "",
    val garden: String = "",
    val postcode: String = "",
    val city: String = "",
    val state: String = "",
    val companyPhone: String = "",
    val companyEmail: String = "",
    val companyWebsite: String = "",
    val companyWechatId: String = "",
    /** 证件：无邀请码 → ssm / director_ic / director_license；有邀请码 → staff_ic */
    val documents: List<KycDocument> = emptyList(),
) {
    /** 有邀请码 = 挂靠员工（不建企业），否则自己开企业账户 */
    val hasInviteCode: Boolean get() = !inviteCode.isNullOrBlank()
}

/** 注册结果（证件上传失败不算注册失败，见 kycFailures） */
data class RegisterResult(
    val userId: String,
    val accountId: String?,
    /** 证件没传上去的原因（注册本身已成功，这些只是附件失败） */
    val kycFailures: List<String> = emptyList(),
)

/**
 * 后台端注册仓库：Auth 账号 → register_company RPC（单事务写业务表）→ 企业 KYC 证件。
 */
class RegisterRepository(
    private val supabase: SupabaseClient = Supabase.client,
) {

    // ---------------------------------------------------------- 一步注册（注册页用这个）
    suspend fun registerFull(payload: RegisterPayload): Result<RegisterResult> = try {
        Result.success(finishRegistration(payload))
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        // 业务表没写完就不把人放进 App：退掉会话，停在注册页重试
        try {
            supabase.auth.signOut()
        } catch (ignored: Exception) {
        }
        Result.failure(Exception(friendlyRegisterError(e), e))
    }

    private suspend fun finishRegistration(payload: RegisterPayload): RegisterResult {
        val userId = ensureAuthUser(payload.email, payload.password)
        val rpc = registerCompany(userId, payload)
        val accountId = (rpc as? JsonObject)?.get("account_id")?.jsonPrimitive?.contentOrNull

        // 会话先于业务行到位：刷新一次，让登录态重新取 primary_role
        try {
            supabase.auth.refreshCurrentSession()
        } catch (e: CancellationException) {
            throw e
        } catch (ignored: Exception) {
        }

        val failures = uploadKycDocuments(userId = userId, accountId = accountId, documents = payload.documents)
        return RegisterResult(userId = userId, accountId = accountId, kycFailures = failures)
    }

    /**
     * 拿到（必要时创建）Auth 账号的 userId —— 幂等，能自愈：
     * ① 先 signUp；
     * ② 没拿到用户（邮箱已注册 / 上次注册只建了 Auth 没写业务表 / 返回体没有 user）→ 用同样的邮箱+密码登录一次；
     * ③ 已经登录且邮箱对得上 → 直接用当前会话。
     * 只有三种都失败才报错，绝不因为 Auth 层的小状况把用户卡死在注册页。
     */
    private suspend fun ensureAuthUser(email: String, password: String): String {
        val target = email.trim()
        var signUpError: Throwable? = null
        val created = try {
            supabase.auth.signUpWith(Email) {
                this.email = target
                this.password = password
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            signUpError = e
            null
        }
        created?.id?.let { return it }

        supabase.auth.currentUserOrNull()
            ?.takeIf { it.email?.equals(target, ignoreCase = true) == true }
            ?.let { return it.id }

        try {
            supabase.auth.signInWith(Email) {
                this.email = target
                this.password = password
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            throw IllegalStateException(friendlySignUpError(signUpError, e), e)
        }
        val signedIn = supabase.auth.currentUserOrNull()
        if (signedIn != null && signedIn.email?.equals(target, ignoreCase = true) == true) return signedIn.id
        throw IllegalStateException("注册失败：拿不到登录状态，请重试")
    }

    // ---------------------------------------------------------- 业务表：单事务 RPC
    private suspend fun registerCompany(userId: String, payload: RegisterPayload): JsonElement {
        val rpc = supabase.postgrest.rpc(
            function = "register_company",
            parameters = buildJsonObject {
                put("p_user_id", userId)
                put("p_email", payload.email.trim())
                put("p_nickname", payload.nickname)
                put("p_account_type", payload.accountType)
                putBlankAsNull("p_invite_code", payload.inviteCode)
                // 持有人联系方式
                putBlankAsNull("p_phone", payload.phone)
                putBlankAsNull("p_wechat_id", payload.wechatId)
                putBlankAsNull("p_website", payload.website)
                // 持有人居住地
                putBlankAsNull("p_owner_addr_line1", payload.ownerAddrLine1)
                putBlankAsNull("p_owner_garden", payload.ownerGarden)
                putBlankAsNull("p_owner_postcode", payload.ownerPostcode)
                putBlankAsNull("p_owner_city", payload.ownerCity)
                putBlankAsNull("p_owner_state", payload.ownerState)
                // 企业资料（有邀请码时不传，RPC 也不会建企业档案）
                putBlankAsNull("p_display_name", payload.displayName)
                putBlankAsNull("p_brn", payload.brn)
                // date 列：空串会 cast 失败，必须传 null
                putDateOrNull("p_registration_date", payload.registrationDate)
                putDateOrNull("p_operation_start_date", payload.operationStartDate)
                putBlankAsNull("p_addr_line1", payload.addrLine1)
                putBlankAsNull("p_garden", payload.garden)
                putBlankAsNull("p_postcode", payload.postcode)
                putBlankAsNull("p_city", payload.city)
                putBlankAsNull("p_state", payload.state)
                putBlankAsNull("p_company_phone", payload.companyPhone)
                putBlankAsNull("p_company_email", payload.companyEmail)
                putBlankAsNull("p_company_website", payload.companyWebsite)
                putBlankAsNull("p_company_wechat_id", payload.companyWechatId)
            },
        ).decodeAs<JsonElement>()
        val ok = (rpc as? JsonObject)?.get("success")?.jsonPrimitive?.contentOrNull
        if (ok != "true") throw IllegalStateException("注册资料写入失败，请重试")
        return rpc
    }

    // ---------------------------------------------------------- 企业 KYC 证件
    private suspend fun uploadKycDocuments(
        userId: String,
        accountId: String?,
        documents: List<KycDocument>,
    ): List<String> {
        val failures = mutableListOf<String>()
        val uploaded = mutableListOf<Pair<String, String>>()
        val wanted = documents.filter { !it.uri.isNullOrBlank() }
        if (wanted.isEmpty()) return failures

        for (doc in wanted) {
            val uri = doc.uri ?: continue
            try {
                val bytes = readUriBytes(uri)
                if (bytes == null || bytes.isEmpty()) {
                    failures += doc.type + "：读不到本地文件"
                    continue
                }
                val ext = extensionOf(doc.fileName, uri)
                val path = userId + "/" + doc.type + "_" + nowMillis() + "." + ext
                supabase.storage.from(BUCKET_KYC).upload(path, bytes) { upsert = true }
                uploaded += doc.type to path
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                failures += doc.type + "：" + (e.message ?: "上传失败")
            }
        }

        // 单份证件失败不影响注册，只收集起来
        if (uploaded.isNotEmpty()) {
            if (accountId == null) {
                failures += "找不到账户，证件没有登记"
            } else {
                for ((type, path) in uploaded) {
                    try {
                        supabase.postgrest.from("se_company_documents").insert(
                            buildJsonObject {
                                put("account_id", accountId)
                                put("type", type)
                                put("file_url", path)
                                put("status", "pending")
                                put("uploaded_by_user_id", userId)
                            },
                        )
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        failures += type + "：证件登记失败（" + (e.message ?: "未知错误") + "）"
                    }
                }
            }
        }
        return failures
    }
}

/** 扩展名：从文件名/uri 猜，猜不到用 jpg */
private fun extensionOf(fileName: String?, uri: String?): String {
    val name = fileName?.takeIf { it.isNotBlank() } ?: uri.orEmpty()
    val ext = name.substringAfterLast('.', "").lowercase()
    return if (ext.length in 1..4 && ext.all { it.isLetterOrDigit() }) ext else "jpg"
}

/** 空字符串 → NULL（date / text 列都宁可空着也别写空串） */
private fun JsonObjectBuilder.putBlankAsNull(key: String, value: String?) {
    if (value.isNullOrBlank()) put(key, JsonNull) else put(key, value)
}

/** 日期：空字符串 → NULL（Postgres date 列 cast 不了空串） */
private fun JsonObjectBuilder.putDateOrNull(key: String, value: String?) {
    if (value.isNullOrBlank()) put(key, JsonNull) else put(key, value)
}

/** 认证/注册失败一律给用户看短句，不暴露底层错误码与原始报文 */
fun friendlySignUpError(signUpError: Throwable?, signInError: Throwable): String {
    val su = (signUpError?.message ?: "").lowercase()
    if (su.contains("already") || su.contains("exists")) {
        return "该邮箱已注册，但密码对不上。如果你就是本人，请直接登录；忘记密码可以点「忘记密码」。"
    }
    return friendlyLoginError(signInError)
}

fun friendlyLoginError(e: Throwable): String {
    val m = (e.message ?: "").lowercase()
    return when {
        m.contains("email not confirmed") || m.contains("not confirmed") -> "邮箱还没验证，请先到邮箱点验证链接"
        m.contains("too many") || m.contains("rate limit") -> "尝试太频繁，请稍后再试"
        m.contains("network") || m.contains("timeout") || m.contains("unable to resolve") ||
            m.contains("connect") || m.contains("offline") -> "网络异常，请检查网络后重试"
        else -> "账号与密码错误"
    }
}

/** 注册流程的失败文案：我们自己抛的（中文短句）原样透传，其余映射成人话 */
fun friendlyRegisterError(e: Throwable): String {
    val raw = e.message.orEmpty()
    // 自己抛的短句已经人话（含中文），直接给用户看
    if (raw.any { it.code in 0x4E00..0x9FFF }) return raw
    val m = raw.lowercase()
    return when {
        m.contains("network") || m.contains("timeout") || m.contains("unable to resolve") ||
            m.contains("connect") || m.contains("offline") -> "网络异常，请检查网络后重试"
        m.contains("already") || m.contains("exists") -> "该邮箱已注册，请直接用该邮箱登录"
        else -> "注册失败，请重试"
    }
}
