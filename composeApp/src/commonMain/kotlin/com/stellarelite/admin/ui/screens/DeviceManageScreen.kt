package com.stellarelite.admin.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.stellarelite.admin.ui.components.FormDivider
import com.stellarelite.admin.ui.components.FormPageHeaderAction
import com.stellarelite.admin.ui.components.FormPrimaryButton
import com.stellarelite.admin.ui.components.FormSectionTitle
import com.stellarelite.admin.ui.components.SubPageHeader
import com.stellarelite.admin.ui.theme.AdminColors

/** 登录设备行（本机演示数据） */
private data class DeviceRow(
    val id: String,
    val name: String,
    val osName: String?,
    val deviceType: String?,
    val lastLoginAt: String?,
    val isActive: Boolean,
)

private fun deviceTypeLine(device: DeviceRow): String {
    val os = device.osName?.takeIf { it.isNotBlank() }
    val type = device.deviceType?.takeIf { it.isNotBlank() }
    return listOfNotNull(os, type).joinToString(" · ").ifBlank { "-" }
}

private fun prettyLogin(raw: String?): String = raw?.takeIf { it.isNotBlank() } ?: "-"

/** 登录设备管理页：已登录 / 已退出（复刻司机端，本页仅本地状态） */
@Composable
fun DeviceManageScreen(onBack: () -> Unit = {}) {
    var active by remember {
        mutableStateOf(
            listOf(
                DeviceRow("d1", "iPhone 15 Pro", "iOS 18.0", "手机", "2026-10-16 19:12", true),
                DeviceRow("d2", "iPad Air", "iPadOS 18.0", "平板", "2026-10-14 09:05", true),
            )
        )
    }
    var inactive by remember {
        mutableStateOf(
            listOf(
                DeviceRow("d3", "Xiaomi 14", "Android 15", "手机", "2026-10-08 21:40", false),
            )
        )
    }
    var editing by remember { mutableStateOf(false) }
    var revealed by remember { mutableStateOf<String?>(null) }
    var detail by remember { mutableStateOf<DeviceRow?>(null) }
    var pending by remember { mutableStateOf<DeviceRow?>(null) }

    fun logoutDevice(id: String) {
        val target = active.firstOrNull { it.id == id } ?: return
        active = active.filterNot { it.id == id }
        inactive = listOf(target.copy(isActive = false)) + inactive
    }

    fun removeDevice(id: String) {
        inactive = inactive.filterNot { it.id == id }
    }

    val current = detail
    if (current != null) {
        DeviceDetailScreen(
            device = current,
            onBack = { detail = null },
            onLogout = { pending = current },
        )
        return
    }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeaderAction(
            title = "登录设备管理",
            actionLabel = if (editing) "完成" else "编辑",
            onBack = onBack,
            onAction = {
                editing = !editing
                if (!editing) revealed = null
            },
        )
        Spacer(Modifier.height(16.dp))

        FormSectionTitle("已登录的设备")
        if (active.isEmpty()) {
            Text("暂无已登录设备", fontSize = 13.sp, color = AdminColors.TextMuted, modifier = Modifier.padding(start = 2.dp, bottom = 12.dp))
        } else {
            active.forEach { device ->
                DeviceRowView(
                    device = device,
                    editing = editing,
                    revealed = revealed == device.id,
                    actionLabel = "退出",
                    actionColor = AdminColors.Danger,
                    onClick = { if (!editing) detail = device },
                    onMinus = { revealed = if (revealed == device.id) null else device.id },
                    onAction = { pending = device },
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        FormSectionTitle("已退出的设备")
        if (inactive.isEmpty()) {
            Text("暂无已退出设备", fontSize = 13.sp, color = AdminColors.TextMuted, modifier = Modifier.padding(start = 2.dp, bottom = 12.dp))
        } else {
            inactive.forEach { device ->
                DeviceRowView(
                    device = device,
                    editing = editing,
                    revealed = revealed == device.id,
                    actionLabel = "删除",
                    actionColor = AdminColors.Danger,
                    onClick = { if (!editing) detail = device },
                    onMinus = { revealed = if (revealed == device.id) null else device.id },
                    onAction = { pending = device },
                )
            }
        }

        Spacer(Modifier.height(110.dp))
    }

    val target = pending
    if (target != null) {
        val isActive = target.isActive
        AlertDialog(
            onDismissRequest = { pending = null },
            containerColor = AdminColors.Card,
            title = {
                Text(
                    if (isActive) "退出该设备" else "删除设备记录",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AdminColors.TextPrimary,
                )
            },
            text = {
                Text(
                    if (isActive) "退出后，下次在该设备登录时需要进行安全验证。" else "删除后这条记录不再显示。",
                    fontSize = 14.sp,
                    color = AdminColors.TextMuted,
                    lineHeight = 20.sp,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val id = target.id
                    pending = null
                    revealed = null
                    detail = null
                    if (isActive) logoutDevice(id) else removeDevice(id)
                }) {
                    Text(if (isActive) "退出" else "删除", color = AdminColors.Danger, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { pending = null }) {
                    Text("取消", color = AdminColors.TextMuted)
                }
            },
        )
    }
}

/** 设备行：编辑态左侧 - 按钮，点 - 后卡片左滑露出右侧操作按钮 */
@Composable
private fun DeviceRowView(
    device: DeviceRow,
    editing: Boolean,
    revealed: Boolean,
    actionLabel: String,
    actionColor: Color,
    onClick: () -> Unit,
    onMinus: () -> Unit,
    onAction: () -> Unit,
) {
    val offset by animateDpAsState(
        targetValue = if (revealed) (-88).dp else 0.dp,
        label = "deviceOffset",
    )

    Box(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .height(64.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(actionColor),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(88.dp)
                .fillMaxHeight()
                .clickable { onAction() },
            contentAlignment = Alignment.Center,
        ) {
            Text(actionLabel, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .offset(x = offset)
                .clip(RoundedCornerShape(16.dp))
                .background(AdminColors.Card)
                .clickable { onClick() }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (editing) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(AdminColors.Danger)
                        .clickable { onMinus() },
                    contentAlignment = Alignment.Center,
                ) {
                    Text("−", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(12.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(device.name, fontSize = 15.sp, color = AdminColors.TextPrimary, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(2.dp))
                Text(deviceTypeLine(device), fontSize = 12.sp, color = AdminColors.TextMuted)
            }
            Text(prettyLogin(device.lastLoginAt), fontSize = 12.sp, color = AdminColors.TextMuted)
        }
    }
}

/** 设备详情页 */
@Composable
private fun DeviceDetailScreen(
    device: DeviceRow,
    onBack: () -> Unit,
    onLogout: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        SubPageHeader("设备详情", onBack = onBack)
        Spacer(Modifier.height(16.dp))

        Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = AdminColors.Card),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                DetailRow("设备名称", device.name)
                FormDivider()
                DetailRow("系统版本", device.osName ?: "-")
                FormDivider()
                DetailRow("设备类型", device.deviceType ?: "-")
                FormDivider()
                DetailRow("最后登录时间", prettyLogin(device.lastLoginAt))
                FormDivider()
                DetailRow("状态", if (device.isActive) "已登录" else "已退出")
            }
        }

        Spacer(Modifier.height(24.dp))
        if (device.isActive) {
            FormPrimaryButton(text = "退出该设备", onClick = onLogout, danger = true)
        }
        Spacer(Modifier.height(110.dp))
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, fontSize = 14.sp, color = AdminColors.TextMuted, modifier = Modifier.weight(1f))
        Text(value, fontSize = 15.sp, color = AdminColors.TextPrimary, fontWeight = FontWeight.Medium)
    }
}
