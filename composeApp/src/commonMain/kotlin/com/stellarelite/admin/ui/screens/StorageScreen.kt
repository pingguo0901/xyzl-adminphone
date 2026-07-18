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
import com.stellarelite.admin.ui.components.*
import com.stellarelite.admin.ui.theme.AdminColors

@Composable
fun StorageScreen(
    loading: Boolean,
    onRefresh: () -> Unit
) {
    var selectedBucket by remember { mutableStateOf("avatars") }

    Column(
        modifier = Modifier.fillMaxSize().background(AdminColors.Background)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("云端存储", color = AdminColors.TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Black)
            ActionButton("刷新", onClick = { onRefresh() }, color = AdminColors.SurfaceVariant)
        }

        // Bucket selector
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip("头像库", selected = selectedBucket == "avatars", onClick = { selectedBucket = "avatars" })
            FilterChip("司机证件", selected = selectedBucket == "driver_documents", onClick = { selectedBucket = "driver_documents" })
            FilterChip("KYC", selected = selectedBucket == "kyc-documents", onClick = { selectedBucket = "kyc-documents" })
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (loading) {
            LoadingView()
        } else {
            EmptyView("云端存储（建设中）\n当前桶: $selectedBucket")
        }
    }
}
