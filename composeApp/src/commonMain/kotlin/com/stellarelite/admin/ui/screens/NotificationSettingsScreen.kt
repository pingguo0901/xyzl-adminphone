package com.stellarelite.admin.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.stellarelite.admin.ui.components.FormDivider
import com.stellarelite.admin.ui.components.FormGroup
import com.stellarelite.admin.ui.components.FormSectionTitle
import com.stellarelite.admin.ui.components.FormSwitchRow
import com.stellarelite.admin.ui.components.SubPageHeader

/** 通知设置页：后台端未打开时 / 后台端打开时（复刻司机端） */
@Composable
fun NotificationSettingsScreen(onBack: () -> Unit = {}) {
    var systemMessage by remember { mutableStateOf(true) }
    var callWhenClosed by remember { mutableStateOf(true) }
    var inAppBanner by remember { mutableStateOf(true) }
    var messageSound by remember { mutableStateOf(true) }
    var callWhenOpen by remember { mutableStateOf(true) }
    var vibrate by remember { mutableStateOf(true) }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        SubPageHeader(title = "通知", onBack = onBack)
        Spacer(Modifier.height(16.dp))

        FormSectionTitle("后台端未打开时")
        FormGroup {
            FormSwitchRow(
                label = "系统消息通知",
                checked = systemMessage,
                onCheckedChange = { systemMessage = it },
            )
            FormDivider()
            FormSwitchRow(
                label = "语音通话通知",
                checked = callWhenClosed,
                onCheckedChange = { callWhenClosed = it },
            )
        }

        Spacer(Modifier.height(18.dp))
        FormSectionTitle("后台端打开时")
        FormGroup {
            FormSwitchRow(
                label = "后台端内消息横幅",
                checked = inAppBanner,
                onCheckedChange = { inAppBanner = it },
            )
            FormDivider()
            FormSwitchRow(
                label = "消息提示音",
                checked = messageSound,
                onCheckedChange = { messageSound = it },
            )
            FormDivider()
            FormSwitchRow(
                label = "语音通话通知",
                checked = callWhenOpen,
                onCheckedChange = { callWhenOpen = it },
            )
            FormDivider()
            FormSwitchRow(
                label = "振动",
                checked = vibrate,
                onCheckedChange = { vibrate = it },
            )
        }

        Spacer(Modifier.height(40.dp))
    }
}
