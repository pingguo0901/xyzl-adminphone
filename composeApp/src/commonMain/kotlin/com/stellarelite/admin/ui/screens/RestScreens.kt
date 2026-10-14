package com.stellarelite.admin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellarelite.admin.model.ApplicantRole
import com.stellarelite.admin.model.RestApplication
import com.stellarelite.admin.model.RestStatus
import com.stellarelite.admin.model.RestStore
import com.stellarelite.admin.model.RestType
import com.stellarelite.admin.model.dateInRange
import com.stellarelite.admin.model.isoToDateDisplay
import com.stellarelite.admin.model.millisToDateDisplay
import com.stellarelite.admin.model.monthStartIso
import com.stellarelite.admin.model.restDaysBetween
import com.stellarelite.admin.platform.currentTimeParts
import com.stellarelite.admin.ui.components.FormPrimaryButton
import com.stellarelite.admin.ui.components.SegmentedTabs
import com.stellarelite.admin.ui.theme.AdminColors

private val appFilters = listOf("全部", "请假", "放假", "已通过", "待通过", "已驳回", "已取消")

/** 查看申请：日期范围（胶囊+原生日历）+ 合并式筛选 + 申请列表（复刻司机端） */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestAppsScreen(onBack: () -> Unit = {}, viewerIsBoss: Boolean = true) {
    var selectedId by remember { mutableStateOf<String?>(null) }

    val current = RestStore.apps.firstOrNull { it.id == selectedId }
    if (current != null) {
        RestAppDetailScreen(
            app = current,
            viewerIsBoss = viewerIsBoss,
            onBack = { selectedId = null },
            onDone = { selectedId = null },
        )
        return
    }

    val todayIso = remember { currentTimeParts().first }
    var rangeStart by remember { mutableStateOf(isoToDateDisplay(monthStartIso(todayIso))) }
    var rangeEnd by remember { mutableStateOf(isoToDateDisplay(todayIso)) }
    var showPicker by remember { mutableStateOf(false) }
    var filter by remember { mutableStateOf(0) }

    val filtered = RestStore.apps.filter { a ->
        val byDate = dateInRange(a.appliedDate, rangeStart, rangeEnd)
        val byType = when (filter) {
            1 -> a.type == RestType.LEAVE
            2 -> a.type == RestType.HOLIDAY
            else -> true
        }
        val byStatus = when (filter) {
            3 -> a.status == RestStatus.APPROVED
            4 -> a.status == RestStatus.PENDING
            5 -> a.status == RestStatus.REJECTED
            6 -> a.status == RestStatus.CANCELLED
            else -> true
        }
        byDate && byType && byStatus
    }

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
                "查看申请",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = AdminColors.TextPrimary,
                modifier = Modifier.align(Alignment.Center),
            )
        }

        Spacer(Modifier.height(14.dp))

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

        Spacer(Modifier.height(12.dp))
        SegmentedTabs(options = appFilters, selectedIndex = filter, onSelect = { filter = it })
        Spacer(Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(filtered, key = { it.id }) { app ->
                RestAppRow(app, onClick = { selectedId = app.id })
            }
        }
    }

    if (showPicker) {
        val st = rememberDateRangePickerState()
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    st.selectedStartDateMillis?.let { rangeStart = millisToDateDisplay(it) }
                    st.selectedEndDateMillis?.let { rangeEnd = millisToDateDisplay(it) }
                    showPicker = false
                }) { Text("确定", color = AdminColors.Primary, fontWeight = FontWeight.SemiBold) }
            },
            dismissButton = { TextButton(onClick = { showPicker = false }) { Text("取消", color = AdminColors.TextMuted) } },
        ) {
            DateRangePicker(state = st, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun RestAppRow(app: RestApplication, onClick: () -> Unit) {
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RestTypeBadge(app.type)
                    Spacer(Modifier.width(8.dp))
                    Text(app.role.label + " · " + app.applicant + " · 申请 " + app.appliedDate, fontSize = 12.sp, color = AdminColors.TextMuted)
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    "休息 " + app.startDate + " - " + app.endDate,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AdminColors.TextPrimary,
                )
                Spacer(Modifier.height(4.dp))
                Text(app.days.toString() + " 天 · " + app.reason, fontSize = 12.sp, color = AdminColors.TextMuted, maxLines = 1)
            }
            Spacer(Modifier.width(8.dp))
            RestStatusBadge(app.status)
            Spacer(Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = AdminColors.TextMuted,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

/**
 * 申请详情：
 * - 自己的申请（内部员工自己）→ 与司机端一致：撤销申请 + 完成
 * - 审批他人：司机申请 → 内部员工 / 老板都可 驳回 / 批准；
 *   内部员工申请 → 只有老板可 驳回 / 批准；自己/同职位不能审批自己的申请
 */
@Composable
fun RestAppDetailScreen(
    app: RestApplication,
    viewerIsBoss: Boolean = true,
    onBack: () -> Unit = {},
    onDone: () -> Unit = {},
) {
    val isSelf = app.applicant == "我"
    val canApprove = !isSelf && when (app.role) {
        ApplicantRole.DRIVER -> true                                     // 司机申请：内部员工 / 老板都可
        ApplicantRole.EMPLOYEE -> viewerIsBoss                           // 内部员工申请：只有老板
    }
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
                "申请详情",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = AdminColors.TextPrimary,
                modifier = Modifier.align(Alignment.Center),
            )
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
                    RestDetailRow("申请人", app.applicant + "（" + app.role.label + "）")
                    RestDetailDivider()
                    RestDetailRow("申请日期", app.appliedDate)
                    RestDetailDivider()
                    RestDetailRow("休息日期", app.startDate)
                    RestDetailDivider()
                    RestDetailRow("结束日期", app.endDate)
                    RestDetailDivider()
                    RestDetailRow("原因", app.reason)
                    RestDetailDivider()
                    RestDetailRow("申请休息天数", app.days.toString() + " 天")
                    RestDetailDivider()
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("状态", fontSize = 14.sp, color = AdminColors.TextMuted, modifier = Modifier.weight(1f))
                        RestStatusBadge(app.status)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        when {
            canApprove && app.status == RestStatus.PENDING -> {
                Row(Modifier.fillMaxWidth()) {
                    Box(
                        Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, AdminColors.Danger, RoundedCornerShape(16.dp))
                            .clickable {
                                RestStore.reject(app.id)
                                onDone()
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("驳回申请", color = AdminColors.Danger, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.width(12.dp))
                    Box(
                        Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(AdminColors.Primary)
                            .clickable {
                                RestStore.approve(app.id)
                                onDone()
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("批准申请", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            isSelf && app.status == RestStatus.PENDING -> {
                Row(Modifier.fillMaxWidth()) {
                    Box(
                        Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, AdminColors.Danger, RoundedCornerShape(16.dp))
                            .clickable {
                                RestStore.cancel(app.id)
                                onDone()
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("撤销申请", color = AdminColors.Danger, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.width(12.dp))
                    Box(
                        Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(AdminColors.Primary)
                            .clickable { onDone() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("完成", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            else -> FormPrimaryButton(text = "完成", onClick = onDone)
        }
    }
}

/** 申请休息：休息/结束日期（胶囊+原生日历）+ 原因 + 自动算天数 + 取消/确认 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestApplyScreen(
    onBack: () -> Unit = {},
    onConfirm: (RestApplication) -> Unit = {},
) {
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    var picking by remember { mutableStateOf<Int?>(null) }

    val days = if (startDate.isNotEmpty() && endDate.isNotEmpty()) restDaysBetween(startDate, endDate) else 0

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
                "申请休息",
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold,
                color = AdminColors.TextPrimary,
                modifier = Modifier.align(Alignment.Center),
            )
        }

        Spacer(Modifier.height(16.dp))

        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                DateCapsule(
                    label = if (startDate.isEmpty()) "休息日期" else startDate,
                    modifier = Modifier.weight(1f),
                    onClick = { picking = 0 },
                )
                DateCapsule(
                    label = if (endDate.isEmpty()) "结束日期" else endDate,
                    modifier = Modifier.weight(1f),
                    onClick = { picking = 1 },
                )
            }

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("请填写申请原因", color = AdminColors.TextMuted) },
                shape = RoundedCornerShape(18.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AdminColors.Primary,
                    unfocusedBorderColor = AdminColors.SurfaceVariant,
                    focusedTextColor = AdminColors.TextPrimary,
                    unfocusedTextColor = AdminColors.TextPrimary,
                ),
                minLines = 3,
            )

            Spacer(Modifier.height(14.dp))
            Text(
                "申请休息天数：" + days.toString() + " 天",
                fontSize = 13.sp,
                color = AdminColors.TextMuted,
            )
            Spacer(Modifier.height(16.dp))
        }

        Row(Modifier.fillMaxWidth()) {
            Box(
                Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, AdminColors.Border, RoundedCornerShape(16.dp))
                    .clickable { onBack() },
                contentAlignment = Alignment.Center,
            ) {
                Text("取消", color = AdminColors.TextMuted, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.width(12.dp))
            Box(
                Modifier
                    .weight(1f)
                    .height(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (startDate.isEmpty() || endDate.isEmpty() || days <= 0) AdminColors.SurfaceVariant else AdminColors.Primary)
                    .clickable(enabled = startDate.isNotEmpty() && endDate.isNotEmpty() && days > 0) {
                        onConfirm(
                            RestApplication(
                                id = RestStore.nextId(),
                                applicant = "我",
                                role = ApplicantRole.EMPLOYEE,
                                appliedDate = isoToDateDisplay(currentTimeParts().first),
                                startDate = startDate,
                                endDate = endDate,
                                reason = reason.ifBlank { "未填写原因" },
                                days = days,
                                type = RestType.LEAVE,
                                status = RestStatus.PENDING,
                            )
                        )
                    },
                contentAlignment = Alignment.Center,
            ) {
                Text("确认", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }

    val target = picking
    if (target != null) {
        val st = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { picking = null },
            confirmButton = {
                TextButton(onClick = {
                    st.selectedDateMillis?.let { millis ->
                        val d = millisToDateDisplay(millis)
                        if (target == 0) {
                            startDate = d
                            if (endDate.isNotEmpty() && restDaysBetween(startDate, endDate) <= 0) endDate = d
                        } else {
                            endDate = d
                        }
                    }
                    picking = null
                }) { Text("确定", color = AdminColors.Primary, fontWeight = FontWeight.SemiBold) }
            },
            dismissButton = { TextButton(onClick = { picking = null }) { Text("取消", color = AdminColors.TextMuted) } },
        ) {
            DatePicker(state = st)
        }
    }
}

@Composable
private fun DateCapsule(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(AdminColors.Card)
            .clickable { onClick() }
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Filled.CalendarMonth,
            contentDescription = null,
            tint = AdminColors.Primary,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(label, fontSize = 14.sp, color = AdminColors.TextPrimary)
    }
}

@Composable
private fun RestDetailRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, fontSize = 14.sp, color = AdminColors.TextMuted, modifier = Modifier.weight(1f))
        Text(
            value,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = AdminColors.TextPrimary,
            modifier = Modifier.weight(1.4f),
            textAlign = TextAlign.End,
        )
    }
}

@Composable
private fun RestDetailDivider() {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp)
            .height(1.dp)
            .background(AdminColors.SurfaceVariant),
    )
}

@Composable
private fun RestTypeBadge(type: RestType) {
    val c = if (type == RestType.LEAVE) AdminColors.Primary else AdminColors.Success
    Box(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(c.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 2.dp),
    ) {
        Text(type.label, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = c)
    }
}

@Composable
private fun RestStatusBadge(status: RestStatus) {
    val c = when (status) {
        RestStatus.PENDING -> AdminColors.Warning
        RestStatus.APPROVED -> AdminColors.Success
        RestStatus.REJECTED -> AdminColors.Danger
        RestStatus.CANCELLED -> AdminColors.TextMuted
    }
    Box(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(c.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 3.dp),
    ) {
        Text(status.label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = c)
    }
}
