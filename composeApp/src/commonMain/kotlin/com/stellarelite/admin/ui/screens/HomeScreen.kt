package com.stellarelite.admin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellarelite.admin.ui.components.GlassCard
import com.stellarelite.admin.ui.components.PageTitle
import com.stellarelite.admin.ui.theme.AdminColors

@Composable
fun HomeScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        PageTitle("首页")
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                CardHeader(Icons.Outlined.Refresh, "轮询消息")
                Spacer(modifier = Modifier.height(10.dp))
                Text("订单 A-2026 有新回复", color = AdminColors.TextPrimary, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("2 分钟前", color = AdminColors.TextMuted, fontSize = 11.sp)
            }
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                CardHeader(Icons.Outlined.Notifications, "重要通知")
                Spacer(modifier = Modifier.height(10.dp))
                NoticeRow("司机张师傅 已完成接单", "10:24")
                NoticeRow("新订单待派车：SG → JB", "10:02")
                NoticeRow("明日排班已更新", "昨天")
            }
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                CardHeader(Icons.Outlined.Assessment, "今日摘要")
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SummaryItem("今日订单", "12", Modifier.weight(1f))
                    SummaryItem("进行中", "3", Modifier.weight(1f))
                    SummaryItem("待派车", "2", Modifier.weight(1f))
                }
            }
        }
        QuickActionsBar()
    }
}

@Composable
private fun CardHeader(icon: ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = title, tint = AdminColors.Primary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, color = AdminColors.Primary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun NoticeRow(text: String, time: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(AdminColors.Primary)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, color = AdminColors.TextSecondary, fontSize = 12.sp, modifier = Modifier.weight(1f))
        Text(time, color = AdminColors.TextMuted, fontSize = 11.sp)
    }
}

@Composable
private fun SummaryItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(AdminColors.SurfaceVariant)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, color = AdminColors.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(label, color = AdminColors.TextMuted, fontSize = 10.sp)
    }
}

@Composable
private fun QuickActionsBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(AdminColors.Card)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        QuickAction(Icons.AutoMirrored.Outlined.ReceiptLong, "收单")
        QuickAction(Icons.Outlined.LocalShipping, "派单")
        QuickAction(Icons.Outlined.Search, "查单")
        QuickAction(Icons.Outlined.Notifications, "通知")
    }
}

@Composable
private fun QuickAction(icon: ImageVector, label: String) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = label, tint = AdminColors.TextPrimary, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.height(3.dp))
        Text(label, color = AdminColors.TextSecondary, fontSize = 10.sp)
    }
}
