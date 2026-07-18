package com.stellarelite.admin.data

import com.stellarelite.admin.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.*

/**
 * Supabase REST API Client for KMP
 */
class SupabaseClient {
    private val baseUrl = "https://zewztxwrlmejngypfszd.supabase.co"
    private val anonKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inpld3p0eHdybG1lam5neXBmc3pkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzYwNzQxMDUsImV4cCI6MjA5MTY1MDEwNX0.peuZcgDlHur92SnCJrNz2TxOlReeoQJmUbdZLvpfpag"

    private var accessToken: String = ""
    private var currentUserId: String = ""
    var onAuthChanged: ((Boolean) -> Unit)? = null

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = false
            })
        }
    }

    private fun headers(): HeadersBuilder.() -> Unit = {
        append("apikey", anonKey)
        append("Authorization", "Bearer $anonKey")
        append("Content-Type", "application/json")
        append("Prefer", "return=representation")
        if (accessToken.isNotEmpty()) {
            set("Authorization", "Bearer $accessToken")
        }
    }

    // ─── Auth ───
    suspend fun login(email: String, password: String): Result<AdminUser> {
        return try {
            val response = client.post("$baseUrl/auth/v1/token?grant_type=password") {
                headers()
                setBody(JsonObject(mapOf(
                    "email" to JsonPrimitive(email),
                    "password" to JsonPrimitive(password)
                )))
            }
            val body = Json.parseToJsonElement(response.bodyAsText()).jsonObject
            accessToken = body["access_token"]?.jsonPrimitive?.content ?: ""
            currentUserId = body["user"]?.jsonObject?.get("id")?.jsonPrimitive?.content ?: ""

            // Fetch staff profile
            val profileRes = client.get("$baseUrl/rest/v1/staff_profiles?id=eq.$currentUserId&select=*") {
                header("apikey", anonKey)
                header("Authorization", "Bearer $accessToken")
            }
            val profiles = Json.parseToJsonElement(profileRes.bodyAsText()).jsonArray
            if (profiles.isEmpty()) return Result.failure(Exception("未找到有效的职员档案信息"))

            val p = profiles[0].jsonObject
            val role = AdminRole.entries.find { it.name == p["role"]?.jsonPrimitive?.content } ?: AdminRole.CF

            onAuthChanged?.invoke(true)
            Result.success(AdminUser(
                id = currentUserId,
                email = email,
                real_name = p["real_name"]?.jsonPrimitive?.content ?: "",
                username = p["username"]?.jsonPrimitive?.content ?: "",
                role = role,
                phone = p["phone"]?.jsonPrimitive?.content ?: "",
                wechat = p["wechat"]?.jsonPrimitive?.content ?: "",
                avatar_url = p["avatar_url"]?.jsonPrimitive?.content ?: "",
                wallet_balance = p["wallet_balance"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0,
                collected_sgd = p["collected_sgd"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0,
                collected_rm = p["collected_rm"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        accessToken = ""
        currentUserId = ""
        onAuthChanged?.invoke(false)
    }

    fun isLoggedIn() = accessToken.isNotEmpty()

    // ─── Generic Supabase REST API ───
    private suspend fun getTable(table: String, query: String = "*", filter: String = "", order: String = ""): List<JsonObject> {
        return try {
            var url = "$baseUrl/rest/v1/$table?select=$query"
            if (filter.isNotEmpty()) url += "&$filter"
            if (order.isNotEmpty()) url += "&order=$order"
            val response = client.get(url) {
                header("apikey", anonKey)
                header("Authorization", "Bearer $anonKey")
                if (accessToken.isNotEmpty()) header("Authorization", "Bearer $accessToken")
            }
            Json.parseToJsonElement(response.bodyAsText()).jsonArray.map { it.jsonObject }
        } catch (_: Exception) { emptyList() }
    }

    private suspend fun updateRow(table: String, id: String, fields: Map<String, JsonElement>): Boolean {
        return try {
            client.patch("$baseUrl/rest/v1/$table?id=eq.$id") {
                header("apikey", anonKey)
                header("Authorization", "Bearer $anonKey")
                if (accessToken.isNotEmpty()) header("Authorization", "Bearer $accessToken")
                header("Prefer", "return=minimal")
                setBody(JsonObject(fields))
            }
            true
        } catch (_: Exception) { false }
    }

    private suspend fun insertRow(table: String, fields: Map<String, String>): Boolean {
        return try {
            client.post("$baseUrl/rest/v1/$table") {
                header("apikey", anonKey)
                header("Authorization", "Bearer $anonKey")
                if (accessToken.isNotEmpty()) header("Authorization", "Bearer $accessToken")
                header("Prefer", "return=minimal")
                setBody(JsonObject(fields.mapValues { JsonPrimitive(it.value) }))
            }
            true
        } catch (_: Exception) { false }
    }

    // ─── Orders ───
    suspend fun fetchOrders(): List<Order> {
        val fromCt = getTable("custom_trips", order = "created_at.desc")
        val fromOr = getTable("orders", order = "created_at.desc")
        return fromCt.map { parseOrder(it, isCustomTrip = true) } + fromOr.map { parseOrder(it, isCustomTrip = false) }
    }

    suspend fun updateOrderStatus(id: String, orderNo: String, status: String, extraFields: Map<String, String> = emptyMap()): Boolean {
        val table = if (orderNo.startsWith("SV-CT-")) "custom_trips" else "orders"
        val fields = (extraFields.mapValues { JsonPrimitive(it.value) } + ("status" to JsonPrimitive(status))).toMap()
        return updateRow(table, id, fields)
    }

    private fun parseOrder(jo: JsonObject, isCustomTrip: Boolean): Order {
        return Order(
            id = jo["id"]?.jsonPrimitive?.content ?: "",
            order_no = jo["order_no"]?.jsonPrimitive?.content ?: (if (isCustomTrip) "SV-CT-${(jo["id"]?.jsonPrimitive?.content ?: "").take(8)}" else ""),
            customer_name = jo["customer_name"]?.jsonPrimitive?.content ?: jo["contact_name"]?.jsonPrimitive?.content ?: "",
            num_adults = jo["num_adults"]?.jsonPrimitive?.content?.toIntOrNull() ?: 0,
            num_children = jo["num_children"]?.jsonPrimitive?.content?.toIntOrNull() ?: 0,
            departure_time = jo["departure_time"]?.jsonPrimitive?.contentOrNull,
            pickup_location = jo["pickup_location"]?.jsonPrimitive?.contentOrNull,
            destination = jo["destination"]?.jsonPrimitive?.contentOrNull,
            remarks = jo["remarks"]?.jsonPrimitive?.contentOrNull,
            vehicle_type = jo["vehicle_type"]?.jsonPrimitive?.contentOrNull,
            vehicle_detail = jo["vehicle_detail"]?.jsonPrimitive?.contentOrNull,
            amount = jo["amount"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0,
            status = jo["status"]?.jsonPrimitive?.content ?: "pending",
            assigned_staff_id = jo["assigned_staff_id"]?.jsonPrimitive?.contentOrNull,
            assigned_driver_id = jo["assigned_driver_id"]?.jsonPrimitive?.contentOrNull,
            driver_salary = jo["driver_salary"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0,
            cash_sgd = jo["cash_sgd"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0,
            cash_rm = jo["cash_rm"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0,
            is_paid = jo["is_paid"]?.jsonPrimitive?.content?.toBooleanStrictOrNull() ?: false,
            cash_received_sgd = jo["cash_received_sgd"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0,
            cash_received_rm = jo["cash_received_rm"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0,
            agent_fee = jo["agent_fee"]?.jsonPrimitive?.content?.toDoubleOrNull(),
            created_at = jo["created_at"]?.jsonPrimitive?.content ?: ""
        )
    }

    // ─── Profiles ───
    suspend fun fetchProfiles(): List<CustomerProfile> {
        return getTable("profiles", order = "created_at.desc").map { jo ->
            CustomerProfile(
                id = jo["id"]?.jsonPrimitive?.content ?: "",
                username = jo["username"]?.jsonPrimitive?.content ?: "",
                real_name = jo["real_name"]?.jsonPrimitive?.content ?: "",
                email = jo["email"]?.jsonPrimitive?.content ?: "",
                phone = jo["phone"]?.jsonPrimitive?.content ?: "",
                avatar_url = jo["avatar_url"]?.jsonPrimitive?.content ?: "",
                created_at = jo["created_at"]?.jsonPrimitive?.content ?: ""
            )
        }
    }

    // ─── Staff Profiles ───
    suspend fun fetchStaffProfiles(): List<StaffProfile> {
        return getTable("staff_profiles", order = "created_at.desc").map { jo ->
            StaffProfile(
                id = jo["id"]?.jsonPrimitive?.content ?: "",
                username = jo["username"]?.jsonPrimitive?.content ?: "",
                real_name = jo["real_name"]?.jsonPrimitive?.content ?: "",
                role = jo["role"]?.jsonPrimitive?.content ?: "",
                phone = jo["phone"]?.jsonPrimitive?.content ?: "",
                wechat = jo["wechat"]?.jsonPrimitive?.content ?: "",
                email = jo["email"]?.jsonPrimitive?.content ?: "",
                avatar_url = jo["avatar_url"]?.jsonPrimitive?.content ?: "",
                wallet_balance = jo["wallet_balance"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0,
                collected_sgd = jo["collected_sgd"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0,
                collected_rm = jo["collected_rm"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0
            )
        }
    }

    // ─── Chat Messages ───
    suspend fun fetchChatMessages(orderId: String? = null): List<ChatMessage> {
        val filter = if (orderId != null) "order_id=eq.$orderId" else ""
        return getTable("chat_messages", filter = filter, order = if (orderId != null) "created_at.asc" else "created_at.desc").map { jo ->
            ChatMessage(
                id = jo["id"]?.jsonPrimitive?.content ?: "",
                order_id = jo["order_id"]?.jsonPrimitive?.content ?: "",
                message = jo["message"]?.jsonPrimitive?.content ?: "",
                is_admin = jo["is_admin"]?.jsonPrimitive?.content?.toBooleanStrictOrNull() ?: false,
                created_at = jo["created_at"]?.jsonPrimitive?.content ?: ""
            )
        }
    }

    suspend fun sendAdminMessage(orderId: String, msg: String): Boolean {
        return insertRow("chat_messages", mapOf(
            "order_id" to orderId,
            "message" to msg,
            "is_admin" to "true"
        ))
    }

    // ─── Duty Roster ───
    suspend fun fetchDutyRoster(): List<DutyRoster> {
        return getTable("duty_roster", order = "created_at.desc").map { jo ->
            DutyRoster(
                id = jo["id"]?.jsonPrimitive?.content ?: "",
                staff_id = jo["staff_id"]?.jsonPrimitive?.content ?: "",
                is_active = jo["is_active"]?.jsonPrimitive?.content?.toBooleanStrictOrNull() ?: false
            )
        }
    }

    // ─── Driver Expenses ───
    suspend fun fetchDriverExpenses(): List<DriverExpense> {
        return getTable("driver_expenses", order = "created_at.desc").map { jo ->
            DriverExpense(
                id = jo["id"]?.jsonPrimitive?.content ?: "",
                driver_id = jo["driver_id"]?.jsonPrimitive?.content ?: "",
                plate_no = jo["plate_no"]?.jsonPrimitive?.content ?: "",
                expense_date = jo["expense_date"]?.jsonPrimitive?.content ?: "",
                usage_type = jo["usage_type"]?.jsonPrimitive?.content ?: "",
                currency = jo["currency"]?.jsonPrimitive?.content ?: "RM",
                amount = jo["amount"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0,
                note = jo["note"]?.jsonPrimitive?.content ?: "",
                receipt_url = jo["receipt_url"]?.jsonPrimitive?.content ?: ""
            )
        }
    }

    // ─── Financial Requests ───
    suspend fun fetchFinancialRequests(): List<FinancialRequest> {
        return getTable("financial_requests", order = "created_at.desc").map { jo ->
            FinancialRequest(
                id = jo["id"]?.jsonPrimitive?.content ?: "",
                order_no = jo["order_no"]?.jsonPrimitive?.content ?: "",
                type = jo["type"]?.jsonPrimitive?.content ?: "",
                user_id = jo["user_id"]?.jsonPrimitive?.content ?: "",
                real_name = jo["real_name"]?.jsonPrimitive?.content ?: "",
                user_role = jo["user_role"]?.jsonPrimitive?.content ?: "",
                amount = jo["amount"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0,
                currency = jo["currency"]?.jsonPrimitive?.content ?: "RM",
                method = jo["method"]?.jsonPrimitive?.content ?: "",
                status = jo["status"]?.jsonPrimitive?.content ?: "pending",
                created_at = jo["created_at"]?.jsonPrimitive?.content ?: ""
            )
        }
    }

    // ─── Wallet Ledger ───
    suspend fun fetchWalletLedger(): List<WalletLedgerEntry> {
        return getTable("wallet_ledger", order = "created_at.desc").map { jo ->
            WalletLedgerEntry(
                id = jo["id"]?.jsonPrimitive?.content ?: "",
                created_at = jo["created_at"]?.jsonPrimitive?.content ?: "",
                actor_id = jo["actor_id"]?.jsonPrimitive?.content ?: "",
                actor_type = jo["actor_type"]?.jsonPrimitive?.content ?: "",
                amount = jo["amount"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0,
                currency = jo["currency"]?.jsonPrimitive?.content ?: "RM",
                transaction_type = jo["transaction_type"]?.jsonPrimitive?.content ?: "",
                status = jo["status"]?.jsonPrimitive?.content ?: "",
                description = jo["description"]?.jsonPrimitive?.content ?: "",
                balance_after = jo["balance_after"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0
            )
        }
    }
}
