package com.stellarelite.admin

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellarelite.admin.data.SupabaseClient
import com.stellarelite.admin.model.*
import com.stellarelite.admin.ui.components.*
import com.stellarelite.admin.ui.screens.*
import com.stellarelite.admin.ui.theme.AdminColors
import kotlinx.coroutines.launch

enum class AppView {
    Login, Dashboard
}

@Composable
fun App() {
    val client = remember { SupabaseClient() }
    var currentView by remember { mutableStateOf(AppView.Login) }
    var currentUser by remember { mutableStateOf<AdminUser?>(null) }
    var currentTab by remember { mutableStateOf(AdminTab.Orders) }
    var loginError by remember { mutableStateOf<String?>(null) }
    var loginLoading by remember { mutableStateOf(false) }
    var globalLoading by remember { mutableStateOf(false) }

    // Data states
    var orders by remember { mutableStateOf<List<Order>>(emptyList()) }
    var profiles by remember { mutableStateOf<List<CustomerProfile>>(emptyList()) }
    var staffProfiles by remember { mutableStateOf<List<StaffProfile>>(emptyList()) }
    var chatMessages by remember { mutableStateOf<List<ChatMessage>>(emptyList()) }
    var dutyRoster by remember { mutableStateOf<List<DutyRoster>>(emptyList()) }
    var driverExpenses by remember { mutableStateOf<List<DriverExpense>>(emptyList()) }

    var selectedOrder by remember { mutableStateOf<Order?>(null) }
    var statusFilter by remember { mutableStateOf<String?>(null) }
    var showOrderDetail by remember { mutableStateOf(false) }
    var newChatMsg by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    // Fetch all data after login
    fun refreshAllData() {
        scope.launch {
            globalLoading = true
            try {
                orders = client.fetchOrders()
                profiles = client.fetchProfiles()
                staffProfiles = client.fetchStaffProfiles()
                chatMessages = client.fetchChatMessages()
                dutyRoster = client.fetchDutyRoster()
                driverExpenses = client.fetchDriverExpenses()
            } catch (_: Exception) {}
            globalLoading = false
        }
    }

    fun onLogin(username: String, password: String) {
        scope.launch {
            loginLoading = true
            loginError = null
            val normalizedUser = username.trim().split("@").first()
            val loginEmail = if (username.contains("@")) username else "$normalizedUser@xyzl.admin"

            client.login(loginEmail, password)
                .onSuccess { user ->
                    currentUser = user
                    currentView = AppView.Dashboard
                    refreshAllData()
                }
                .onFailure { e ->
                    loginError = "认证失败: ${e.message}"
                }
            loginLoading = false
        }
    }

    fun onLogout() {
        client.logout()
        currentUser = null
        currentView = AppView.Login
    }

    fun onEndService(orderId: String, orderNo: String) {
        scope.launch {
            val farewell = "本次服务已结束，感谢贵宾的选择与支持。期待下次为您带来更佳的服务体验。"
            client.sendAdminMessage(orderId, farewell)
            client.updateOrderStatus(orderId, orderNo, "completed")
            refreshAllData()
        }
    }

    fun onSendMessage(orderId: String, msg: String) {
        scope.launch {
            client.sendAdminMessage(orderId, msg)
            newChatMsg = ""
            chatMessages = client.fetchChatMessages(orderId)
        }
    }

    // Render views
    when (currentView) {
        AppView.Login -> {
            LoginScreen(
                onLogin = ::onLogin,
                loading = loginLoading,
                error = loginError
            )
        }
        AppView.Dashboard -> {
            if (showOrderDetail && selectedOrder != null) {
                OrderDetailScreen(
                    order = selectedOrder!!,
                    staff = staffProfiles,
                    drivers = staffProfiles.filter { it.role == "DR" },
                    onBack = { showOrderDetail = false },
                    onUpdateStatus = { id, orderNo, status, extra ->
                        scope.launch {
                            client.updateOrderStatus(id, orderNo, status)
                            refreshAllData()
                        }
                    },
                    onEndService = { onEndService(selectedOrder!!.id, selectedOrder!!.order_no) },
                    onSendMessage = { onSendMessage(selectedOrder!!.id, it) },
                    messages = chatMessages.filter { it.order_id == selectedOrder!!.id }
                )
            } else {
                DashboardShell(
                    currentUser = currentUser,
                    currentTab = currentTab,
                    onTabSelected = { currentTab = it },
                    onLogout = ::onLogout,
                ) {
                    when (currentTab) {
                        AdminTab.Orders -> OrdersScreen(
                            orders = orders,
                            loading = globalLoading,
                            onRefresh = ::refreshAllData,
                            onSelectOrder = { order ->
                                selectedOrder = order
                                showOrderDetail = true
                                scope.launch {
                                    chatMessages = client.fetchChatMessages(order.id)
                                }
                            },
                            statusFilter = statusFilter,
                            onStatusFilterChange = { statusFilter = it }
                        )
                        AdminTab.Profiles -> ProfilesScreen(
                            profiles = profiles,
                            loading = globalLoading,
                            onRefresh = ::refreshAllData
                        )
                        AdminTab.Staff -> StaffProfilesScreen(
                            staff = staffProfiles,
                            loading = globalLoading,
                            onRefresh = ::refreshAllData
                        )
                        AdminTab.CustomTrips -> CustomTripsScreen(
                            trips = orders,
                            loading = globalLoading,
                            onRefresh = ::refreshAllData,
                            onSelectTrip = { order ->
                                selectedOrder = order
                                showOrderDetail = true
                            }
                        )
                        AdminTab.Chat -> ChatAuditScreen(
                            messages = chatMessages,
                            loading = globalLoading,
                            onRefresh = ::refreshAllData
                        )
                        AdminTab.Storage -> StorageScreen(
                            loading = globalLoading,
                            onRefresh = ::refreshAllData
                        )
                        AdminTab.DutyRoster -> DutyRosterScreen(
                            roster = dutyRoster,
                            drivers = staffProfiles.filter { it.role == "DR" },
                            loading = globalLoading,
                            onRefresh = ::refreshAllData,
                            onToggleDuty = { /* TODO: toggle duty */ }
                        )
                        AdminTab.Finance -> FinanceScreen(
                            expenses = driverExpenses,
                            loading = globalLoading,
                            onRefresh = ::refreshAllData
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardShell(
    currentUser: AdminUser?,
    currentTab: AdminTab,
    onTabSelected: (AdminTab) -> Unit,
    onLogout: () -> Unit,
    content: @Composable () -> Unit
) {
    val user = currentUser ?: return
    val isManagement = user.role == AdminRole.DSZ || user.role == AdminRole.MS || user.role == AdminRole.KJ

    val tabs = when (user.role) {
        AdminRole.KJ -> listOf(AdminTab.Orders, AdminTab.CustomTrips, AdminTab.Finance, AdminTab.Storage)
        AdminRole.DSZ, AdminRole.MS -> AdminTab.entries.toList().dropLast(1) // All except Finance
        AdminRole.ZG -> listOf(AdminTab.Profiles, AdminTab.Staff, AdminTab.Orders, AdminTab.CustomTrips)
        else -> listOf(AdminTab.Orders, AdminTab.CustomTrips) // CF, GJ
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AdminColors.Background)
            .statusBarsPadding()
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "星域臻旅 · 后台端",
                    color = AdminColors.TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    "${user.real_name.ifEmpty { user.username }} · ${user.role.label}",
                    color = AdminColors.roleColor(user.role.name),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                    .background(AdminColors.Danger.copy(alpha = 0.15f))
                    .clickable { onLogout() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("退出", color = AdminColors.Danger, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Tab content
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            content()
        }

        // Bottom tabs
        AdminBottomNavBar(
            tabs = tabs,
            currentTab = currentTab,
            onTabSelected = onTabSelected
        )
    }
}

@Composable
fun OrderDetailScreen(
    order: Order,
    staff: List<StaffProfile>,
    drivers: List<StaffProfile>,
    onBack: () -> Unit,
    onUpdateStatus: (String, String, String, Map<String, String>) -> Unit,
    onEndService: () -> Unit,
    onSendMessage: (String) -> Unit,
    messages: List<ChatMessage>
) {
    var chatInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AdminColors.Background)
            .statusBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                    .background(AdminColors.SurfaceVariant)
                    .clickable { onBack() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("← 返回", color = AdminColors.TextMuted, fontSize = 12.sp)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusBadge(order.status)
                if (order.status == "in_progress") {
                    Box(
                        modifier = Modifier
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                            .background(AdminColors.Success)
                            .clickable { onEndService() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("结束服务", color = androidx.compose.ui.graphics.Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Order info
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Text(order.order_no, color = AdminColors.Primary, fontSize = 18.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(8.dp))
            InfoRow("客户", order.customer_name)
            InfoRow("出发时间", order.departure_time ?: "-")
            InfoRow("上车地点", order.pickup_location ?: "-")
            InfoRow("目的地", order.destination ?: "-")
            InfoRow("车型", order.vehicle_type ?: "-")
            InfoRow("金额", "RM ${"%.2f".format(order.amount)}", valueColor = 0xFFFBBF24)
            InfoRow("司机薪资", "RM ${"%.2f".format(order.driver_salary)}")
            InfoRow("现金SGD", "SGD ${"%.2f".format(order.cash_sgd)}")
            InfoRow("现金RM", "RM ${"%.2f".format(order.cash_rm)}")
            InfoRow("已支付", if (order.is_paid) "✓ 是" else "✗ 否")
            InfoRow("创建时间", order.created_at)
        }

        // Quick actions
        if (order.status == "pending" || order.status == "confirmed") {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (order.status == "pending") {
                    ActionButton("确认接单", onClick = {
                        onUpdateStatus(order.id, order.order_no, "confirmed", emptyMap())
                    }, color = AdminColors.StatusConfirmed)
                }
                ActionButton("开始服务", onClick = {
                    onUpdateStatus(order.id, order.order_no, "in_progress", emptyMap())
                }, color = AdminColors.StatusInProgress)
            }
        }

        Text("聊天记录", color = AdminColors.TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp))

        // Chat messages
        androidx.compose.foundation.lazy.LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(messages) { msg ->
                val isAdmin = msg.is_admin
                val align = if (isAdmin) Alignment.End else Alignment.Start
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = align) {
                    Box(
                        modifier = Modifier
                            .widthIn(max = 260.dp)
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                            .background(if (isAdmin) AdminColors.PrimaryBg else AdminColors.SurfaceVariant)
                            .padding(10.dp)
                    ) {
                        Text(msg.message, color = AdminColors.TextPrimary, fontSize = 13.sp)
                    }
                }
            }
        }

        // Chat input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(20.dp))
                    .background(AdminColors.Surface)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                androidx.compose.foundation.text.BasicTextField(
                    value = chatInput,
                    onValueChange = { chatInput = it },
                    textStyle = androidx.compose.ui.text.TextStyle(color = AdminColors.TextPrimary, fontSize = 13.sp),
                    cursorBrush = androidx.compose.ui.graphics.SolidColor(AdminColors.Primary),
                    singleLine = true,
                    decorationBox = { inner ->
                        Box { if (chatInput.isEmpty()) Text("输入消息...", color = AdminColors.TextDisabled, fontSize = 13.sp); inner() }
                    }
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(AdminColors.Primary)
                    .clickable {
                        if (chatInput.isNotBlank()) {
                            onSendMessage(chatInput)
                            chatInput = ""
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("→", color = androidx.compose.ui.graphics.Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}
