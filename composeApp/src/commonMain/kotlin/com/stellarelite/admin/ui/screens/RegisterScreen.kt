package com.stellarelite.admin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellarelite.admin.platform.MediaSource
import com.stellarelite.admin.platform.PickedMedia
import com.stellarelite.admin.platform.rememberDatePickerLauncher
import com.stellarelite.admin.platform.rememberMediaPicker
import com.stellarelite.admin.ui.theme.AdminColors

// ─── 输入过滤正则 ───
private val reCjkEnNumPunct = Regex("[\\u4e00-\\u9fa5A-Za-z0-9\\p{P}\\p{S}]") // 中文/英文/数字/特殊标点
private val reEnSpace = Regex("[A-Za-z ]")                                        // 英文+空格
private val reDigit = Regex("[0-9]")                                              // 数字
private val reEn = Regex("[A-Za-z]")                                              // 英文
private val reEnNumPunct = Regex("[A-Za-z0-9\\p{P}\\p{S}]")                    // 英文/数字/特殊标点

private fun filterText(raw: String, pattern: Regex): String =
    raw.filter { pattern.matches(it.toString()) }

// ─── 区号列表 ───
private val countryCodes = listOf(
    "+60", "+65", "+86", "+852", "+853", "+886",
    "+1", "+44", "+61", "+81", "+82", "+66"
)

@Composable
fun RegisterScreen(
    onRegister: () -> Unit,
    onBack: () -> Unit
) {
    // 基础
    var inviteCode by remember { mutableStateOf("") }
    var businessType by remember { mutableStateOf("独资企业") }
    var nickname by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }
    var regNumber by remember { mutableStateOf("") }
    var regDate by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }

    // 企业营业地址
    var bizStreet by remember { mutableStateOf("") }
    var bizGarden by remember { mutableStateOf("") }
    var bizPostcode by remember { mutableStateOf("") }
    var bizState by remember { mutableStateOf("") }

    // 持有人居住地
    var holderStreet by remember { mutableStateOf("") }
    var holderGarden by remember { mutableStateOf("") }
    var holderPostcode by remember { mutableStateOf("") }
    var holderState by remember { mutableStateOf("") }

    // 企业联系方式
    var bizPhoneCode by remember { mutableStateOf("+60") }
    var bizPhone by remember { mutableStateOf("") }
    var bizEmail by remember { mutableStateOf("") }
    var bizWebsite by remember { mutableStateOf("") }
    var bizWechat by remember { mutableStateOf("") }

    // 持有人联系方式
    var holderPhoneCode by remember { mutableStateOf("+60") }
    var holderPhone by remember { mutableStateOf("") }
    var holderEmail by remember { mutableStateOf("") }
    var holderWebsite by remember { mutableStateOf("") }
    var holderWechat by remember { mutableStateOf("") }

    // 上传
    var ssmFile by remember { mutableStateOf<PickedMedia?>(null) }
    var icFile by remember { mutableStateOf<PickedMedia?>(null) }
    var licenseFile by remember { mutableStateOf<PickedMedia?>(null) }
    var agreed by remember { mutableStateOf(false) }

    // 弹窗/错误
    var dateTarget by remember { mutableStateOf<String?>(null) }
    var showLicenseSource by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val hasInviteCode = inviteCode.isNotBlank()

    val launchDate = rememberDatePickerLauncher { date ->
        when (dateTarget) {
            "reg" -> regDate = date
            "start" -> startDate = date
        }
        dateTarget = null
    }
    val launchMedia = rememberMediaPicker()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AdminColors.Background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "返回",
                tint = AdminColors.TextPrimary,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onBack() }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("注册账号", color = AdminColors.TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Black)
        }
        Spacer(modifier = Modifier.height(24.dp))

        // ─── 邀请码 ───
        SectionTitle("邀请码")
        TextInputField(
            value = inviteCode,
            onValueChange = { inviteCode = filterText(it, reEnNumPunct) },
            placeholder = "请输入邀请码（选填）",
            keyboardType = KeyboardType.Text
        )
        Spacer(modifier = Modifier.height(20.dp))

        if (!hasInviteCode) {
            // ─── 企业类型 ───
            SectionTitle("企业类型")
            CapsuleToggle(
                options = listOf("独资企业", "融资企业"),
                selected = businessType,
                onSelect = { businessType = it }
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // ─── 昵称 ───
        SectionTitle("昵称")
        TextInputField(
            value = nickname,
            onValueChange = { nickname = filterText(it, reCjkEnNumPunct) },
            placeholder = "请输入昵称",
            keyboardType = KeyboardType.Text
        )
        Spacer(modifier = Modifier.height(20.dp))

        if (!hasInviteCode) {
            // ─── 企业名称 ───
            SectionTitle("企业名称")
            TextInputField(
                value = companyName,
                onValueChange = { companyName = filterText(it, reEnSpace) },
                placeholder = "请输入企业名称（英文）",
                keyboardType = KeyboardType.Text
            )
            Spacer(modifier = Modifier.height(20.dp))

            // ─── 注册编号 ───
            SectionTitle("注册编号")
            TextInputField(
                value = regNumber,
                onValueChange = { regNumber = filterText(it, reDigit) },
                placeholder = "请输入注册编号（数字）",
                keyboardType = KeyboardType.Number
            )
            Spacer(modifier = Modifier.height(20.dp))

            // ─── 日期 ───
            SectionTitle("企业注册日期")
            DateField(value = regDate, placeholder = "选择注册日期") { dateTarget = "reg"; launchDate() }
            Spacer(modifier = Modifier.height(14.dp))

            SectionTitle("企业开始日期")
            DateField(value = startDate, placeholder = "选择开始日期") { dateTarget = "start"; launchDate() }
            Spacer(modifier = Modifier.height(20.dp))

            // ─── 企业营业地址 ───
            SectionTitle("企业营业地址")
            AddressFields(
                street = bizStreet, onStreet = { bizStreet = filterText(it, reEnNumPunct) },
                garden = bizGarden, onGarden = { bizGarden = filterText(it, reEnNumPunct) },
                postcode = bizPostcode, onPostcode = { bizPostcode = filterText(it, reDigit) },
                state = bizState, onState = { bizState = filterText(it, reEn) }
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // ─── 持有人居住地（有邀请码时去掉「持有人」字样） ───
        SectionTitle(if (hasInviteCode) "居住地" else "持有人居住地")
        AddressFields(
            street = holderStreet, onStreet = { holderStreet = filterText(it, reEnNumPunct) },
            garden = holderGarden, onGarden = { holderGarden = filterText(it, reEnNumPunct) },
            postcode = holderPostcode, onPostcode = { holderPostcode = filterText(it, reDigit) },
            state = holderState, onState = { holderState = filterText(it, reEn) }
        )
        Spacer(modifier = Modifier.height(20.dp))

        if (!hasInviteCode) {
            // ─── 企业手机号 ───
            SectionTitle("企业手机号")
            PhoneField(
                code = bizPhoneCode,
                onCodeChange = { bizPhoneCode = it },
                number = bizPhone,
                onNumberChange = { bizPhone = filterText(it, reDigit) }
            )
            Spacer(modifier = Modifier.height(20.dp))

            // ─── 企业邮箱 ───
            SectionTitle("企业邮箱地址")
            TextInputField(
                value = bizEmail,
                onValueChange = { bizEmail = filterText(it, reEnNumPunct) },
                placeholder = "请输入企业邮箱",
                keyboardType = KeyboardType.Email
            )
            Spacer(modifier = Modifier.height(14.dp))

            // ─── 企业官网 ───
            SectionTitle("企业官网网址")
            TextInputField(
                value = bizWebsite,
                onValueChange = { bizWebsite = filterText(it, reEnNumPunct) },
                placeholder = "请输入企业官网网址",
                keyboardType = KeyboardType.Uri
            )
            Spacer(modifier = Modifier.height(14.dp))

            // ─── 企业微信号 ───
            SectionTitle("企业微信号")
            TextInputField(
                value = bizWechat,
                onValueChange = { bizWechat = filterText(it, reEnNumPunct) },
                placeholder = "请输入企业微信号",
                keyboardType = KeyboardType.Text
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // ─── 持有人手机号（有邀请码时「手机号」） ───
        SectionTitle(if (hasInviteCode) "手机号" else "持有人手机号")
        PhoneField(
            code = holderPhoneCode,
            onCodeChange = { holderPhoneCode = it },
            number = holderPhone,
            onNumberChange = { holderPhone = filterText(it, reDigit) }
        )
        Spacer(modifier = Modifier.height(20.dp))

        // ─── 持有人邮箱 ───
        SectionTitle(if (hasInviteCode) "邮箱地址" else "持有人邮箱地址")
        TextInputField(
            value = holderEmail,
            onValueChange = { holderEmail = filterText(it, reEnNumPunct) },
            placeholder = "请输入邮箱地址",
            keyboardType = KeyboardType.Email
        )
        Spacer(modifier = Modifier.height(14.dp))

        // ─── 持有人官网 ───
        SectionTitle(if (hasInviteCode) "官网网址" else "持有人官网网址")
        TextInputField(
            value = holderWebsite,
            onValueChange = { holderWebsite = filterText(it, reEnNumPunct) },
            placeholder = "请输入官网网址",
            keyboardType = KeyboardType.Uri
        )
        Spacer(modifier = Modifier.height(14.dp))

        // ─── 持有人微信号（有邀请码时「微信号」） ───
        SectionTitle(if (hasInviteCode) "微信号" else "持有人微信号")
        TextInputField(
            value = holderWechat,
            onValueChange = { holderWechat = filterText(it, reEnNumPunct) },
            placeholder = "请输入微信号",
            keyboardType = KeyboardType.Text
        )
        Spacer(modifier = Modifier.height(20.dp))

        if (!hasInviteCode) {
            // ─── 企业认证 ───
            SectionTitle("企业认证")
            UploadBox(
                title = "上传马来西亚 SSM 合规文档 Borang A",
                file = ssmFile,
                onClick = {
                    launchMedia(MediaSource.DOCUMENT) { ssmFile = it }
                },
                onClear = { ssmFile = null }
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // ─── KYC 认证 ───
        SectionTitle("KYC 认证")
        UploadBox(
            title = if (hasInviteCode) "上传马来西亚身份证" else "上传持有人马来西亚身份证",
            file = icFile,
            onClick = {
                launchMedia(MediaSource.CAMERA) { icFile = it }
            },
            onClear = { icFile = null }
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (!hasInviteCode) {
            UploadBox(
                title = "上传持有人马来西亚驾照",
                file = licenseFile,
                onClick = { showLicenseSource = true },
                onClear = { licenseFile = null }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // ─── KYC 协议 ───
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { agreed = !agreed }
        ) {
            Checkbox(
                checked = agreed,
                onCheckedChange = { agreed = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = AdminColors.Primary,
                    uncheckedColor = AdminColors.TextMuted,
                    checkmarkColor = Color.White
                )
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                "我已阅读并同意《KYC 实名认证服务政策》",
                color = AdminColors.TextSecondary,
                fontSize = 13.sp
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        // ─── 注册按钮 ───
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(AdminColors.Primary)
                .clickable {
                    val missing = validate(
                        hasInviteCode = hasInviteCode,
                        nickname = nickname, companyName = companyName, regNumber = regNumber,
                        regDate = regDate, startDate = startDate,
                        bizStreet = bizStreet, bizGarden = bizGarden, bizPostcode = bizPostcode, bizState = bizState,
                        holderStreet = holderStreet, holderGarden = holderGarden, holderPostcode = holderPostcode, holderState = holderState,
                        bizPhone = bizPhone, bizEmail = bizEmail, bizWebsite = bizWebsite, bizWechat = bizWechat,
                        holderPhone = holderPhone, holderEmail = holderEmail, holderWebsite = holderWebsite, holderWechat = holderWechat,
                        ssmFile = ssmFile, icFile = icFile, licenseFile = licenseFile, agreed = agreed
                    )
                    if (missing == null) onRegister() else errorMsg = missing
                }
                .padding(vertical = 15.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("注 册", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(40.dp))
    }

    // ─── 驾照来源选择 ───
    if (showLicenseSource) {
        AlertDialog(
            onDismissRequest = { showLicenseSource = false },
            containerColor = AdminColors.Card,
            title = { Text("选择上传方式", color = AdminColors.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    SourceRow("拍照") { showLicenseSource = false; launchMedia(MediaSource.CAMERA) { licenseFile = it } }
                    Spacer(modifier = Modifier.height(8.dp))
                    SourceRow("相册照片") { showLicenseSource = false; launchMedia(MediaSource.GALLERY) { licenseFile = it } }
                    Spacer(modifier = Modifier.height(8.dp))
                    SourceRow("文档") { showLicenseSource = false; launchMedia(MediaSource.DOCUMENT) { licenseFile = it } }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { showLicenseSource = false }) { Text("取消", color = AdminColors.TextMuted) } }
        )
    }

    // ─── 校验错误弹窗 ───
    if (errorMsg != null) {
        AlertDialog(
            onDismissRequest = { errorMsg = null },
            containerColor = AdminColors.Card,
            title = { Text("请补充以下信息", color = AdminColors.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = { Text(errorMsg!!, color = AdminColors.TextSecondary, fontSize = 14.sp) },
            confirmButton = { TextButton(onClick = { errorMsg = null }) { Text("知道了", color = AdminColors.Primary) } }
        )
    }
}

// ─── 校验逻辑 ───
private fun validate(
    hasInviteCode: Boolean,
    nickname: String, companyName: String, regNumber: String, regDate: String, startDate: String,
    bizStreet: String, bizGarden: String, bizPostcode: String, bizState: String,
    holderStreet: String, holderGarden: String, holderPostcode: String, holderState: String,
    bizPhone: String, bizEmail: String, bizWebsite: String, bizWechat: String,
    holderPhone: String, holderEmail: String, holderWebsite: String, holderWechat: String,
    ssmFile: PickedMedia?, icFile: PickedMedia?, licenseFile: PickedMedia?, agreed: Boolean
): String? {
    val missing = mutableListOf<String>()
    if (hasInviteCode) {
        if (nickname.isBlank()) missing += "昵称"
        if (holderStreet.isBlank()) missing += "居住地·门牌及街道"
        if (holderGarden.isBlank()) missing += "居住地·花园"
        if (holderPostcode.isBlank()) missing += "居住地·邮编"
        if (holderState.isBlank()) missing += "居住地·州属"
        if (holderPhone.isBlank()) missing += "手机号"
        if (holderEmail.isBlank()) missing += "邮箱地址"
        if (holderWebsite.isBlank()) missing += "官网网址"
        if (holderWechat.isBlank()) missing += "微信号"
        if (icFile == null) missing += "马来西亚身份证"
    } else {
        if (nickname.isBlank()) missing += "昵称"
        if (companyName.isBlank()) missing += "企业名称"
        if (regNumber.isBlank()) missing += "注册编号"
        if (regDate.isBlank()) missing += "企业注册日期"
        if (startDate.isBlank()) missing += "企业开始日期"
        if (bizStreet.isBlank()) missing += "企业营业地址·门牌及街道"
        if (bizGarden.isBlank()) missing += "企业营业地址·花园"
        if (bizPostcode.isBlank()) missing += "企业营业地址·邮编"
        if (bizState.isBlank()) missing += "企业营业地址·州属"
        if (holderStreet.isBlank()) missing += "持有人居住地·门牌及街道"
        if (holderGarden.isBlank()) missing += "持有人居住地·花园"
        if (holderPostcode.isBlank()) missing += "持有人居住地·邮编"
        if (holderState.isBlank()) missing += "持有人居住地·州属"
        if (bizPhone.isBlank()) missing += "企业手机号"
        if (bizEmail.isBlank()) missing += "企业邮箱地址"
        if (bizWebsite.isBlank()) missing += "企业官网网址"
        if (holderPhone.isBlank()) missing += "持有人手机号"
        if (holderEmail.isBlank()) missing += "持有人邮箱地址"
        if (holderWebsite.isBlank()) missing += "持有人官网网址"
        if (bizWechat.isBlank() && holderWechat.isBlank()) missing += "企业微信号或持有人微信号（至少填一个）"
        if (ssmFile == null) missing += "企业认证 Borang A"
        if (icFile == null) missing += "马来西亚身份证"
        if (licenseFile == null) missing += "马来西亚驾照"
    }
    if (!agreed) missing += "KYC 协议勾选"
    return if (missing.isEmpty()) null else "以下为必填项：\n" + missing.joinToString("、")
}

// ─── 分组标题 ───
@Composable
private fun SectionTitle(title: String) {
    Text(
        title,
        color = AdminColors.TextSecondary,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(8.dp))
}

// ─── 文本输入框 ───
@Composable
private fun TextInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AdminColors.Card)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        if (value.isEmpty()) {
            Text(placeholder, color = AdminColors.TextDisabled, fontSize = 15.sp)
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(color = AdminColors.TextPrimary, fontSize = 15.sp),
            cursorBrush = SolidColor(AdminColors.Primary),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
        )
    }
}

// ─── 日期选择字段 ───
@Composable
private fun DateField(value: String, placeholder: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AdminColors.Card)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                if (value.isEmpty()) placeholder else value,
                color = if (value.isEmpty()) AdminColors.TextDisabled else AdminColors.TextPrimary,
                fontSize = 15.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Outlined.CalendarMonth,
                contentDescription = "选择日期",
                tint = AdminColors.TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// ─── 手机号字段（区号弹窗 + 数字输入） ───
@Composable
private fun PhoneField(
    code: String,
    onCodeChange: (String) -> Unit,
    number: String,
    onNumberChange: (String) -> Unit
) {
    var codeMenu by remember { mutableStateOf(false) }
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        // 区号选择
        Box {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(AdminColors.Card)
                    .clickable { codeMenu = true }
                    .padding(horizontal = 14.dp, vertical = 14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(code, color = AdminColors.TextPrimary, fontSize = 15.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        Icons.Outlined.KeyboardArrowDown,
                        contentDescription = "选择区号",
                        tint = AdminColors.TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            DropdownMenu(expanded = codeMenu, onDismissRequest = { codeMenu = false }, containerColor = AdminColors.CardAlt) {
                countryCodes.forEach { c ->
                    DropdownMenuItem(
                        text = { Text(c, color = AdminColors.TextPrimary, fontSize = 15.sp) },
                        onClick = { onCodeChange(c); codeMenu = false }
                    )
                }
            }
        }
        // 号码输入
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(AdminColors.Card)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            if (number.isEmpty()) {
                Text("请输入号码", color = AdminColors.TextDisabled, fontSize = 15.sp)
            }
            BasicTextField(
                value = number,
                onValueChange = onNumberChange,
                singleLine = true,
                textStyle = TextStyle(color = AdminColors.TextPrimary, fontSize = 15.sp),
                cursorBrush = SolidColor(AdminColors.Primary),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
        }
    }
}

// ─── 地址字段组 ───
@Composable
private fun AddressFields(
    street: String, onStreet: (String) -> Unit,
    garden: String, onGarden: (String) -> Unit,
    postcode: String, onPostcode: (String) -> Unit,
    state: String, onState: (String) -> Unit
) {
    TextInputField(street, onStreet, "门牌及街道", KeyboardType.Text)
    Spacer(modifier = Modifier.height(10.dp))
    TextInputField(garden, onGarden, "花园", KeyboardType.Text)
    Spacer(modifier = Modifier.height(10.dp))
    TextInputField(postcode, onPostcode, "邮编", KeyboardType.Number)
    Spacer(modifier = Modifier.height(10.dp))
    TextInputField(state, onState, "州属", KeyboardType.Text)
}

// ─── 上传框 ───
@Composable
private fun UploadBox(
    title: String,
    file: PickedMedia?,
    onClick: () -> Unit,
    onClear: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AdminColors.Card)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        if (file == null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.CloudUpload,
                    contentDescription = "上传",
                    tint = AdminColors.Primary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(title, color = AdminColors.TextSecondary, fontSize = 14.sp)
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "已上传：" + file.fileName,
                    color = AdminColors.Success,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f),
                    maxLines = 1
                )
                Text(
                    "重选",
                    color = AdminColors.Primary,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { onClick() }
                )
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    "移除",
                    color = AdminColors.Danger,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { onClear() }
                )
            }
        }
    }
}

// ─── 驾照来源选项 ───
@Composable
private fun SourceRow(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(AdminColors.SurfaceVariant)
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Text(label, color = AdminColors.TextPrimary, fontSize = 15.sp)
    }
}

// ─── 胶囊选项 ───
@Composable
private fun CapsuleToggle(options: List<String>, selected: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(999.dp))
            .background(AdminColors.Card)
            .padding(4.dp)
    ) {
        options.forEach { opt ->
            val isSel = opt == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (isSel) AdminColors.Primary else Color.Transparent)
                    .clickable { onSelect(opt) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    opt,
                    color = if (isSel) Color.White else AdminColors.TextMuted,
                    fontSize = 15.sp,
                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

