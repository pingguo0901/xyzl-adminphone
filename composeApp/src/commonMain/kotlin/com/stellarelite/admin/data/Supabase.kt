package com.stellarelite.admin.data

import com.russhwolf.settings.Settings
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.SettingsSessionManager
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

/**
 * Supabase 单例（懒加载，全 App 共用一份）—— 集团枢纽（SEH）项目。
 *
 * 集成模块：
 * - Auth      ：邮箱 / 密码 注册（后台端注册要先有 Auth 账号）
 * - Postgrest ：RPC（register_company）/ 表读写
 * - Storage   ：文件上传（企业 KYC 证件等）
 *
 * 会话持久化：multiplatform-settings
 *   Android = SharedPreferences，iOS = NSUserDefaults，重启 App 不掉登录。
 *
 * 注意：同目录的 SupabaseClient 是旧的本地模拟数据客户端（其他页面还在用），
 * 与本对象互不影响。
 */
object Supabase {

    /** 集团枢纽（SEH）项目地址 */
    const val URL: String = "https://yldweqbpvlngmkhvjwrn.supabase.co"

    /** anon key（客户端公开密钥，可随包发布；不要换成 service_role） */
    const val ANON_KEY: String =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InlsZHdlcWJwdmxuZ21raHZqd3JuIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTA3MDA1NzksImV4cCI6MjEwNjI3NjU3OX0.PSfYg0P64dJYdoJKd3c703oe64Ed0sdN1R7bAJ9oLxQ"

    /** 懒加载：第一次访问时才建客户端 */
    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = URL,
            supabaseKey = ANON_KEY,
        ) {
            install(Auth) {
                // 会话落盘（Settings() 来自 multiplatform-settings-no-arg）
                sessionManager = SettingsSessionManager(settings = Settings())
            }
            install(Postgrest)
            install(Storage)
        }
    }
}
