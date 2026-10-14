package com.stellarelite.admin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellarelite.admin.model.DriverQuota
import com.stellarelite.admin.model.DutyData
import com.stellarelite.admin.model.DutyRecord
import com.stellarelite.admin.model.RestStore
import com.stellarelite.admin.model.formatRinggit
import com.stellarelite.admin.model.formatSgd
import com.stellarelite.admin.model.isoToDateDisplay
import com.stellarelite.admin.model.millisToDateDisplay
import com.stellarelite.admin.model.monthStartIso
import com.stellarelite.admin.platform.currentTimeParts
import com.stellarelite.admin.platform.saveStatementPdf
import com.stellarelite.admin.ui.components.SegmentedTabs
import com.stellarelite.admin.ui.theme.AdminColors

/**
 * 我的值班：
 * - 值班记录两组（公司老板：内部员工 / 司机；内部员工：我的 / 司机的）
 * - 公司内部员工：右上角「查看申请」+ 底部「申请休息」
 * - 不再有司机的「休息额度 / 跨境额度」卡片
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DutyScreen(
    onBack: () -> Unit = {},
    isBoss: Boolean = true,
) {
    var selected by remember { mutableStateOf<DutyRecord?>(null) }
    var showApply by remember { mutableStateOf(false) }
    var showApps by remember { mutableStateOf(false) }

    val current = selected
    if (current != null) {
        DutyDetailScreen(record = current, onBack = { selected = null }, onDone = { selected = null })
        return
    }
    if (showApply) {
        RestApplyScreen(
            onBack = { showApply = false },
            onConfirm = { app ->
                RestStore.add(app)
                showApply = false
            },
        )
        return
    }
    if (showApps) {
        RestAppsScreen(onBack = { showApps = false })
        return
    }

    val todayIso = remember { currentTimeParts().first }
    var rangeStart by remember { mutableStateOf(isoToDateDisplay(monthStartIso(todayIso))) }
    var rangeEnd by remember { mutableStateOf(isoToDateDisplay(todayIso)) }
    var showPicker by remember { mutableStateOf(false) }
    var group by remember { mutableStateOf(0) }

    val groupLabels = if (isBoss) listOf("内部员工", "司机") else listOf("我的", "司机的")
    val records = if (group == 0) DutyData.internalRecords else DutyData.driverRecords

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        // 顶栏：返回 + 标题 + 右上角「查看申请」
        Box(Modifier.fillMaxWidth().height(44.dp)) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AdminColors.SurfaceVariant)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "返回",
                    tint = AdminColors.TextPrimary,
                    modifier = Modifier.size(20.dp),
                )
            }
            Text(
                "我的值班",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = AdminColors.TextPrimary,
                modifier = Modifier.align(Alignment.Center),
            )
            Row(
                Modifier
                    .align(Alignment.CenterEnd)
                    .clip(RoundedCornerShape(50))
                    .background(AdminColors.SurfaceVariant)
                    .clickable { showApps = true }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("查看申请", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = AdminColors.Primary)
            }
        }

        Spacer(Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Row(
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .background(AdminColors.SurfaceVariant)
                        .clickable { showPicker = true }
                        .padding(horizontal = 16.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Filled.CalendarMonth,
                        contentDescription = null,
                        tint = AdminColors.Primary,
                        modifier = Modifier.size(18.dp),
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        rangeStart + "  -  " + rangeEnd,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = AdminColors.TextPrimary,
                    )
                }
            }

            item {
                Text(
                    "司机当月额度（跨境 / 休息）",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AdminColors.TextMuted,
                    modifier = Modifier.padding(start = 6.dp, top = 6.dp),
                )
            }

            items(DutyData.driverQuotas, key = { it.name }) { quota ->
                DriverQuotaCard(quota)
            }

            item {
                Text(
                    "值班记录",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AdminColors.TextMuted,
                    modifier = Modifier.padding(start = 6.dp, top = 6.dp),
                )
            }

            item {
                SegmentedTabs(options = groupLabels, selectedIndex = group, onSelect = { group = it })
            }

            items(records, key = { it.id }) { record ->
                DutyRecordRow(record, onClick = { selected = record })
            }
        }

        Spacer(Modifier.height(12.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(AdminColors.Primary)
                .clickable { showApply = true },
            contentAlignment = Alignment.Center,
        ) {
            Text("申请休息", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }

    if (showPicker) {
        val pickerState = rememberDateRangePickerState()
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedStartDateMillis?.let { rangeStart = millisToDateDisplay(it) }
                    pickerState.selectedEndDateMillis?.let { rangeEnd = millisToDateDisplay(it) }
                    showPicker = false
                }) { Text("确定", color = AdminColors.Primary, fontWeight = FontWeight.SemiBold) }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("取消", color = AdminColors.TextMuted) }
            },
        ) {
            DateRangePicker(state = pickerState, modifier = Modifier.weight(1f))
        }
    }
}

/** 单个司机当月额度卡：跨境 / 休息（当月，不累计） */
@Composable
private fun DriverQuotaCard(quota: DriverQuota) {
    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AdminColors.Card),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(quota.name, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = AdminColors.TextPrimary)
                Spacer(Modifier.weight(1f))
                Text("当月", fontSize = 11.sp, color = AdminColors.TextMuted)
            }
            Spacer(Modifier.height(10.dp))
            Row {
                QuotaLine("已跨境", quota.crossUsed.toString() + " 天", "剩 " + quota.crossRemaining.toString() + " 天", AdminColors.Primary, Modifier.weight(1f))
                QuotaLine("已休息", quota.restUsed.toString() + " 天", "剩 " + quota.restRemaining.toString() + " 天", AdminColors.Success, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun QuotaLine(label: String, used: String, remain: String, accent: Color, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(label, fontSize = 11.sp, color = AdminColors.TextMuted)
        Spacer(Modifier.height(2.dp))
        Text(used, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = accent)
        Spacer(Modifier.height(2.dp))
        Text(remain, fontSize = 11.sp, color = AdminColors.TextMuted)
    }
}

@Composable
private fun DutyRecordRow(record: DutyRecord, onClick: () -> Unit) {
    Card(
        Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AdminColors.Card),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(record.date, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = AdminColors.TextPrimary)
                Spacer(Modifier.height(4.dp))
                Text(
                    record.name + " · " + record.orders.toString() + " 单 · 薪资 " + formatRinggit(record.salary),
                    fontSize = 12.sp,
                    color = AdminColors.TextMuted,
                )
            }
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = AdminColors.TextMuted,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

/** 值班详情：左上返回 / 右上打印 PDF / 当日结算明细 / 底部完成（复刻司机端） */
@Composable
fun DutyDetailScreen(
    record: DutyRecord,
    onBack: () -> Unit = {},
    onDone: () -> Unit = {},
) {
    var savedInfo by remember { mutableStateOf<String?>(null) }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Box(Modifier.fillMaxWidth().height(44.dp)) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AdminColors.SurfaceVariant)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "返回",
                    tint = AdminColors.TextPrimary,
                    modifier = Modifier.size(20.dp),
                )
            }
            Text(
                "值班详情",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = AdminColors.TextPrimary,
                modifier = Modifier.align(Alignment.Center),
            )
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AdminColors.SurfaceVariant)
                    .clickable {
                        val ok = saveStatementPdf(dutyPdfFileName(record), dutyPdfText(record))
                        savedInfo = if (ok) "值班详情 PDF 已生成并保存到手机（下载/XYZL）" else "保存失败，请检查存储权限后重试"
                    },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Print,
                    contentDescription = "打印 PDF",
                    tint = AdminColors.Primary,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AdminColors.Card),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Column(Modifier.padding(20.dp)) {
                    DetailRow("值班人", record.name)
                    DetailDivider()
                    DetailRow("日期", record.date)
                    DetailDivider()
                    DetailRow("今日薪资", formatRinggit(record.salary))
                    DetailDivider()
                    DetailRow("今日报销", formatRinggit(record.reimbursement))
                    DetailDivider()
                    DetailRow("今日单数", record.orders.toString() + " 单")
                    DetailDivider()
                    DetailRow("今日代收SGD", formatSgd(record.collectSgd))
                    DetailDivider()
                    DetailRow("今日代收MYR", formatRinggit(record.collectMyr))
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        Box(
            Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(AdminColors.Primary)
                .clickable { onDone() },
            contentAlignment = Alignment.Center,
        ) {
            Text("完成", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }

    val info = savedInfo
    if (info != null) {
        AlertDialog(
            onDismissRequest = { savedInfo = null },
            containerColor = AdminColors.Card,
            title = { Text("值班详情", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = AdminColors.TextPrimary) },
            text = { Text(info, fontSize = 14.sp, color = AdminColors.TextMuted) },
            confirmButton = { TextButton(onClick = { savedInfo = null }) { Text("确定", color = AdminColors.Primary) } },
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, fontSize = 14.sp, color = AdminColors.TextMuted, modifier = Modifier.weight(1f))
        Text(value, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = AdminColors.TextPrimary)
    }
}

@Composable
private fun DetailDivider() {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp)
            .height(1.dp)
            .background(AdminColors.SurfaceVariant),
    )
}

private fun dutyPdfFileName(record: DutyRecord): String {
    val d = record.date.replace("/", "")
    return "值班详情_" + d + ".pdf"
}

private fun dutyPdfText(record: DutyRecord): String {
    val sb = StringBuilder()
    sb.appendLine("星域臻旅 后台端 · 值班详情")
    sb.appendLine("值班人：" + record.name)
    sb.appendLine("日期：" + record.date)
    sb.appendLine("--------------------------------------------------")
    sb.appendLine("今日薪资：" + formatRinggit(record.salary))
    sb.appendLine("今日报销：" + formatRinggit(record.reimbursement))
    sb.appendLine("今日单数：" + record.orders + " 单")
    sb.appendLine("今日代收SGD：" + formatSgd(record.collectSgd))
    sb.appendLine("今日代收MYR：" + formatRinggit(record.collectMyr))
    sb.appendLine("--------------------------------------------------")
    val now = currentTimeParts()
    sb.appendLine("生成时间：" + now.first + " " + now.second)
    return sb.toString()
}
