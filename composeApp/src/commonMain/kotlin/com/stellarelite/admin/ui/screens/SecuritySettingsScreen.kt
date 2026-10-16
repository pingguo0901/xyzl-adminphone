package com.stellarelite.admin.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellarelite.admin.ui.components.FormDivider
import com.stellarelite.admin.ui.components.FormGroup
import com.stellarelite.admin.ui.components.FormNote
import com.stellarelite.admin.ui.components.SubPageHeader
import com.stellarelite.admin.ui.theme.AdminColors

private val OauthLabels = listOf("通过 Apple 登录", "通过 Facebook 登录", "通过 WeChat 登录")

/** 更多安全设置页：第三方登录绑定状态（复刻司机端，本页仅本地状态） */
@Composable
fun SecuritySettingsScreen(onBack: () -> Unit = {}) {
    var hint by remember { mutableStateOf<String?>(null) }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        SubPageHeader("更多安全设置", onBack = onBack)
        Spacer(Modifier.height(16.dp))

        FormGroup {
            OauthLabels.forEachIndexed { index, label ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { hint = "第三方绑定即将开放" }
                        .padding(vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(label, fontSize = 15.sp, color = AdminColors.TextPrimary, modifier = Modifier.weight(1f))
                    Text(
                        text = "未绑定",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AdminColors.TextMuted,
                    )
                }
                if (index != OauthLabels.lastIndex) {
                    FormDivider()
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        FormNote("第三方登录要接 Apple / Facebook / WeChat 的 SDK，接入后再走授权流程，绑定的第三方账号统一绑定到当前账号。")
        Spacer(Modifier.height(110.dp))
    }

    val message = hint
    if (message != null) {
        AlertDialog(
            onDismissRequest = { hint = null },
            containerColor = AdminColors.Card,
            title = { Text("提示", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = AdminColors.TextPrimary) },
            text = { Text(message, fontSize = 14.sp, color = AdminColors.TextMuted) },
            confirmButton = {
                TextButton(onClick = { hint = null }) {
                    Text("确定", color = AdminColors.Primary, fontWeight = FontWeight.SemiBold)
                }
            },
        )
    }
}
