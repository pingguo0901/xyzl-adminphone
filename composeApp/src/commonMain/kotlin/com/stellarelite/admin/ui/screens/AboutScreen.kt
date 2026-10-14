package com.stellarelite.admin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellarelite.admin.ui.components.AppIcon
import com.stellarelite.admin.ui.components.SubPageHeader
import com.stellarelite.admin.ui.theme.AdminColors

/** 关于平台页：APP 图标 + 名称 + 版本号 + 版本更新入口 + EULA 按钮 + 版权（复刻司机端） */
@Composable
fun AboutScreen(
    versionName: String,
    onOpenVersionUpdate: () -> Unit = {},
    onOpenEula: () -> Unit = {},
    onBack: () -> Unit = {},
) {
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SubPageHeader(title = "关于平台", onBack = onBack)

        Column(
            Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(36.dp))

            Box(
                Modifier.size(92.dp).clip(RoundedCornerShape(24.dp)).background(AdminColors.SurfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                AppIcon(size = 92)
            }

            Spacer(Modifier.height(18.dp))
            Text("星域臻旅 后台端", fontSize = 19.sp, fontWeight = FontWeight.SemiBold, color = AdminColors.TextPrimary)
            Spacer(Modifier.height(6.dp))
            Text("Version " + versionName, fontSize = 13.sp, color = AdminColors.TextMuted)

            Spacer(Modifier.height(30.dp))
            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = AdminColors.Card),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { onOpenVersionUpdate() }
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("版本更新", fontSize = 15.sp, color = AdminColors.TextPrimary, modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = AdminColors.TextMuted,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }

        TextButton(onClick = onOpenEula) {
            Text("《终端用户软件许可协议》", fontSize = 14.sp, color = AdminColors.Primary)
        }
        Spacer(Modifier.height(8.dp))
        Text("星域臻旅版权所有", fontSize = 12.sp, color = AdminColors.TextMuted)
        Spacer(Modifier.height(16.dp))
    }
}
