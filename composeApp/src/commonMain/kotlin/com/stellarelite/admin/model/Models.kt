package com.stellarelite.admin.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

// ─── Admin Roles ───
enum class AdminRole(val label: String) {
    CF("客服"), GJ("管家"), DSZ("董事长"), MS("秘书"), KJ("会计"), ZG("总管")
}

// ─── Staff Profile ───
@Serializable
data class StaffProfile(
    val id: String = "",
    val username: String = "",
    val real_name: String = "",
    val role: String = "",
    val phone: String = "",
    val wechat: String = "",
    val email: String = "",
    val avatar_url: String = "",
    val wallet_balance: Double = 0.0,
    val collected_sgd: Double = 0.0,
    val collected_rm: Double = 0.0,
    val status: String = "active",
    val created_at: String = ""
)

// ─── Admin User (after login) ───
@Serializable
data class AdminUser(
    val id: String,
    val email: String = "",
    val username: String = "",
    val real_name: String = "",
    val role: AdminRole = AdminRole.CF,
    val avatar_url: String = "",
    val phone: String = "",
    val wechat: String = "",
    val wallet_balance: Double = 0.0,
    val collected_sgd: Double = 0.0,
    val collected_rm: Double = 0.0,
    val is_on_duty: Boolean = false
)

// ─── Order / Trip ───
@Serializable
data class Order(
    val id: String = "",
    val user_id: String? = null,
    val order_no: String = "",
    val customer_name: String = "",
    val num_adults: Int = 0,
    val num_children: Int = 0,
    val luggage_count: Int = 0,
    val departure_time: String? = null,
    val pickup_location: String? = null,
    val destination: String? = null,
    val charter_duration: String? = null,
    val remarks: String? = null,
    val vehicle_type: String? = null,
    val vehicle_detail: String? = null,
    val amount: Double = 0.0,
    val status: String = "pending",
    val assigned_staff_id: String? = null,
    val assigned_driver_id: String? = null,
    val driver_salary: Double = 0.0,
    val cash_sgd: Double = 0.0,
    val cash_rm: Double = 0.0,
    val is_paid: Boolean = false,
    val cash_received_sgd: Double = 0.0,
    val cash_received_rm: Double = 0.0,
    val agent_fee: Double? = null,
    val finished_at: String? = null,
    val pickup_state: String? = null,
    val destination_state: String? = null,
    val phone: String? = null,
    val wechat: String? = null,
    val created_at: String = ""
)

// ─── Chat Message ───
@Serializable
data class ChatMessage(
    val id: String = "",
    val order_id: String = "",
    val message: String = "",
    val is_admin: Boolean = false,
    val created_at: String = ""
)

// ─── Duty Roster ───
@Serializable
data class DutyRoster(
    val id: String = "",
    val staff_id: String = "",
    val is_active: Boolean = false,
    val created_at: String = ""
)

// ─── Driver Vehicle ───
@Serializable
data class DriverVehicle(
    val id: String = "",
    val driver_id: String = "",
    val plate_no: String = "",
    val brand: String = "",
    val model: String = "",
    val seats: Int = 7,
    val is_active: Boolean = false,
    val status: String = "approved",
    val created_at: String = ""
)

// ─── Customer Profile ───
@Serializable
data class CustomerProfile(
    val id: String = "",
    val username: String = "",
    val real_name: String = "",
    val email: String = "",
    val phone: String = "",
    val avatar_url: String = "",
    val created_at: String = ""
)

// ─── Driver Expense ───
@Serializable
data class DriverExpense(
    val id: String = "",
    val driver_id: String = "",
    val plate_no: String = "",
    val expense_date: String = "",
    val usage_type: String = "",
    val currency: String = "RM",
    val amount: Double = 0.0,
    val note: String = "",
    val receipt_url: String = "",
    val vehicle_id: String = "",
    val created_at: String = ""
)

// ─── Financial Request ───
@Serializable
data class FinancialRequest(
    val id: String = "",
    val order_no: String = "",
    val type: String = "SJ",
    val user_id: String = "",
    val real_name: String = "",
    val user_role: String = "",
    val amount: Double = 0.0,
    val currency: String = "RM",
    val method: String = "CASH",
    val account_number: String = "",
    val account_name: String = "",
    val qr_url: String = "",
    val audit_command: String = "",
    val status: String = "pending",
    val remark: String = "",
    val created_at: String = ""
)

// ─── Wallet Ledger ───
@Serializable
data class WalletLedgerEntry(
    val id: String = "",
    val created_at: String = "",
    val actor_id: String = "",
    val actor_type: String = "",
    val amount: Double = 0.0,
    val currency: String = "RM",
    val transaction_type: String = "",
    val status: String = "completed",
    val reference_id: String = "",
    val description: String = "",
    val balance_after: Double = 0.0
)

// ─── Storage File ───
@Serializable
data class StorageFile(
    val name: String = "",
    val id: String? = null,
    val created_at: String? = null,
    val metadata: JsonObject? = null
)

// ─── Supabase Auth Response ───
@Serializable
data class SupabaseAuthResponse(
    val access_token: String = "",
    val refresh_token: String = "",
    val user: SupabaseUser? = null
)

@Serializable
data class SupabaseUser(
    val id: String = "",
    val email: String = "",
    val role: String = ""
)

// ─── Supabase API Wrapper ───
@Serializable
data class SupabaseQueryResult<T>(
    val data: List<T>? = null,
    val error: String? = null,
    val count: Int = 0
)
