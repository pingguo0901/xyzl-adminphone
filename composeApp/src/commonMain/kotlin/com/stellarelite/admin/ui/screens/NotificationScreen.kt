package com.stellarelite.admin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellarelite.admin.ui.theme.AdminColors

private enum class NotifType(val label: String) {
    CHAT("聊天通知"), OFFICIAL("官方通知")
}

private data class NotifItem(
    val id: Int,
    val icon: ImageVector,
    val title: String,
    val content: String,
    val time: String,
    val type: NotifType
)

private val mockNotifications = listOf(
    NotifItem(1, Icons.AutoMirrored.Outlined.Chat, "张师傅发来消息", "收到，马上出发前往上车点", "10:24", NotifType.CHAT),
    NotifItem(2, Icons.Outlined.Campaign, "新订单待派车", "SG → JB 新订单，请及时派车", "10:02", NotifType.OFFICIAL),
    NotifItem(3, Icons.AutoMirrored.Outlined.Chat, "李管家发来消息", "行程已确认，司机信息同步中", "09:58", NotifType.CHAT),
    NotifItem(4, Icons.Outlined.Campaign, "明日排班已更新", "请查看明日接送排班表", "昨天", NotifType.OFFICIAL),
    NotifItem(5, Icons.AutoMirrored.Outlined.Chat, "王客服发来消息", "客户已付款，请核对订单", "09:30", NotifType.CHAT),
    NotifItem(6, Icons.Outlined.Campaign, "系统维护通知", "今晚 23:00-01:00 系统升级", "周一", NotifType.OFFICIAL)
)

@Composable
fun NotificationScreen(onBack: () -> Unit) {
    var filter by remember { mutableStateOf("全部") }

    Column(modifier = Modifier.fillMaxSize()) {
        // 顶部标题栏（返回 + 标题）
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "返回",
                tint = AdminColors.TextPrimary,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onBack() }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("通知", color = AdminColors.TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Black)
        }

        // 胶囊式筛选
        NotificationFilterBar(
            options = listOf("全部", "聊天通知", "官方通知"),
            selected = filter,
            onSelect = { filter = it }
        )

        // 消息列表
        val filtered = when (filter) {
            "聊天通知" -> mockNotifications.filter { it.type == NotifType.CHAT }
            "官方通知" -> mockNotifications.filter { it.type == NotifType.OFFICIAL }
            else -> mockNotifications
        }

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filtered) { item ->
                NotificationRow(item)
            }
        }
    }
}

@Composable
private fun NotificationFilterBar(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { opt ->
            val isSelected = selected == opt
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) AdminColors.Primary else AdminColors.SurfaceVariant)
                    .clickable { onSelect(opt) }
                    .padding(horizontal = 16.dp, vertical = 7.dp)
            ) {
                Text(
                    opt,
                    color = if (isSelected) androidx.compose.ui.graphics.Color.White else AdminColors.TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun NotificationRow(item: NotifItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AdminColors.Card)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(21.dp))
                .background(AdminColors.SurfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(item.icon, contentDescription = null, tint = AdminColors.Primary, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.title, color = AdminColors.TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(item.content, color = AdminColors.TextMuted, fontSize = 12.sp, maxLines = 1)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(item.time, color = AdminColors.TextMuted, fontSize = 11.sp)
    }
}
