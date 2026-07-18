package com.stellarelite.admin.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellarelite.admin.ui.theme.AdminColors

// ─── Bottom Nav Bar ───

enum class AdminTab(val label: String, val emoji: String) {
    Profiles("用户档案", "👥"),
    Staff("职员架构", "🛡️"),
    Orders("工单系统", "📋"),
    DutyRoster("排班监控", "⏰"),
    CustomTrips("行程矩阵", "🗺️"),
    Chat("通讯审计", "💬"),
    Storage("云端存储", "📁"),
    Finance("财务管理", "💰")
}

@Composable
fun AdminBottomNavBar(
    tabs: List<AdminTab>,
    currentTab: AdminTab,
    onTabSelected: (AdminTab) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AdminColors.NavBar)
            .padding(top = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 4.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            tabs.forEach { tab ->
                val isSelected = currentTab == tab
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) AdminColors.PrimaryBg else AdminColors.Card)
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(tab.emoji, fontSize = 16.sp)
                    Text(
                        tab.label,
                        color = if (isSelected) AdminColors.Primary else AdminColors.TextMuted,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

// ─── Common Components ───

@Composable
fun SectionTitle(title: String) {
    Text(
        title,
        color = AdminColors.TextSecondary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 4.dp)
    )
}

@Composable
fun InfoRow(label: String, value: String, valueColor: Long = 0xFFFFFFFF) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = AdminColors.TextMuted, fontSize = 12.sp)
        Text(value, color = Color(valueColor), fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.End, modifier = Modifier.widthIn(max = 180.dp))
    }
}

@Composable
fun StatusBadge(status: String) {
    val color = AdminColors.statusColor(status)
    val label = AdminColors.statusLabel(status)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(label, color = color, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun LoadingView(message: String = "加载中...") {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(message, color = AdminColors.TextMuted, fontSize = 14.sp)
    }
}

@Composable
fun EmptyView(message: String = "暂无数据") {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(message, color = AdminColors.TextMuted, fontSize = 14.sp)
    }
}

@Composable
fun ActionButton(
    label: String,
    onClick: () -> Unit,
    color: Color = AdminColors.Primary,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(color)
            .clickable { onClick() }
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    color: Color = AdminColors.Primary
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) color else AdminColors.SurfaceVariant)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(label, color = if (selected) Color.White else AdminColors.TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "搜索..."
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AdminColors.Surface)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        if (query.isEmpty()) {
            Text(placeholder, color = AdminColors.TextDisabled, fontSize = 13.sp)
        }
        // In a real implementation, use TextField
    }
}


