package com.stellarelite.admin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellarelite.admin.model.ChatMessage
import com.stellarelite.admin.ui.components.*
import com.stellarelite.admin.ui.theme.AdminColors

@Composable
fun ChatAuditScreen(
    messages: List<ChatMessage>,
    loading: Boolean,
    onRefresh: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filtered = remember(messages, searchQuery) {
        if (searchQuery.isEmpty()) messages
        else messages.filter {
            it.message.contains(searchQuery, true) ||
            it.order_id.contains(searchQuery, true)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(AdminColors.Background)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("通讯审计", color = AdminColors.TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Black)
                Text("${filtered.size} 条消息", color = AdminColors.TextMuted, fontSize = 12.sp)
            }
            ActionButton("刷新", onClick = { onRefresh() }, color = AdminColors.SurfaceVariant)
        }

        Box(modifier = Modifier.padding(horizontal = 14.dp)) {
            SearchBar(query = searchQuery, onQueryChange = { searchQuery = it }, placeholder = "搜索消息内容...")
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (loading) {
            LoadingView()
        } else if (filtered.isEmpty()) {
            EmptyView("暂无聊天记录")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(filtered) { msg ->
                    ChatBubble(msg = msg)
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun ChatBubble(msg: ChatMessage) {
    val isAdmin = msg.is_admin
    val bubbleColor = if (isAdmin) AdminColors.PrimaryBg else AdminColors.SurfaceVariant
    val align = if (isAdmin) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalAlignment = align
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(bubbleColor)
                .padding(12.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        if (isAdmin) "管理员" else "用户",
                        color = if (isAdmin) AdminColors.Primary else AdminColors.TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        msg.order_id.takeLast(8),
                        color = AdminColors.TextDisabled,
                        fontSize = 10.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    msg.message,
                    color = AdminColors.TextPrimary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
