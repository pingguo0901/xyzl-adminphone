package com.stellarelite.admin.data

import com.stellarelite.admin.model.*
import kotlin.random.Random

/**
 * 本地模拟数据客户端 — 已断开 Supabase 后端
 * 所有数据均为静态模拟数据，APP 完全离线可运行
 */
class SupabaseClient {
    var onAuthChanged: ((Boolean) -> Unit)? = null

    private var isLoggedIn = false
    private var currentRole: AdminRole = AdminRole.DSZ

    // ─── 内置模拟数据 ───

    private val mockStaff = listOf(
        StaffProfile("s1", "boss", "聪锕", "DSZ", "+60 12-3456789", "boss_wechat", "boss@xyzl.admin", "", 0.0, 0.0, 0.0),
        StaffProfile("s2", "mishu", "陈秘书", "MS", "+60 13-1112223", "chen_wechat", "chen@xyzl.admin", "", 0.0, 0.0, 0.0),
        StaffProfile("s3", "kuaiji", "张会计", "KJ", "+60 13-2223334", "zhang_wechat", "zhang@xyzl.admin", "", 0.0, 0.0, 0.0),
        StaffProfile("s4", "driver1", "王师傅", "DR", "+60 16-8887771", "wang_wechat", "wang@xyzl.admin", "", 1500.0, 450.0, 1200.0),
        StaffProfile("s5", "driver2", "李师傅", "DR", "+60 16-8887772", "li_wechat", "li@xyzl.admin", "", 2000.0, 600.0, 1800.0),
        StaffProfile("s6", "driver3", "陈师傅", "DR", "+60 16-8887773", "chen_dr_wechat", "chend@xyzl.admin", "", 800.0, 200.0, 500.0),
        StaffProfile("s7", "driver4", "张师傅", "DR", "+60 16-8887774", "zhang_dr_wechat", "zhangd@xyzl.admin", "", 3200.0, 900.0, 2500.0),
        StaffProfile("s8", "keji", "林客服", "CF", "+60 14-5556667", "lin_wechat", "lin@xyzl.admin", "", 0.0, 0.0, 0.0),
        StaffProfile("s9", "guanjia", "黄管家", "GJ", "+60 14-5556668", "huang_wechat", "huang@xyzl.admin", "", 0.0, 0.0, 0.0),
        StaffProfile("s10", "zongguan", "吴总管", "ZG", "+60 14-5556669", "wu_wechat", "wu@xyzl.admin", "", 0.0, 0.0, 0.0),
    )

    private val mockOrders = listOf(
        Order("o1", "u101", "SV-20260715-001", "陈明", 2, 0, 2, "2026-07-18 09:00", "吉隆坡国际机场 KLIA", "云顶高原", "单程", "请准备儿童座椅", "豪华7座", "Toyota Alphard", 350.0, "confirmed", "s8", "s4", 280.0, 0.0, 350.0, true, 0.0, 0.0, null, null, "雪兰莪", "彭亨", "+60 12-1234567", "chen_ming", created_at = "2026-07-15 14:30"),
        Order("o2", "u102", "SV-20260716-002", "Alice Wong", 3, 1, 3, "2026-07-18 10:30", "吉隆坡双子塔", "马六甲古城", "一日游8小时", null, "豪华7座", "Toyota Vellfire", 600.0, "in_progress", "s8", "s5", 480.0, 0.0, 600.0, false, 0.0, 0.0, null, null, "吉隆坡", "马六甲", "+60 12-2345678", "alice_wong", created_at = "2026-07-16 09:15"),
        Order("o3", null, "SV-CT-20260717-001", "张伟", 4, 0, 4, "2026-07-18 14:00", "槟城国际机场", "槟城乔治市 + 极乐寺", "包车4小时", "临时定制行程", "商务6座", "Mercedes V-Class", 280.0, "pending", null, null, 200.0, 0.0, 280.0, false, 0.0, 0.0, null, null, "槟城", "槟城", "+60 12-3456789", "zhang_wei", created_at = "2026-07-17 16:00"),
        Order("o4", "u103", "SV-20260717-003", "刘德华", 1, 0, 1, "2026-07-18 16:00", "新山 CIQ", "新加坡樟宜机场", "单程跨境", "VIP贵宾，需举牌接机", "商务4座", "BMW 5 Series", 450.0, "confirmed", "s9", "s6", 350.0, 150.0, 0.0, true, 150.0, 0.0, null, null, "柔佛", "新加坡", "+60 12-4567890", "liu_huade", created_at = "2026-07-17 11:00"),
        Order("o5", null, "SV-CT-20260718-002", "Sarah Tan", 5, 2, 5, "2026-07-19 08:00", "吉隆坡市中心", "怡保 + 金马仑高原", "三日两夜", "多日行程，住宿已定", "中巴15座", "Toyota Hiace", 1800.0, "pending", null, null, 1500.0, 0.0, 1800.0, false, 0.0, 0.0, null, null, "吉隆坡", "彭亨/霹雳", "+60 12-5678901", "sarah_tan", created_at = "2026-07-18 07:00"),
        Order("o6", "u104", "SV-20260718-004", "林志玲", 2, 0, 2, "2026-07-18 12:00", "兰卡威机场", "兰卡威珍南海滩", "单程接机", null, "豪华4座", "Lexus ES", 120.0, "completed", "s8", "s4", 80.0, 0.0, 120.0, true, 0.0, 120.0, null, "2026-07-18 13:15", "吉打", "吉打", "+60 12-6789012", "lin_zhiling", created_at = "2026-07-18 08:30"),
    )

    private val mockProfiles = listOf(
        CustomerProfile("u101", "chenming", "陈明", "chen@email.com", "+60 12-1234567", "", "2026-06-01"),
        CustomerProfile("u102", "alicewong", "Alice Wong", "alice@email.com", "+60 12-2345678", "", "2026-06-15"),
        CustomerProfile("u103", "huade", "刘德华", "huade@email.com", "+60 12-4567890", "", "2026-05-20"),
        CustomerProfile("u104", "zhiling", "林志玲", "zhiling@email.com", "+60 12-6789012", "", "2026-07-01"),
        CustomerProfile("u105", "vikram", "Vikram Kumar", "vikram@email.com", "+60 12-7890123", "", "2026-04-10"),
        CustomerProfile("u106", "emma", "Emma Davis", "emma@email.com", "+60 12-8901234", "", "2026-07-05"),
    )

    private val mockChatMessages = listOf(
        ChatMessage("c1", "o1", "你好，我需要从机场接机到云顶高原，2人2行李", false, "2026-07-15 14:35"),
        ChatMessage("c2", "o1", "好的，已收到您的订单。我们会安排司机准时在机场接您。", true, "2026-07-15 14:40"),
        ChatMessage("c3", "o1", "谢谢！司机会举牌吗？", false, "2026-07-15 14:42"),
        ChatMessage("c4", "o1", "会的，司机会举牌写您的名字'陈明'，请留意。", true, "2026-07-15 14:45"),
        ChatMessage("c5", "o2", "请问从双子塔到马六甲大概需要多久？", false, "2026-07-16 09:20"),
        ChatMessage("c6", "o2", "大约2小时车程，中途可以在休息站停靠。", true, "2026-07-16 09:25"),
        ChatMessage("c7", "o2", "好的，我们要去鸡场街和红屋，司机知道路吗？", false, "2026-07-16 09:30"),
        ChatMessage("c8", "o2", "当然，我们司机对马六甲非常熟悉。已备注您的行程路线。", true, "2026-07-16 09:35"),
        ChatMessage("c9", "o4", "明天下午4点从新山CIQ出发，一定要准时", false, "2026-07-17 11:05"),
        ChatMessage("c10", "o4", "没问题，刘先生。VIP服务已安排妥当，司机会提前15分钟到达。", true, "2026-07-17 11:10"),
    )

    private val mockDutyRoster = listOf(
        DutyRoster("r1", "s4", true, "2026-07-18 06:00"),
        DutyRoster("r2", "s5", true, "2026-07-18 07:30"),
        DutyRoster("r3", "s6", true, "2026-07-18 08:00"),
        DutyRoster("r4", "s7", false, "2026-07-18 05:00"),
    )

    private val mockExpenses = listOf(
        DriverExpense("e1", "s4", "JKL 8888", "2026-07-17", "加油", "RM", 150.0, "Shell 95", "", "v1", "2026-07-17"),
        DriverExpense("e2", "s4", "JKL 8888", "2026-07-16", "过路费", "RM", 35.0, "PLUS Highway", "", "v1", "2026-07-16"),
        DriverExpense("e3", "s5", "JKL 6666", "2026-07-17", "保养", "SGD", 200.0, "Toyota Service Center", "", "v2", "2026-07-17"),
        DriverExpense("e4", "s6", "JKL 3333", "2026-07-15", "加油", "RM", 120.0, "Petronas 97", "", "v3", "2026-07-15"),
        DriverExpense("e5", "s7", "JKL 1111", "2026-07-18", "停车", "RM", 25.0, "Mid Valley Parking", "", "v4", "2026-07-18"),
    )

    private val mockFinancialRequests = listOf(
        FinancialRequest("fr1", "SV-20260715-001", "SJ", "s4", "王师傅", "DR", 280.0, "RM", "CASH", "", "", "", "", "completed", "司机薪资结算", "2026-07-15"),
        FinancialRequest("fr2", "SV-20260716-002", "SJ", "s5", "李师傅", "DR", 480.0, "RM", "BANK", "1234567890", "李师傅", "", "", "pending", "等待审计", "2026-07-16"),
        FinancialRequest("fr3", "", "BX", "s4", "王师傅", "DR", 150.0, "RM", "CASH", "", "", "", "", "completed", "加油报销", "2026-07-17"),
    )

    private val mockWalletLedger = listOf(
        WalletLedgerEntry("wl1", "2026-07-17 16:00", "s4", "staff", 280.0, "RM", "salary_credit", "completed", "SV-20260715-001", "王师傅薪资结算", 1780.0),
        WalletLedgerEntry("wl2", "2026-07-17 17:00", "s4", "staff", -150.0, "RM", "expense_debit", "completed", "e1", "加油扣除", 1630.0),
        WalletLedgerEntry("wl3", "2026-07-16 10:00", "s4", "staff", 350.0, "RM", "collect_rm", "completed", "s4", "现金收款 RM 350", 1630.0),
        WalletLedgerEntry("wl4", "2026-07-18 09:00", "s7", "staff", 480.0, "RM", "salary_credit", "completed", "SV-20260714-005", "张师傅薪资", 3680.0),
    )

    // ─── 内置管理员账号 ───
    private val demoAccounts = mapOf(
        "boss" to AdminUser("s1", "boss@xyzl.admin", "boss", "聪锕", AdminRole.DSZ, "", "+60 12-3456789", "boss_wechat"),
        "mishu" to AdminUser("s2", "chen@xyzl.admin", "mishu", "陈秘书", AdminRole.MS, "", "+60 13-1112223", "chen_wechat"),
        "kuaiji" to AdminUser("s3", "zhang@xyzl.admin", "kuaiji", "张会计", AdminRole.KJ, "", "+60 13-2223334", "zhang_wechat"),
        "zongguan" to AdminUser("s10", "wu@xyzl.admin", "zongguan", "吴总管", AdminRole.ZG, "", "+60 14-5556669", "wu_wechat"),
        "keji" to AdminUser("s8", "lin@xyzl.admin", "keji", "林客服", AdminRole.CF, "", "+60 14-5556667", "lin_wechat"),
        "guanjia" to AdminUser("s9", "huang@xyzl.admin", "guanjia", "黄管家", AdminRole.GJ, "", "+60 14-5556668", "huang_wechat"),
    )

    // ─── API 接口（全用模拟数据） ───

    suspend fun login(email: String, password: String): Result<AdminUser> {
        val normalizedUser = email.trim().split("@").first().lowercase()
        val user = demoAccounts[normalizedUser]
        return if (user != null) {
            isLoggedIn = true
            currentRole = user.role
            onAuthChanged?.invoke(true)
            Result.success(user)
        } else {
            Result.failure(Exception("账号认证失败，请检查账号密码。\n\n可用演示账号：boss / mishu / kuaiji / zongguan / keji / guanjia\n密码任意"))
        }
    }

    fun logout() {
        isLoggedIn = false
        onAuthChanged?.invoke(false)
    }

    // ─── Orders ───
    suspend fun fetchOrders(): List<Order> = mockOrders

    suspend fun updateOrderStatus(id: String, orderNo: String, status: String, extraFields: Map<String, String> = emptyMap()): Boolean {
        // 模拟更新（实际不做数据库写入）
        println("[Mock] 更新订单 $orderNo 状态: $status")
        return true
    }

    // ─── Profiles ───
    suspend fun fetchProfiles(): List<CustomerProfile> = mockProfiles

    // ─── Staff Profiles ───
    suspend fun fetchStaffProfiles(): List<StaffProfile> = mockStaff

    // ─── Chat Messages ───
    suspend fun fetchChatMessages(orderId: String? = null): List<ChatMessage> {
        return if (orderId != null) {
            mockChatMessages.filter { it.order_id == orderId }
        } else {
            mockChatMessages
        }
    }

    suspend fun sendAdminMessage(orderId: String, msg: String): Boolean {
        println("[Mock] 发送管理员消息到订单 $orderId: $msg")
        return true
    }

    // ─── Duty Roster ───
    suspend fun fetchDutyRoster(): List<DutyRoster> = mockDutyRoster

    // ─── Driver Expenses ───
    suspend fun fetchDriverExpenses(): List<DriverExpense> = mockExpenses

    // ─── Financial Requests ───
    suspend fun fetchFinancialRequests(): List<FinancialRequest> = mockFinancialRequests

    // ─── Wallet Ledger ───
    suspend fun fetchWalletLedger(): List<WalletLedgerEntry> = mockWalletLedger
}
