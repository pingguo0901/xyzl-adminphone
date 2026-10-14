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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellarelite.admin.ui.components.FormDivider
import com.stellarelite.admin.ui.components.FormGroup
import com.stellarelite.admin.ui.components.FormNote
import com.stellarelite.admin.ui.components.FormSectionTitle
import com.stellarelite.admin.ui.components.FormSwitchRow
import com.stellarelite.admin.ui.components.FormValueRow
import com.stellarelite.admin.ui.components.SubPageHeader
import com.stellarelite.admin.ui.theme.AdminColors
import com.stellarelite.admin.ui.theme.DisplayPrefs

/** 界面与显示页：深色/浅色模式 · 字体大小 */
@Composable
fun DisplayScreen(
    onBack: () -> Unit = {},
    onOpenDarkMode: () -> Unit = {},
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
                onClick = {},
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

/** 胶囊式左右分段控件 */
@Composable
private fun SegmentedTabs(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(AdminColors.SurfaceVariant)
            .padding(4.dp),
    ) {
        options.forEachIndexed { index, option ->
            val selected = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (selected) AdminColors.Primary else Color.Transparent)
                    .clickable { onSelect(index) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    option,
                    fontSize = 14.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                    color = if (selected) Color(0xFFFFFFFF) else AdminColors.TextMuted,
                )
            }
        }
    }
}
