package com.stellarelite.admin.model

import kotlin.math.roundToInt

/** 一条值班记录（每日结算） */
data class DutyRecord(
    val id: String,
    val name: String,          // 值班人（内部员工姓名 / 司机姓名）
    val date: String,          // 14/10/2026
    val salary: Double,        // 今日薪资 (RM)
    val reimbursement: Double, // 今日报销 (RM)
    val orders: Int,           // 今日单数
    val collectSgd: Double,    // 今日代收 SGD
    val collectMyr: Double,    // 今日代收 MYR
)

/** 司机当月额度（跨境 / 休息，当月不累计） */
data class DriverQuota(
    val name: String,
    val crossUsed: Int,
    val crossRemaining: Int,
    val restUsed: Int,
    val restRemaining: Int,
)

object DutyData {

    /** 每个司机的当月跨境 / 休息额度 */
    val driverQuotas = listOf(
        DriverQuota("张师傅", 8, 7, 3, 3),
        DriverQuota("王师傅", 5, 10, 2, 4),
        DriverQuota("Ahmad", 11, 4, 1, 5),
    )
    /** 内部员工值班记录（含自己） */
    val internalRecords = listOf(
        DutyRecord("I001", "我", "14/10/2026", 120.00, 18.50, 6, 320.00, 450.00),
        DutyRecord("I002", "我", "13/10/2026", 135.00, 0.00, 7, 210.00, 620.00),
        DutyRecord("I003", "陈主管", "14/10/2026", 140.00, 20.00, 5, 260.00, 380.00),
        DutyRecord("I004", "李调度", "14/10/2026", 130.00, 10.00, 4, 150.00, 260.00),
        DutyRecord("I005", "陈主管", "13/10/2026", 128.00, 0.00, 6, 300.00, 410.00),
    )

    /** 司机值班记录 */
    val driverRecords = listOf(
        DutyRecord("D001", "张师傅", "14/10/2026", 120.00, 18.50, 6, 320.00, 450.00),
        DutyRecord("D002", "王师傅", "14/10/2026", 150.00, 12.00, 8, 540.00, 210.00),
        DutyRecord("D003", "Ahmad", "14/10/2026", 110.00, 0.00, 6, 0.00, 780.00),
        DutyRecord("D004", "张师傅", "13/10/2026", 135.00, 0.00, 7, 210.00, 620.00),
        DutyRecord("D005", "李师傅", "13/10/2026", 126.00, 8.00, 7, 260.00, 410.00),
    )
}

/** SGD 金额格式化（commonMain 无 String.format） */
fun formatSgd(amount: Double): String {
    val cents = (amount * 100).roundToInt()
    return "S\$ " + (cents / 100) + "." + (cents % 100).toString().padStart(2, '0')
}

/** RM 金额格式化 */
fun formatRinggit(amount: Double): String {
    val cents = (amount * 100).roundToInt()
    return "RM " + (cents / 100) + "." + (cents % 100).toString().padStart(2, '0')
}
