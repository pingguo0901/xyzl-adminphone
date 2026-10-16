package com.stellarelite.admin.ui.screens

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import com.stellarelite.admin.ui.components.FormCheckRow
import com.stellarelite.admin.ui.components.FormNote
import com.stellarelite.admin.ui.components.FormPageHeaderAction
import com.stellarelite.admin.ui.components.FormPrimaryButton
import com.stellarelite.admin.ui.components.FormSectionTitle
import com.stellarelite.admin.ui.components.FormSelect
import com.stellarelite.admin.ui.components.FormTextField
import com.stellarelite.admin.ui.components.FormUpload
import com.stellarelite.admin.ui.components.SegmentedTabs
import com.stellarelite.admin.ui.components.SubPageHeader
import com.stellarelite.admin.ui.theme.AdminColors

private val OwnerOptions = listOf("是", "否")

/** 车辆卡片（本机演示数据） */
private data class VehicleCard(
    val brand: String,
    val model: String,
    val trim: String,
    val plate: String,
    val verifyAt: String? = null,
    val verifiedAt: String? = null,
    val status: String = "approved",
)

private val approvedVehicles = listOf(
    VehicleCard("Toyota", "Vios", "1.5G", "WXY 1234"),
)

private val pendingVehicles = listOf(
    VehicleCard("Honda", "City", "VTi-E", "WYY 5678", verifyAt = "2026-10-14 10:20", verifiedAt = null, status = "pending"),
)

/** 品牌 → 型号（增加车辆页选择用） */
private val BrandModels: Map<String, List<String>> = mapOf(
    "Toyota" to listOf("Vios", "Hilux", "Camry"),
    "Honda" to listOf("City", "Civic", "HR-V"),
    "Perodua" to listOf("Axia", "Myvi", "Bezza"),
    "Proton" to listOf("Saga", "X50"),
    "Nissan" to listOf("Almera", "X-Trail"),
)

/** 型号 → 车款 */
private val ModelTrims: Map<String, List<String>> = mapOf(
    "Vios" to listOf("1.5J", "1.5E", "1.5G"),
    "Hilux" to listOf("2.4V", "2.8 Rogue"),
    "Camry" to listOf("2.5V", "2.5 Hybrid"),
    "City" to listOf("S", "E", "VTi-E"),
    "Civic" to listOf("1.5 TC", "1.5 TC-P"),
    "HR-V" to listOf("S", "E", "V"),
    "Axia" to listOf("G", "SE", "AV"),
    "Myvi" to listOf("1.3 G", "1.5 H", "1.5 AV"),
    "Bezza" to listOf("1.0 G", "1.3 AV"),
    "Saga" to listOf("Standard", "Premium"),
    "X50" to listOf("Standard", "Flagship"),
    "Almera" to listOf("VL", "VLT"),
    "X-Trail" to listOf("2.0 2WD", "2.5 4WD"),
)

private fun statusLabel(status: String?): String = when (status) {
    "approved" -> "已通过"
    "pending" -> "审核中"
    "rejected" -> "已拒绝"
    else -> status ?: "-"
}

private fun prettyTime(raw: String?): String = raw?.takeIf { it.isNotBlank() } ?: "-"

/** 车辆信息页：我的车辆（已通过）+ 待通过验证（复刻司机端，本页仅本地状态） */
@Composable
fun VehicleInfoScreen(
    onBack: () -> Unit = {},
    isCompanyDriver: Boolean = false,
) {
    var showAdd by remember { mutableStateOf(false) }

    if (isCompanyDriver) {
        CompanyVehicleView(onBack = onBack)
        return
    }

    if (showAdd) {
        AddVehicleScreen(onBack = { showAdd = false })
        return
    }

    MyVehiclesView(onBack = onBack, onAdd = { showAdd = true })
}

/** 公司司机：已授权车辆（公司分配） */
@Composable
private fun CompanyVehicleView(onBack: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        SubPageHeader("车辆信息", onBack = onBack)
        Spacer(Modifier.height(16.dp))
        Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = AdminColors.Card),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Column(Modifier.fillMaxWidth().padding(18.dp)) {
                Text("已授权车辆", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = AdminColors.TextPrimary)
                Spacer(Modifier.height(12.dp))
                Text("请向所属公司索取授权车辆", fontSize = 14.sp, color = AdminColors.TextMuted)
            }
        }
        Spacer(Modifier.height(110.dp))
    }
}

/** 个人：我的车辆 + 待通过验证车辆 */
@Composable
private fun MyVehiclesView(
    onBack: () -> Unit,
    onAdd: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeaderAction(title = "车辆信息", actionLabel = "增加车辆", onBack = onBack, onAction = onAdd)
        Spacer(Modifier.height(16.dp))

        Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = AdminColors.Card),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Column(Modifier.fillMaxWidth().padding(18.dp)) {
                Text("我的车辆", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = AdminColors.TextPrimary)
                Spacer(Modifier.height(12.dp))
                if (approvedVehicles.isEmpty()) {
                    Text("暂无已验证车辆", fontSize = 14.sp, color = AdminColors.TextMuted)
                } else {
                    approvedVehicles.forEachIndexed { index, vehicle ->
                        VehicleInfoLines(vehicle)
                        if (index != approvedVehicles.lastIndex) Spacer(Modifier.height(12.dp))
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        HorizontalDivider(color = AdminColors.SurfaceVariant, thickness = 1.dp)
        Spacer(Modifier.height(20.dp))

        Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = AdminColors.Card),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Column(Modifier.fillMaxWidth().padding(18.dp)) {
                Text("待通过验证车辆", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = AdminColors.TextPrimary)
                Spacer(Modifier.height(12.dp))
                if (pendingVehicles.isEmpty()) {
                    Text("暂无待验证车辆", fontSize = 14.sp, color = AdminColors.TextMuted)
                } else {
                    pendingVehicles.forEachIndexed { index, vehicle ->
                        VehicleInfoLines(vehicle)
                        Spacer(Modifier.height(8.dp))
                        InfoLine("验证日期时间", prettyTime(vehicle.verifyAt))
                        InfoLine("通过日期时间", prettyTime(vehicle.verifiedAt))
                        InfoLine("状态", statusLabel(vehicle.status))
                        if (index != pendingVehicles.lastIndex) Spacer(Modifier.height(14.dp))
                    }
                }
            }
        }

        Spacer(Modifier.height(110.dp))
    }
}

@Composable
private fun VehicleInfoLines(vehicle: VehicleCard) {
    InfoLine("品牌", vehicle.brand)
    InfoLine("型号", vehicle.model)
    InfoLine("车款", vehicle.trim)
    InfoLine("车牌", vehicle.plate)
}

@Composable
private fun InfoLine(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, fontSize = 13.sp, color = AdminColors.TextMuted, modifier = Modifier.weight(1f))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = AdminColors.TextPrimary)
    }
}

/** 增加车辆页：品牌/型号/车款下拉 + 车牌 + 4 张照片 + 3 份证明 + 车主身份 */
@Composable
fun AddVehicleScreen(onBack: () -> Unit = {}) {
    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var trim by remember { mutableStateOf("") }
    var plate by remember { mutableStateOf("") }

    var frontPhoto by remember { mutableStateOf<String?>(null) }
    var backPhoto by remember { mutableStateOf<String?>(null) }
    var frontSeatPhoto by remember { mutableStateOf<String?>(null) }
    var backSeatPhoto by remember { mutableStateOf<String?>(null) }
    var roadTaxFile by remember { mutableStateOf<String?>(null) }
    var regCardFile by remember { mutableStateOf<String?>(null) }
    var insuranceFile by remember { mutableStateOf<String?>(null) }

    var isOwnerAnswer by remember { mutableStateOf(OwnerOptions.first()) }
    var ownerIcFile by remember { mutableStateOf<String?>(null) }
    var ownerIcUnavailable by remember { mutableStateOf(false) }

    var dialogTitle by remember { mutableStateOf("") }
    var dialogBody by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    val isOwner = isOwnerAnswer == "是"
    val models = BrandModels[brand].orEmpty()
    val trims = ModelTrims[model].orEmpty()

    val ready = brand.isNotBlank() && model.isNotBlank() && plate.isNotBlank() &&
        frontPhoto != null && backPhoto != null && frontSeatPhoto != null && backSeatPhoto != null &&
        roadTaxFile != null && regCardFile != null && insuranceFile != null &&
        (isOwner || ownerIcFile != null || ownerIcUnavailable)

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        SubPageHeader("增加车辆", onBack = onBack)
        Spacer(Modifier.height(16.dp))

        Column(Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState())) {

            FormNote("车辆品牌 / 型号 / 车款为演示数据；实际由后台车辆库下发。")
            Spacer(Modifier.height(10.dp))

            FormSelect(
                label = "品牌",
                value = brand,
                placeholder = "选择品牌",
                options = BrandModels.keys.toList(),
                onSelect = { picked ->
                    brand = picked
                    model = ""
                    trim = ""
                },
            )
            FormSelect(
                label = "型号",
                value = model,
                placeholder = "选择型号",
                options = models,
                onSelect = { picked ->
                    model = picked
                    trim = ""
                },
            )
            FormSelect(
                label = "车款",
                value = trim,
                placeholder = "选择车款",
                options = trims,
                onSelect = { trim = it },
            )
            FormTextField(
                label = "车牌号",
                value = plate,
                placeholder = "例如 WXY 1234",
                onValueChange = { plate = sanitizePlate(it) },
            )

            FormSectionTitle("车辆照片")
            FormUpload(label = "车前照片", hint = "拍照或从相册选择", uri = frontPhoto, onPicked = { frontPhoto = it })
            FormUpload(label = "车后照片", hint = "拍照或从相册选择", uri = backPhoto, onPicked = { backPhoto = it })
            FormUpload(label = "车前排照片", hint = "拍照或从相册选择", uri = frontSeatPhoto, onPicked = { frontSeatPhoto = it })
            FormUpload(label = "车后排照片", hint = "拍照或从相册选择", uri = backSeatPhoto, onPicked = { backSeatPhoto = it })

            FormSectionTitle("车辆证明")
            FormUpload(label = "路税证明", hint = "拍照或上传文件", uri = roadTaxFile, onPicked = { roadTaxFile = it })
            FormUpload(label = "车卡证明", hint = "拍照或上传文件", uri = regCardFile, onPicked = { regCardFile = it })
            FormUpload(label = "保险证明", hint = "拍照或上传文件", uri = insuranceFile, onPicked = { insuranceFile = it })

            FormSectionTitle("是否车主")
            SegmentedTabs(
                options = OwnerOptions,
                selectedIndex = OwnerOptions.indexOf(isOwnerAnswer).coerceAtLeast(0),
                onSelect = { isOwnerAnswer = OwnerOptions[it] },
            )
            Spacer(Modifier.height(14.dp))

            if (!isOwner) {
                FormUpload(
                    label = "车主马来西亚身份证",
                    hint = "仅拍照上传",
                    uri = ownerIcFile,
                    onPicked = { ownerIcFile = it },
                    allowDocument = false,
                )
                FormCheckRow(
                    label = "我无法获取该车主身份证",
                    selected = ownerIcUnavailable,
                    onClick = { ownerIcUnavailable = !ownerIcUnavailable },
                )
            }

            Spacer(Modifier.height(6.dp))
        }

        FormPrimaryButton(
            text = "完成提交",
            enabled = ready,
            onClick = {
                if (brand.isBlank() || model.isBlank()) {
                    dialogTitle = "资料没填全"
                    dialogBody = "品牌和型号都要选"
                } else {
                    dialogTitle = "提交成功"
                    dialogBody = "车辆资料已提交，等待审核"
                }
                showDialog = true
            },
        )
        Spacer(Modifier.height(16.dp))
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = AdminColors.Card,
            title = { Text(dialogTitle, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = AdminColors.TextPrimary) },
            text = { Text(dialogBody, fontSize = 14.sp, color = AdminColors.TextMuted) },
            confirmButton = {
                TextButton(onClick = {
                    val success = dialogTitle == "提交成功"
                    showDialog = false
                    if (success) onBack()
                }) {
                    Text("确定", color = AdminColors.Primary, fontWeight = FontWeight.SemiBold)
                }
            },
        )
    }
}
