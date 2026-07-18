package com.stellarelite.admin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellarelite.admin.model.CustomerProfile
import com.stellarelite.admin.ui.components.*
import com.stellarelite.admin.ui.theme.AdminColors

@Composable
fun ProfilesScreen(
    profiles: List<CustomerProfile>,
    loading: Boolean,
    onRefresh: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filtered = remember(profiles, searchQuery) {
        if (searchQuery.isEmpty()) profiles
        else profiles.filter {
            it.real_name.contains(searchQuery, true) ||
            it.username.contains(searchQuery, true) ||
            it.phone.contains(searchQuery, true)
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
                Text("用户档案", color = AdminColors.TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Black)
                Text("${filtered.size} 位用户", color = AdminColors.TextMuted, fontSize = 12.sp)
            }
            ActionButton("刷新", onClick = { onRefresh() }, color = AdminColors.SurfaceVariant)
        }

        Box(modifier = Modifier.padding(horizontal = 14.dp)) {
            SearchBar(query = searchQuery, onQueryChange = { searchQuery = it }, placeholder = "搜索用户...")
        }

        if (loading) {
            LoadingView()
        } else if (filtered.isEmpty()) {
            EmptyView("暂无用户数据")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filtered) { profile ->
                    ProfileCard(profile = profile)
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun ProfileCard(profile: CustomerProfile) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AdminColors.Card)
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Avatar placeholder
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(AdminColors.PrimaryBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    profile.real_name.take(1),
                    color = AdminColors.Primary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(profile.real_name.ifEmpty { profile.username }, color = AdminColors.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(profile.email.ifEmpty { profile.phone }, color = AdminColors.TextMuted, fontSize = 12.sp)
            }

            Text("查看 >", color = AdminColors.TextMuted, fontSize = 11.sp)
        }
    }
}
