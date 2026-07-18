package com.stellarelite.admin.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellarelite.admin.model.Order
import com.stellarelite.admin.ui.components.*
import com.stellarelite.admin.ui.theme.AdminColors

@Composable
fun OrdersScreen(
    orders: List<Order>,
    loading: Boolean,
    onRefresh: () -> Unit,
    onSelectOrder: (Order) -> Unit,
    statusFilter: String?,
    onStatusFilterChange: (String?) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredOrders = remember(orders, searchQuery, statusFilter) {
        orders.filter { o ->
            val matchStatus = statusFilter == null || o.status == statusFilter
            val matchSearch = searchQuery.isEmpty() ||
                o.order_no.contains(searchQuery, true) ||
                o.customer_name.contains(searchQuery, true) ||
                (o.pickup_location?.contains(searchQuery, true) ?: false)
            matchStatus && matchSearch
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AdminColors.Background)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("工单系统", color = AdminColors.TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Black)
                Text("${filteredOrders.size} 个工单", color = AdminColors.TextMuted, fontSize = 12.sp)
            }
            ActionButton("刷新", onClick = { onRefresh() }, color = AdminColors.SurfaceVariant)
        }

        // Status filter chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip("全部", selected = statusFilter == null, onClick = { onStatusFilterChange(null) }, color = AdminColors.Primary)
            FilterChip("待处理", selected = statusFilter == "pending", onClick = { onStatusFilterChange("pending") }, color = AdminColors.StatusPending)
            FilterChip("已确认", selected = statusFilter == "confirmed", onClick = { onStatusFilterChange("confirmed") }, color = AdminColors.StatusConfirmed)
            FilterChip("进行中", selected = statusFilter == "in_progress", onClick = { onStatusFilterChange("in_progress") }, color = AdminColors.StatusInProgress)
            FilterChip("已完成", selected = statusFilter == "completed", onClick = { onStatusFilterChange("completed") }, color = AdminColors.StatusCompleted)
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Search
        Box(modifier = Modifier.padding(horizontal = 14.dp)) {
            SearchBar(query = searchQuery, onQueryChange = { searchQuery = it }, placeholder = "搜索订单号/客户名/上车地点...")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Order list
        if (loading) {
            LoadingView()
        } else if (filteredOrders.isEmpty()) {
            EmptyView("暂无工单")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredOrders) { order ->
                    OrderCard(order = order, onClick = { onSelectOrder(order) })
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun OrderCard(order: Order, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AdminColors.Card)
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    order.order_no,
                    color = AdminColors.Primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                StatusBadge(order.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            InfoRow("客户", order.customer_name)
            InfoRow("上车地点", order.pickup_location ?: "未设置")
            InfoRow("目的地", order.destination ?: "未设置")
            InfoRow("出发时间", order.departure_time ?: "未设置")

            if (order.amount > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("金额", color = AdminColors.TextMuted, fontSize = 12.sp)
                    Text(
                        "RM ${"%.2f".format(order.amount)}",
                        color = AdminColors.Warning,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
