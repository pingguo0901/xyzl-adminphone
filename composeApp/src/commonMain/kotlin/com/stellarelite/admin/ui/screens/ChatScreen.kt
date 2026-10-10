package com.stellarelite.admin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Warning
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
import com.stellarelite.admin.ui.components.FilterChipBar
import com.stellarelite.admin.ui.components.PageTitle
import com.stellarelite.admin.ui.theme.AdminColors

private data class ChatItem(val icon: ImageVector, val name: String, val lastMsg: String, val time: String)

@Composable
fun ChatScreen(onBellClick: () -> Unit = {}) {
    var filter by remember { mutableStateOf("私聊") }

    Column(modifier = Modifier.fillMaxSize()) {
        PageTitle("聊天", onBellClick = onBellClick)
        FilterChipBar(
            options = listOf("私聊", "收单", "丢单", "交通"),
            selected = filter,
            onSelect = { filter = it }
        )
        val chats = when (filter) {
            "私聊" -> listOf(
                ChatItem(Icons.Outlined.Person, "张师傅", "收到，马上出发", "10:24"),
                ChatItem(Icons.Outlined.Person, "李管家", "行程已确认", "09:58"),
                ChatItem(Icons.Outlined.Person, "王客服", "客户已付款", "09:30")
            )
            "收单" -> listOf(
                ChatItem(Icons.AutoMirrored.Outlined.ReceiptLong, "新订单", "SG → JB 新订单待接", "10:02")
            )
            "丢单" -> listOf(
                ChatItem(Icons.Outlined.Warning, "丢单提醒", "订单 A-2025 超时未处理", "09:15")
            )
            else -> listOf(
                ChatItem(Icons.Outlined.LocalShipping, "交通信息", "关卡拥堵，预计延误 20 分钟", "08:40")
            )
        }
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chats) { chat ->
                ChatRow(chat)
            }
        }
    }
}

@Composable
private fun ChatRow(item: ChatItem) {
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
            Icon(item.icon, contentDescription = null, tint = AdminColors.TextPrimary, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, color = AdminColors.TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(item.lastMsg, color = AdminColors.TextMuted, fontSize = 12.sp, maxLines = 1)
        }
        Text(item.time, color = AdminColors.TextMuted, fontSize = 11.sp)
    }
}
