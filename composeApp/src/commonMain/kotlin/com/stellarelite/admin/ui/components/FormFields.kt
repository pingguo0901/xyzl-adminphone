package com.stellarelite.admin.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.stellarelite.admin.platform.MediaSource
import com.stellarelite.admin.platform.rememberMediaPicker
import com.stellarelite.admin.ui.theme.AdminColors

// ─── 表单控件（复刻司机端 FormControls 用法，配色走后台端主题） ───

/** 输入框配色：焦点边框 = 品牌蓝，底色 = 卡片色 */
@Composable
internal fun formFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AdminColors.Primary,
    unfocusedBorderColor = AdminColors.SurfaceVariant,
    focusedTextColor = AdminColors.TextPrimary,
    unfocusedTextColor = AdminColors.TextPrimary,
    cursorColor = AdminColors.Primary,
    focusedContainerColor = AdminColors.Card,
    unfocusedContainerColor = AdminColors.Card,
    focusedPlaceholderColor = AdminColors.TextDisabled,
    unfocusedPlaceholderColor = AdminColors.TextDisabled,
)

/** 字段标题（输入框上方） */
@Composable
fun FormLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = AdminColors.TextMuted,
        modifier = modifier.padding(start = 2.dp, bottom = 8.dp),
    )
}

/** 普通输入框 */
@Composable
fun FormTextField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardNumber: Boolean = false,
) {
    Column(modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        FormLabel(label)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = AdminColors.TextMuted) },
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (keyboardNumber) KeyboardType.Number else KeyboardType.Text,
            ),
            colors = formFieldColors(),
        )
    }
}

/** 弹出式选项（下拉） */
@Composable
fun FormSelect(
    label: String,
    value: String,
    placeholder: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        FormLabel(label)
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(AdminColors.Card)
                    .clickable { expanded = true }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = value.ifBlank { placeholder },
                    color = if (value.isBlank()) AdminColors.TextMuted else AdminColors.TextPrimary,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = AdminColors.TextMuted,
                    modifier = Modifier.size(20.dp),
                )
            }
            if (expanded) {
                Popup(
                    onDismissRequest = { expanded = false },
                    properties = PopupProperties(focusable = true),
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = AdminColors.Card,
                        shadowElevation = 8.dp,
                    ) {
                        Column(Modifier.width(220.dp).padding(vertical = 6.dp)) {
                            options.forEach { option ->
                                Text(
                                    option,
                                    fontSize = 15.sp,
                                    color = AdminColors.TextPrimary,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onSelect(option)
                                            expanded = false
                                        }
                                        .padding(horizontal = 18.dp, vertical = 12.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/** 紧凑弹出式选项（区号那种窄栏） */
@Composable
fun FormSelectCompact(
    value: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(AdminColors.Card)
                .clickable { expanded = true }
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(value, color = AdminColors.TextPrimary, fontSize = 15.sp, modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = AdminColors.TextMuted,
                modifier = Modifier.size(18.dp),
            )
        }
        if (expanded) {
            Popup(
                onDismissRequest = { expanded = false },
                properties = PopupProperties(focusable = true),
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = AdminColors.Card,
                    shadowElevation = 8.dp,
                ) {
                    Column(Modifier.width(140.dp).padding(vertical = 6.dp)) {
                        options.forEach { option ->
                            Text(
                                option,
                                fontSize = 15.sp,
                                color = AdminColors.TextPrimary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onSelect(option)
                                        expanded = false
                                    }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

/** 点击式选择栏（生日等） */
@Composable
fun FormPickerField(
    label: String,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        FormLabel(label)
        Box(
            Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(AdminColors.Card)
                .clickable { onClick() }
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = text.ifBlank { "选择日期" },
                color = if (text.isBlank()) AdminColors.TextMuted else AdminColors.TextPrimary,
                fontSize = 15.sp,
            )
        }
    }
}

/** 密码输入框（默认隐藏明文） */
@Composable
fun FormPasswordField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        FormLabel(label)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = AdminColors.TextMuted) },
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default,
            colors = formFieldColors(),
        )
    }
}

/** 带右侧文字按钮的二级页标题栏（返回 + 居中标题 + 右侧动作） */
@Composable
fun FormPageHeaderAction(
    title: String,
    actionLabel: String,
    onBack: () -> Unit,
    onAction: () -> Unit,
) {
    Box(Modifier.fillMaxWidth().height(44.dp)) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(AdminColors.SurfaceVariant)
                .clickable { onBack() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "返回",
                tint = AdminColors.TextPrimary,
                modifier = Modifier.size(20.dp),
            )
        }
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            color = AdminColors.TextPrimary,
            modifier = Modifier.align(Alignment.Center),
        )
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .height(36.dp)
                .clip(RoundedCornerShape(50))
                .background(AdminColors.SurfaceVariant)
                .clickable { onAction() }
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(actionLabel, fontSize = 14.sp, color = AdminColors.Primary, fontWeight = FontWeight.SemiBold)
        }
    }
}

/** 上传框：拍照 / 相册 / 文档（可分别开关），带上方标题 */
@Composable
fun FormUpload(
    label: String,
    hint: String,
    uri: String?,
    onPicked: (String) -> Unit,
    modifier: Modifier = Modifier,
    allowCamera: Boolean = true,
    allowGallery: Boolean = true,
    allowDocument: Boolean = true,
) {
    Column(modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        FormLabel(label)
        FormUploadTile(
            label = label,
            hint = hint,
            uri = uri,
            onPicked = onPicked,
            modifier = Modifier.fillMaxWidth(),
            allowCamera = allowCamera,
            allowGallery = allowGallery,
            allowDocument = allowDocument,
        )
    }
}

/** 上传框（不带标题）：拍照 / 相册 / 文档 */
@Composable
fun FormUploadTile(
    label: String,
    hint: String,
    uri: String?,
    onPicked: (String) -> Unit,
    modifier: Modifier = Modifier,
    allowCamera: Boolean = true,
    allowGallery: Boolean = true,
    allowDocument: Boolean = true,
) {
    var showSheet by remember { mutableStateOf(false) }
    val pickMedia = rememberMediaPicker()

    Box(
        modifier
            .height(124.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(AdminColors.Card)
            .clickable { showSheet = true },
        contentAlignment = Alignment.Center,
    ) {
        if (uri == null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Filled.CameraAlt,
                    contentDescription = null,
                    tint = AdminColors.TextMuted,
                    modifier = Modifier.size(26.dp),
                )
                Spacer(Modifier.height(6.dp))
                Text(hint, color = AdminColors.TextMuted, fontSize = 13.sp)
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = AdminColors.Primary)
                Spacer(Modifier.height(4.dp))
                Text("$hint 已上传", color = AdminColors.TextPrimary, fontSize = 12.sp)
            }
        }
    }

    if (showSheet) {
        Dialog(onDismissRequest = { showSheet = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = AdminColors.Card,
                shadowElevation = 12.dp,
            ) {
                Column(Modifier.fillMaxWidth().padding(20.dp)) {
                    Text(label, color = AdminColors.TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(12.dp))
                    if (allowCamera) {
                        FormSheetRow(Icons.Filled.PhotoCamera, "拍照") {
                            showSheet = false
                            pickMedia(MediaSource.CAMERA) { media -> media?.let { onPicked(it.uri) } }
                        }
                    }
                    if (allowGallery) {
                        FormSheetRow(Icons.Filled.PhotoLibrary, "从相册选择") {
                            showSheet = false
                            pickMedia(MediaSource.GALLERY) { media -> media?.let { onPicked(it.uri) } }
                        }
                    }
                    if (allowDocument) {
                        FormSheetRow(Icons.Filled.Description, "选择文档") {
                            showSheet = false
                            pickMedia(MediaSource.DOCUMENT) { media -> media?.let { onPicked(it.uri) } }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    TextButton(
                        onClick = { showSheet = false },
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        Text("取消", color = AdminColors.TextMuted)
                    }
                }
            }
        }
    }
}

@Composable
private fun FormSheetRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = AdminColors.Primary)
        Spacer(Modifier.width(12.dp))
        Text(label, color = AdminColors.TextPrimary, fontSize = 15.sp)
    }
}
