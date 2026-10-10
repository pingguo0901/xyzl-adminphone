package com.stellarelite.admin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellarelite.admin.ui.components.FilterChipBar
import com.stellarelite.admin.ui.components.PageTitle
import com.stellarelite.admin.ui.components.StatusBadge
import com.stellarelite.admin.ui.theme.AdminColors

private data class TripItem(val orderNo: String, val passenger: String, val route: String, val status: String)

@Composable
fun TripsScreen(onBellClick: () -> Unit = {}, onAddOrder: () -> Unit = {}) {
    var filter by remember { mutableStateOf("全部") }

    Column(modifier = Modifier.fillMaxSize()) {
        PageTitle("行程", onBellClick = onBellClick)
        FilterChipBar(
            options = listOf("全部", "已完成", "未完成", "已取消"),
            selected = filter,
            onSelect = { filter = it }
        )
        val trips = listOf(
            TripItem("A-2026", "张三", "SG → JB", "completed"),
            TripItem("A-2025", "李四", "KL → SG", "in_progress"),
            TripItem("A-2024", "王五", "JB → KL", "cancelled"),
            TripItem("A-2023", "赵六", "SG → KL", "completed"),
            TripItem("A-2022", "孙七", "JB → SG", "pending")
        )
        val labelMap = mapOf("已完成" to "completed", "未完成" to "in_progress", "已取消" to "cancelled")
        val filtered = if (filter == "全部") trips else trips.filter { it.status == labelMap[filter] }
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filtered) { trip ->
                TripRow(trip)
            }
        }
        TripsQuickBar(onAddOrder = onAddOrder)
    }
}

@Composable
private fun TripRow(item: TripItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AdminColors.Card)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.orderNo, color = AdminColors.Primary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(3.dp))
            Text(item.passenger, color = AdminColors.TextPrimary, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(item.route, color = AdminColors.TextMuted, fontSize = 11.sp)
        }
        StatusBadge(item.status)
    }
}

@Composable
private fun TripsQuickBar(onAddOrder: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(AdminColors.Card)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 快捷操作占位
        Text(
            "快捷操作",
            color = AdminColors.TextMuted,
            fontSize = 11.sp,
            modifier = Modifier.weight(1f)
        )
        // 右上角圆形 + 按钮
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(AdminColors.Primary)
                .clickable { onAddOrder() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Add, contentDescription = "新建订单", tint = Color.White, modifier = Modifier.size(20.dp))
        }
    }
}
