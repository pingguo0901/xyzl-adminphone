package com.stellarelite.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellarelite.admin.model.VersionInfo
import com.stellarelite.admin.ui.components.AdminBottomNavBar
import com.stellarelite.admin.ui.components.AdminTab
import com.stellarelite.admin.ui.screens.ChatScreen
import com.stellarelite.admin.ui.screens.EntryScreen
import com.stellarelite.admin.ui.screens.GpsScreen
import com.stellarelite.admin.ui.screens.HomeScreen
import com.stellarelite.admin.ui.screens.MeScreen
import com.stellarelite.admin.ui.screens.NotificationScreen
import com.stellarelite.admin.ui.screens.RegisterScreen
import com.stellarelite.admin.ui.screens.XingYuLoginScreen
import com.stellarelite.admin.ui.screens.TripsScreen
import com.stellarelite.admin.ui.theme.AdminColors

enum class AppView {
    Entry, Login, Register, Dashboard
}

@Composable
fun App(
    onCheckUpdate: (suspend () -> VersionInfo?)? = null,
    onRequestUpdate: ((VersionInfo) -> Unit)? = null
) {
    var showUpdateDialog by remember { mutableStateOf(false) }
    var updateInfo by remember { mutableStateOf<VersionInfo?>(null) }
    var currentView by remember { mutableStateOf(AppView.Entry) }
    var currentTab by remember { mutableStateOf(AdminTab.Home) }
    var showNotifications by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        onCheckUpdate?.let { checkFn ->
            try {
                val info = checkFn()
                if (info != null) {
                    updateInfo = info
                    showUpdateDialog = true
                }
            } catch (_: Exception) { }
        }
    }

    when (currentView) {
        AppView.Entry -> {
            EntryScreen(
                onLoginClick = { currentView = AppView.Login },
                onRegisterClick = { currentView = AppView.Register }
            )
        }
        AppView.Login -> {
            XingYuLoginScreen(
                onLogin = { _, _ -> currentView = AppView.Dashboard },
                onRegister = { currentView = AppView.Register },
                onForgetPassword = { /* TODO: 忘记密码流程 */ },
                onWechatLogin = { /* TODO: 微信登录 */ },
                onAppleLogin = { /* TODO: Apple 登录 */ },
                onUserAgreementClick = { /* TODO: 用户协议 */ },
                onPrivacyPolicyClick = { /* TODO: 隐私政策 */ }
            )
        }
        AppView.Register -> {
            RegisterScreen(
                onRegister = { _, _, _ -> currentView = AppView.Login },
                onBack = { currentView = AppView.Entry }
            )
        }
        AppView.Dashboard -> {
            if (showNotifications) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AdminColors.Background)
                        .statusBarsPadding()
                ) {
                    NotificationScreen(onBack = { showNotifications = false })
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AdminColors.Background)
                        .statusBarsPadding()
                ) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        when (currentTab) {
                            AdminTab.Home -> HomeScreen(onBellClick = { showNotifications = true })
                            AdminTab.Chat -> ChatScreen(onBellClick = { showNotifications = true })
                            AdminTab.Gps -> GpsScreen(onBellClick = { showNotifications = true })
                            AdminTab.Trips -> TripsScreen(onBellClick = { showNotifications = true })
                            AdminTab.Me -> MeScreen()
                        }
                    }
                    AdminBottomNavBar(
                        tabs = AdminTab.entries.toList(),
                        currentTab = currentTab,
                        onTabSelected = { currentTab = it }
                    )
                }
            }
        }
    }

    if (showUpdateDialog && updateInfo != null) {
        AlertDialog(
            onDismissRequest = { showUpdateDialog = false },
            containerColor = AdminColors.Card,
            title = { Text("发现新版本 v" + updateInfo!!.versionName, color = AdminColors.TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
            text = { Text(updateInfo!!.changelog.replace("- ", "• "), color = AdminColors.TextSecondary, fontSize = 14.sp) },
            confirmButton = { TextButton(onClick = { showUpdateDialog = false; onRequestUpdate?.invoke(updateInfo!!) }) { Text("立即更新", color = AdminColors.Primary, fontWeight = FontWeight.SemiBold) } },
            dismissButton = { TextButton(onClick = { showUpdateDialog = false }) { Text("稍后", color = AdminColors.TextMuted) } }
        )
    }
}
