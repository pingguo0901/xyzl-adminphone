package com.stellarelite.admin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
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
fun MeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        PageTitle("我")
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(36.dp))
                    .background(AdminColors.SurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Person, contentDescription = null, tint = AdminColors.TextPrimary, modifier = Modifier.size(36.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text("管理员", color = AdminColors.TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(2.dp))
            Text("星域集团 · 后台端", color = AdminColors.TextMuted, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                MenuRow(Icons.Outlined.Settings, "账号设置")
                MenuRow(Icons.Outlined.Notifications, "消息通知")
                MenuRow(Icons.Outlined.Info, "关于")
                MenuRow(Icons.AutoMirrored.Outlined.Logout, "退出登录")
            }
        }
    }
}

@Composable
private fun MenuRow(icon: ImageVector, label: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = label, tint = AdminColors.TextPrimary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(label, color = AdminColors.TextPrimary, fontSize = 13.sp, modifier = Modifier.weight(1f))
        Text("›", color = AdminColors.TextMuted, fontSize = 18.sp)
    }
}
