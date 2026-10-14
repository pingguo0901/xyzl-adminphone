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
import com.stellarelite.admin.ui.screens.AboutScreen
import com.stellarelite.admin.ui.screens.AboutUsScreen
import com.stellarelite.admin.ui.screens.ChatScreen
import com.stellarelite.admin.ui.screens.DataRetentionPolicyScreen
import com.stellarelite.admin.ui.screens.DisclaimerScreen
import com.stellarelite.admin.ui.screens.EulaScreen
import com.stellarelite.admin.ui.screens.FinalComplianceScreen
import com.stellarelite.admin.ui.screens.KycPolicyScreen
import com.stellarelite.admin.ui.screens.PrivacyPolicyScreen
import com.stellarelite.admin.ui.screens.RefundPolicyScreen
import com.stellarelite.admin.ui.screens.UserServiceAgreementScreen
import com.stellarelite.admin.ui.screens.EntryScreen
import com.stellarelite.admin.ui.screens.GpsScreen
import com.stellarelite.admin.ui.screens.HomeScreen
import com.stellarelite.admin.ui.screens.MeScreen
import com.stellarelite.admin.ui.screens.NewOrderScreen
import com.stellarelite.admin.ui.screens.NotificationScreen
import com.stellarelite.admin.ui.screens.RegisterScreen
import com.stellarelite.admin.ui.screens.LoginScreen
import com.stellarelite.admin.ui.screens.TripsScreen
import com.stellarelite.admin.ui.theme.AdminColors
import kotlinx.coroutines.launch

enum class AppView {
    Entry, Login, Register, Dashboard
}

@Composable
fun App(
    appVersionName: String = "1.0.15",
    onCheckUpdate: (suspend () -> VersionInfo?)? = null,
    onRequestUpdate: ((VersionInfo) -> Unit)? = null
) {
    var showUpdateDialog by remember { mutableStateOf(false) }
    var showLatestDialog by remember { mutableStateOf(false) }
    var updateInfo by remember { mutableStateOf<VersionInfo?>(null) }
    var currentView by remember { mutableStateOf(AppView.Entry) }
    var currentTab by remember { mutableStateOf(AdminTab.Home) }
    var showNotifications by remember { mutableStateOf(false) }
    var showNewOrder by remember { mutableStateOf(false) }
    var showAboutUs by remember { mutableStateOf(false) }
    var showAbout by remember { mutableStateOf(false) }
    var policyIndex by remember { mutableStateOf<Int?>(null) }
    val scope = rememberCoroutineScope()

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
            LoginScreen(
                onBack = { currentView = AppView.Entry },
                onLoginSuccess = { currentView = AppView.Dashboard },
                onRegister = { currentView = AppView.Register },
                onForgetPassword = { /* TODO: 忘记密码流程 */ },
                onWechatLogin = { /* TODO: 微信登录 */ },
                onAppleLogin = { /* TODO: Apple 登录 */ },
                onOpenPolicy = { /* TODO: 协议与政策页 */ }
            )
        }
        AppView.Register -> {
            RegisterScreen(
                onRegister = { currentView = AppView.Login },
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
            } else if (showNewOrder) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AdminColors.Background)
                        .statusBarsPadding()
                ) {
                    NewOrderScreen(onBack = { showNewOrder = false })
                }
            } else if (showAboutUs) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AdminColors.Background)
                ) {
                    AboutUsScreen(onBack = { showAboutUs = false })
                }
            } else if (showAbout) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AdminColors.Background)
                ) {
                    AboutScreen(
                        versionName = appVersionName,
                        onOpenVersionUpdate = {
                            scope.launch {
                                val info = onCheckUpdate?.invoke()
                                if (info != null) {
                                    updateInfo = info
                                    showUpdateDialog = true
                                } else {
                                    showLatestDialog = true
                                }
                            }
                        },
                        onOpenEula = { showAbout = false; policyIndex = 7 },
                        onBack = { showAbout = false }
                    )
                }
            } else if (policyIndex != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AdminColors.Background)
                ) {
                    PolicyByIndex(policyIndex!!) { policyIndex = null }
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
                            AdminTab.Trips -> TripsScreen(onBellClick = { showNotifications = true }, onAddOrder = { showNewOrder = true })
                            AdminTab.Me -> MeScreen(
                                onBellClick = { showNotifications = true },
                                onOpenAboutUs = { showAboutUs = true },
                                onOpenAbout = { showAbout = true },
                                onOpenPolicy = { policyIndex = it }
                            )
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

    if (showLatestDialog) {
        AlertDialog(
            onDismissRequest = { showLatestDialog = false },
            containerColor = AdminColors.Card,
            title = { Text("版本更新", color = AdminColors.TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
            text = { Text("已是最新版本", color = AdminColors.TextSecondary, fontSize = 14.sp) },
            confirmButton = { TextButton(onClick = { showLatestDialog = false }) { Text("完成", color = AdminColors.Primary, fontWeight = FontWeight.SemiBold) } }
        )
    }
}

/** 协议/政策索引：0 用户服务协议 / 1 隐私政策 / 2 KYC / 3 退款 / 4 数据留存 / 5 免责 / 6 合规最终说明 / 7 EULA */
@Composable
private fun PolicyByIndex(index: Int, onBack: () -> Unit) {
    when (index) {
        0 -> UserServiceAgreementScreen(onBack = onBack)
        1 -> PrivacyPolicyScreen(onBack = onBack)
        2 -> KycPolicyScreen(onBack = onBack)
        3 -> RefundPolicyScreen(onBack = onBack)
        4 -> DataRetentionPolicyScreen(onBack = onBack)
        5 -> DisclaimerScreen(onBack = onBack)
        6 -> FinalComplianceScreen(onBack = onBack)
        else -> EulaScreen(onBack = onBack)
    }
}
