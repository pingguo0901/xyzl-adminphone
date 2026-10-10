package com.stellarelite.admin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellarelite.admin.ui.components.GlassCard
import com.stellarelite.admin.ui.theme.AdminColors

@Composable
fun NewOrderScreen(onBack: () -> Unit) {
    var agentName by remember { mutableStateOf("") }
    var agentMethod by remember { mutableStateOf("WhatsApp") }
    var agentContact by remember { mutableStateOf("") }
    var customerName by remember { mutableStateOf("") }
    var customerMethod by remember { mutableStateOf("WhatsApp") }
    var customerContact by remember { mutableStateOf("") }
    var pickupCount by remember { mutableStateOf(1) }
    var dropoffCount by remember { mutableStateOf(1) }
    var pickupAddresses by remember { mutableStateOf(listOf("")) }
    var dropoffAddresses by remember { mutableStateOf(listOf("")) }
    var noteForDriver by remember { mutableStateOf("") }
    var noteForBackend by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("现金") }
    var currency by remember { mutableStateOf("SGD") }
    var amount by remember { mutableStateOf("") }
    var currencyOpen by remember { mutableStateOf(false) }
    var driver by remember { mutableStateOf("") }
    var vehicle by remember { mutableStateOf("") }
    var driverOpen by remember { mutableStateOf(false) }
    var vehicleOpen by remember { mutableStateOf(false) }

    val mockDrivers = listOf("张师傅", "李师傅", "王师傅")
    val mockVehicles = listOf("SG-1234A", "JB-5678B", "KL-9012C")

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回", tint = AdminColors.TextPrimary, modifier = Modifier.size(24.dp).clickable { onBack() })
            Spacer(modifier = Modifier.width(8.dp))
            Text("新建订单", color = AdminColors.TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Black)
        }

        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                CardLabel("中介名称", false)
                Spacer(Modifier.height(6.dp))
                OrderInput(agentName, { agentName = it }, "请输入中介名称")
                Spacer(Modifier.height(12.dp))
                CardLabel("中介联系方式", false)
                Spacer(Modifier.height(6.dp))
                MethodSelector(agentMethod) { agentMethod = it }
                Spacer(Modifier.height(6.dp))
                OrderInput(agentContact, { agentContact = it }, "请输入 " + agentMethod + " 联系方式")
            }

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                CardLabel("客户名称", false)
                Spacer(Modifier.height(6.dp))
                OrderInput(customerName, { customerName = it }, "请输入客户名称")
                Spacer(Modifier.height(12.dp))
                CardLabel("客户联系方式", true)
                Spacer(Modifier.height(6.dp))
                MethodSelector(customerMethod) { customerMethod = it }
                Spacer(Modifier.height(6.dp))
                OrderInput(customerContact, { customerContact = it }, "请输入 " + customerMethod + " 联系方式")
            }

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    CardLabel("起点", true)
                    Spacer(modifier = Modifier.weight(1f))
                    AddButton("增加起点") { if (pickupCount < 10) { pickupCount += 1; pickupAddresses = pickupAddresses + "" } }
                }
                Spacer(Modifier.height(6.dp))
                pickupAddresses.forEachIndexed { idx, addr ->
                    AddressInput(addr, { v -> pickupAddresses = pickupAddresses.toMutableList().also { it[idx] = v } }, "请输入起点地址")
                    Spacer(Modifier.height(6.dp))
                }
            }

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    CardLabel("目的地", true)
                    Spacer(modifier = Modifier.weight(1f))
                    AddButton("增加目的地") { if (dropoffCount < 10) { dropoffCount += 1; dropoffAddresses = dropoffAddresses + "" } }
                }
                Spacer(Modifier.height(6.dp))
                dropoffAddresses.forEachIndexed { idx, addr ->
                    AddressInput(addr, { v -> dropoffAddresses = dropoffAddresses.toMutableList().also { it[idx] = v } }, "请输入目的地地址")
                    Spacer(Modifier.height(6.dp))
                }
            }

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                CardLabel("备注（让司机看的）", false)
                Spacer(Modifier.height(6.dp))
                OrderInput(noteForDriver, { noteForDriver = it }, "给司机的备注")
                Spacer(Modifier.height(12.dp))
                CardLabel("备注（让后台看的）", false)
                Spacer(Modifier.height(6.dp))
                OrderInput(noteForBackend, { noteForBackend = it }, "给后台的备注")
            }

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                CardLabel("收钱确认", false)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PillOption("现金", paymentMethod == "现金") { paymentMethod = "现金" }
                    PillOption("ACC", paymentMethod == "ACC") { paymentMethod = "ACC" }
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box {
                        Box(modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(AdminColors.SurfaceVariant).clickable { currencyOpen = true }.padding(horizontal = 14.dp, vertical = 12.dp)) {
                            Text(currency, color = AdminColors.TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        DropdownMenu(expanded = currencyOpen, onDismissRequest = { currencyOpen = false }) {
                            listOf("SGD", "MYR", "USD", "CNY").forEach { c ->
                                DropdownMenuItem(text = { Text(c) }, onClick = { currency = c; currencyOpen = false })
                            }
                        }
                    }
                    OrderInput(amount, { amount = it }, "金额", Modifier.weight(1f))
                }
            }

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                CardLabel("司机确认", false)
                Spacer(Modifier.height(6.dp))
                Box {
                    DropdownField(if (driver.isEmpty()) "请选择负责该订单的司机" else driver, driver.isEmpty()) { driverOpen = true }
                    DropdownMenu(expanded = driverOpen, onDismissRequest = { driverOpen = false }) {
                        mockDrivers.forEach { d -> DropdownMenuItem(text = { Text(d) }, onClick = { driver = d; driverOpen = false }) }
                    }
                }
            }

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                CardLabel("车辆确认", false)
                Spacer(Modifier.height(6.dp))
                Box {
                    DropdownField(if (vehicle.isEmpty()) "请选择负责该订单的车辆" else vehicle, vehicle.isEmpty()) { vehicleOpen = true }
                    DropdownMenu(expanded = vehicleOpen, onDismissRequest = { vehicleOpen = false }) {
                        mockVehicles.forEach { v -> DropdownMenuItem(text = { Text(v) }, onClick = { vehicle = v; vehicleOpen = false }) }
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth().height(52.dp).clip(RoundedCornerShape(999.dp)).background(AdminColors.Primary).clickable { },
                contentAlignment = Alignment.Center
            ) {
                Text("确认订单", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CardLabel(text: String, required: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text, color = AdminColors.TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        if (required) {
            Text(" *", color = AdminColors.Danger, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        } else {
            Text(" *非必填*", color = AdminColors.TextMuted, fontSize = 11.sp)
        }
    }
}

@Composable
private fun MethodSelector(selected: String, onSelect: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        PillOption("WhatsApp", selected == "WhatsApp") { onSelect("WhatsApp") }
        PillOption("WeChat", selected == "WeChat") { onSelect("WeChat") }
    }
}

@Composable
private fun PillOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier.clip(RoundedCornerShape(16.dp)).background(if (selected) AdminColors.Primary else AdminColors.SurfaceVariant).clickable { onClick() }.padding(horizontal = 18.dp, vertical = 8.dp)
    ) {
        Text(label, color = if (selected) Color.White else AdminColors.TextMuted, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun AddButton(contentDescription: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier.size(28.dp).clip(RoundedCornerShape(14.dp)).background(AdminColors.Primary).clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Filled.Add, contentDescription = contentDescription, tint = Color.White, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun OrderInput(value: String, onValueChange: (String) -> Unit, placeholder: String, modifier: Modifier = Modifier.fillMaxWidth()) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = { Text(placeholder, color = AdminColors.TextMuted, fontSize = 13.sp) },
        shape = RoundedCornerShape(12.dp),
        colors = fieldColors(),
        singleLine = true
    )
}

@Composable
private fun AddressInput(value: String, onValueChange: (String) -> Unit, placeholder: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = AdminColors.TextMuted, fontSize = 13.sp) },
        leadingIcon = { Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = AdminColors.Primary, modifier = Modifier.size(20.dp)) },
        shape = RoundedCornerShape(12.dp),
        colors = fieldColors(),
        singleLine = true
    )
}

@Composable
private fun DropdownField(text: String, isPlaceholder: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(AdminColors.SurfaceVariant).clickable { onClick() }.padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Text(text, color = if (isPlaceholder) AdminColors.TextMuted else AdminColors.TextPrimary, fontSize = 13.sp)
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AdminColors.Primary,
    unfocusedBorderColor = AdminColors.Border,
    focusedTextColor = AdminColors.TextPrimary,
    unfocusedTextColor = AdminColors.TextPrimary,
    focusedPlaceholderColor = AdminColors.TextMuted,
    unfocusedPlaceholderColor = AdminColors.TextMuted,
    unfocusedContainerColor = AdminColors.SurfaceVariant,
    focusedContainerColor = AdminColors.SurfaceVariant
)