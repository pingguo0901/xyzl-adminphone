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
import com.stellarelite.admin.model.DutyRoster
import com.stellarelite.admin.model.StaffProfile
import com.stellarelite.admin.ui.components.*
import com.stellarelite.admin.ui.theme.AdminColors

@Composable
fun DutyRosterScreen(
    roster: List<DutyRoster>,
    drivers: List<StaffProfile>,
    loading: Boolean,
    onRefresh: () -> Unit,
    onToggleDuty: (String) -> Unit
) {
    val activeIds = remember(roster) { roster.filter { it.is_active }.map { it.staff_id }.toSet() }
    var searchQuery by remember { mutableStateOf("") }

    val filtered = remember(drivers, searchQuery) {
        if (searchQuery.isEmpty()) drivers
        else drivers.filter {
            it.real_name.contains(searchQuery, true) ||
            it.username.contains(searchQuery, true)
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
                Text("排班监控", color = AdminColors.TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Black)
                Text("${activeIds.size}/${filtered.size} 在线", color = AdminColors.TextMuted, fontSize = 12.sp)
            }
            ActionButton("刷新", onClick = { onRefresh() }, color = AdminColors.SurfaceVariant)
        }

        Box(modifier = Modifier.padding(horizontal = 14.dp)) {
            SearchBar(query = searchQuery, onQueryChange = { searchQuery = it }, placeholder = "搜索司机...")
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (loading) {
            LoadingView()
        } else if (filtered.isEmpty()) {
            EmptyView("暂无司机数据")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(filtered) { driver ->
                    val isActive = activeIds.contains(driver.id)
                    DutyDriverCard(
                        name = driver.real_name,
                        phone = driver.phone,
                        isActive = isActive,
                        onToggle = { onToggleDuty(driver.id) }
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun DutyDriverCard(
    name: String,
    phone: String,
    isActive: Boolean,
    onToggle: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AdminColors.Card)
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Online indicator
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isActive) AdminColors.Success else AdminColors.TextDisabled)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(name, color = AdminColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Text(phone.ifEmpty { "无联系方式" }, color = AdminColors.TextMuted, fontSize = 11.sp)
            }

            // Toggle button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isActive) AdminColors.Danger.copy(alpha = 0.15f) else AdminColors.PrimaryBg)
                    .clickable { onToggle() }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    if (isActive) "下线" else "上线",
                    color = if (isActive) AdminColors.Danger else AdminColors.Primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
