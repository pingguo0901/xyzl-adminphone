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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellarelite.admin.platform.rememberDatePickerLauncher
import com.stellarelite.admin.ui.components.FormPickerField
import com.stellarelite.admin.ui.components.FormPrimaryButton
import com.stellarelite.admin.ui.components.FormSectionTitle
import com.stellarelite.admin.ui.components.FormTextField
import com.stellarelite.admin.ui.components.SegmentedTabs
import com.stellarelite.admin.ui.components.SubPageHeader
import com.stellarelite.admin.ui.theme.AdminColors

/** KYC 三种证件的显示名 */
private val KycTypeLabels = listOf(
    "ic" to "身份证",
    "license" to "驾照",
    "passport" to "护照",
)

/** 隐私信息页：姓名 / 性别 / 生日 / 居住地 + KYC 认证（复刻司机端，本页仅本地状态） */
@Composable
fun PrivacyInfoScreen(
    onBack: () -> Unit = {},
    inviteCode: String = "",
    companyName: String = "",
) {
    var fullName by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf(GenderOptions.first()) }
    var birthday by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var garden by remember { mutableStateOf("") }
    var postcode by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }

    var hint by remember { mutableStateOf<String?>(null) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogBody by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    val launchBirthdayPicker = rememberDatePickerLauncher { date -> birthday = date }

    val changed = listOf(fullName, birthday, street, garden, postcode, city, state).any { it.isNotBlank() }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        SubPageHeader("隐私信息", onBack = onBack)
        Spacer(Modifier.height(16.dp))

        if (inviteCode.isNotBlank()) {
            FormSectionTitle("邀请码")
            Card(
                Modifier.fillMaxWidth().padding(bottom = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AdminColors.Card),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Column(Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    ReadOnlyRow("邀请码", inviteCode)
                    ReadOnlyRow("归属公司", companyName.ifBlank { "-" })
                }
            }
            Spacer(Modifier.height(18.dp))
        }

        FormTextField(
            label = "姓名",
            value = fullName,
            placeholder = "仅英文和空格",
            onValueChange = { fullName = sanitizeEnglishName(it) },
        )

        FormSectionTitle("性别")
        SegmentedTabs(
            options = GenderOptions,
            selectedIndex = GenderOptions.indexOf(gender).coerceAtLeast(0),
            onSelect = { gender = GenderOptions[it] },
        )
        Spacer(Modifier.height(18.dp))

        FormPickerField(
            label = "生日日期",
            text = birthday,
            onClick = { launchBirthdayPicker() },
        )

        FormSectionTitle("居住地")
        FormTextField(
            label = "门牌及街道",
            value = street,
            placeholder = "英文 / 数字 / 标点",
            onValueChange = { street = sanitizeEnglishPunct(it) },
        )
        FormTextField(
            label = "花园",
            value = garden,
            placeholder = "英文 / 数字 / 标点",
            onValueChange = { garden = sanitizeEnglishPunct(it) },
        )
        FormTextField(
            label = "邮编",
            value = postcode,
            placeholder = "仅数字",
            onValueChange = { postcode = sanitizeDigits(it) },
            keyboardNumber = true,
        )
        FormTextField(
            label = "城市",
            value = city,
            placeholder = "仅英文",
            onValueChange = { city = sanitizeEnglish(it) },
        )
        FormTextField(
            label = "州属",
            value = state,
            placeholder = "仅英文",
            onValueChange = { state = sanitizeEnglish(it) },
        )

        FormSectionTitle("KYC认证")
        Card(
            Modifier.fillMaxWidth().padding(bottom = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AdminColors.Card),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                KycTypeLabels.forEach { (_, label) ->
                    KycStatusRow(
                        name = label,
                        valid = false,
                        detail = "未上传",
                        onUpdate = { hint = "KYC 证件更新需要接入后台上传流程，本页目前只做界面。" },
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        FormPrimaryButton(
            text = "保存更改",
            enabled = changed,
            onClick = {
                dialogTitle = "保存成功"
                dialogBody = "隐私信息已更新"
                showDialog = true
            },
        )
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

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = AdminColors.Card,
            title = { Text(dialogTitle, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = AdminColors.TextPrimary) },
            text = { Text(dialogBody, fontSize = 14.sp, color = AdminColors.TextMuted) },
            confirmButton = {
                TextButton(onClick = {
                    val success = dialogTitle == "保存成功"
                    showDialog = false
                    if (success) onBack()
                }) {
                    Text("确定", color = AdminColors.Primary, fontWeight = FontWeight.SemiBold)
                }
            },
        )
    }
}

/** 只读信息行（标签 + 值） */
@Composable
private fun ReadOnlyRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, fontSize = 13.sp, color = AdminColors.TextMuted, modifier = Modifier.weight(1f))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = AdminColors.TextPrimary)
    }
}

/** KYC 证件状态行：有效 / 更新 */
@Composable
private fun KycStatusRow(name: String, valid: Boolean, detail: String, onUpdate: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(name, fontSize = 15.sp, color = AdminColors.TextPrimary)
            Text(detail, fontSize = 12.sp, color = AdminColors.TextMuted)
        }
        if (valid) {
            Text("有效", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = AdminColors.Primary)
        } else {
            Text(
                "更新",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFF43F5E),
                modifier = Modifier.clickable { onUpdate() },
            )
        }
    }
}
