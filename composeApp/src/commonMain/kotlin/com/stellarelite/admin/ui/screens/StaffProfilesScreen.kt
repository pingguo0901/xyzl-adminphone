package com.stellarelite.admin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellarelite.admin.model.StaffProfile
import com.stellarelite.admin.ui.components.*
import com.stellarelite.admin.ui.theme.AdminColors

@Composable
fun StaffProfilesScreen(
    staff: List<StaffProfile>,
    loading: Boolean,
    onRefresh: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filtered = remember(staff, searchQuery) {
        if (searchQuery.isEmpty()) staff
        else staff.filter {
            it.real_name.contains(searchQuery, true) ||
            it.role.contains(searchQuery, true) ||
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
                Text("职员架构", color = AdminColors.TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Black)
                Text("${filtered.size} 位职员", color = AdminColors.TextMuted, fontSize = 12.sp)
            }
            ActionButton("刷新", onClick = { onRefresh() }, color = AdminColors.SurfaceVariant)
        }

        Box(modifier = Modifier.padding(horizontal = 14.dp)) {
            SearchBar(query = searchQuery, onQueryChange = { searchQuery = it }, placeholder = "搜索职员...")
        }

        if (loading) {
            LoadingView()
        } else if (filtered.isEmpty()) {
            EmptyView("暂无职员数据")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filtered) { staff ->
                    StaffCard(staff = staff)
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun StaffCard(staff: StaffProfile) {
    val roleColor = AdminColors.roleColor(staff.role)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AdminColors.Card)
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Role badge
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(roleColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(staff.role, color = roleColor, fontSize = 18.sp, fontWeight = FontWeight.Black)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(staff.real_name, color = AdminColors.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(2.dp))
                Text("@${staff.username}", color = AdminColors.TextMuted, fontSize = 12.sp)
            }

            Column(horizontalAlignment = Alignment.End) {
                if (staff.wallet_balance > 0) {
                    Text(
                        "RM ${"%.0f".format(staff.wallet_balance)}",
                        color = AdminColors.Warning,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (staff.phone.isNotEmpty()) {
                    Text(staff.phone, color = AdminColors.TextMuted, fontSize = 11.sp)
                }
            }
        }
    }
}
