package com.stellarelite.admin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.RowScope
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
import com.stellarelite.admin.model.Order
import com.stellarelite.admin.ui.components.*
import com.stellarelite.admin.ui.theme.AdminColors

@Composable
fun CustomTripsScreen(
    trips: List<Order>,
    loading: Boolean,
    onRefresh: () -> Unit,
    onSelectTrip: (Order) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filtered = remember(trips, searchQuery) {
        if (searchQuery.isEmpty()) trips
        else trips.filter {
            it.order_no.contains(searchQuery, true) ||
            it.customer_name.contains(searchQuery, true) ||
            (it.destination?.contains(searchQuery, true) ?: false)
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
                Text("行程矩阵", color = AdminColors.TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Black)
                Text("${filtered.size} 条行程记录", color = AdminColors.TextMuted, fontSize = 12.sp)
            }
            ActionButton("刷新", onClick = { onRefresh() }, color = AdminColors.SurfaceVariant)
        }

        Box(modifier = Modifier.padding(horizontal = 14.dp)) {
            SearchBar(query = searchQuery, onQueryChange = { searchQuery = it }, placeholder = "搜索行程...")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Summary stats
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val pending = filtered.count { it.status == "pending" }
            val inProgress = filtered.count { it.status == "in_progress" }
            val completed = filtered.count { it.status == "completed" }
            StatCard("待处理", pending, AdminColors.StatusPending)
            StatCard("进行中", inProgress, AdminColors.StatusInProgress)
            StatCard("已完成", completed, AdminColors.StatusCompleted)
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (loading) {
            LoadingView()
        } else if (filtered.isEmpty()) {
            EmptyView("暂无行程数据")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(filtered) { trip ->
                    TripCompactCard(trip = trip, onClick = { onSelectTrip(trip) })
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun RowScope.StatCard(label: String, count: Int, color: androidx.compose.ui.graphics.Color) {
    Box(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(AdminColors.Card)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(count.toString(), color = color, fontSize = 22.sp, fontWeight = FontWeight.Black)
            Text(label, color = AdminColors.TextMuted, fontSize = 10.sp)
        }
    }
}

@Composable
fun TripCompactCard(trip: Order, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AdminColors.Card)
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(trip.order_no, color = AdminColors.Primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    StatusBadge(trip.status)
                }
                Text(trip.customer_name, color = AdminColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 4.dp))
                Text(trip.pickup_location ?: "未设置上车地点", color = AdminColors.TextMuted, fontSize = 11.sp, maxLines = 1)
            }
            if (trip.amount > 0) {
                Text(
                    "RM${"%.0f".format(trip.amount)}",
                    color = AdminColors.Warning,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
