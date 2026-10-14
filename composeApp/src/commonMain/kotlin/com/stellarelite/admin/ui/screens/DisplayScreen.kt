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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.stellarelite.admin.ui.components.FormDivider
import com.stellarelite.admin.ui.components.FormGroup
import com.stellarelite.admin.ui.components.FormNote
import com.stellarelite.admin.ui.components.FormSectionTitle
import com.stellarelite.admin.ui.components.FormSwitchRow
import com.stellarelite.admin.ui.components.FormValueRow
import com.stellarelite.admin.ui.components.SegmentedTabs
import com.stellarelite.admin.ui.components.SubPageHeader
import com.stellarelite.admin.ui.theme.DisplayPrefs

/** 界面与显示页：深色/浅色模式 · 字体大小 · 多语言 · 翻译（复刻司机端） */
@Composable
fun DisplayScreen(
    onBack: () -> Unit = {},
    onOpenDarkMode: () -> Unit = {},
    onOpenFontSize: () -> Unit = {},
    onOpenLanguage: () -> Unit = {},
    onOpenTranslation: () -> Unit = {},
) {
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        SubPageHeader(title = "界面与显示", onBack = onBack)
        Spacer(Modifier.height(16.dp))

        FormGroup {
            FormValueRow(
                label = "深色 / 浅色模式",
                value = DisplayPrefs.modeLabel,
                onClick = onOpenDarkMode,
            )
            FormDivider()
            FormValueRow(
                label = "字体大小",
                value = DisplayPrefs.fontSizeLevel.toString() + " / 7 档",
                onClick = onOpenFontSize,
            )
            FormDivider()
            FormValueRow(
                label = "多语言",
                value = DisplayPrefs.language,
                onClick = onOpenLanguage,
            )
            FormDivider()
            FormValueRow(
                label = "翻译",
                value = "翻译为 " + DisplayPrefs.translateTo,
                onClick = onOpenTranslation,
            )
        }

        Spacer(Modifier.height(40.dp))
    }
}

/** 深色模式页：跟随系统开关 + 手动选择 深色 / 浅色 */
@Composable
fun DarkModeScreen(onBack: () -> Unit = {}) {
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        SubPageHeader(title = "深色模式", onBack = onBack)
        Spacer(Modifier.height(16.dp))

        FormGroup {
            FormSwitchRow(
                label = "跟随系统",
                checked = DisplayPrefs.followSystem,
                onCheckedChange = { DisplayPrefs.followSystem = it },
            )
        }
        Spacer(Modifier.height(10.dp))
        FormNote("开启后，将跟随系统打开或关闭深色模式。")

        if (!DisplayPrefs.followSystem) {
            Spacer(Modifier.height(18.dp))
            FormSectionTitle("手动选择")
            SegmentedTabs(
                options = listOf("深色", "浅色"),
                selectedIndex = if (DisplayPrefs.darkMode) 0 else 1,
                onSelect = { DisplayPrefs.darkMode = it == 0 },
            )
        }

        Spacer(Modifier.height(40.dp))
    }
}
