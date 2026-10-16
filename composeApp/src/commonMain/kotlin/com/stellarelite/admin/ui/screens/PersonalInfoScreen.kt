package com.stellarelite.admin.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.stellarelite.admin.platform.MediaSource
import com.stellarelite.admin.platform.loadImageBitmap
import com.stellarelite.admin.platform.rememberMediaPicker
import com.stellarelite.admin.ui.components.FormLabel
import com.stellarelite.admin.ui.components.FormPrimaryButton
import com.stellarelite.admin.ui.components.FormSelectCompact
import com.stellarelite.admin.ui.components.FormTextField
import com.stellarelite.admin.ui.components.SubPageHeader
import com.stellarelite.admin.ui.components.formFieldColors
import com.stellarelite.admin.ui.theme.AdminColors

/** 个人信息页：头像 + 昵称 + ID + 手机号（区号）+ 邮箱 + 微信号（复刻司机端，本页仅本地状态） */
@Composable
fun PersonalInfoScreen(onBack: () -> Unit = {}) {
    val pickMedia = rememberMediaPicker()

    val initialNickname = "管理员"
    val initialId = "AM-0001"

    var nickname by remember { mutableStateOf(initialNickname) }
    var displayId by remember { mutableStateOf(initialId) }
    var dialCode by remember { mutableStateOf(DialCodes.first()) }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var wechat by remember { mutableStateOf("") }

    var avatarUri by remember { mutableStateOf<String?>(null) }
    var avatar by remember { mutableStateOf<ImageBitmap?>(null) }

    var showAvatarSheet by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogBody by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(avatarUri) {
        avatar = avatarUri?.let { loadImageBitmap(it) }
    }

    val changed = nickname != initialNickname ||
        displayId != initialId ||
        dialCode != DialCodes.first() ||
        phone.isNotEmpty() ||
        email.isNotEmpty() ||
        wechat.isNotEmpty() ||
        avatarUri != null

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        SubPageHeader("个人信息", onBack = onBack)
        Spacer(Modifier.height(16.dp))
        Column(Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState())) {

            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .background(AdminColors.SurfaceVariant)
                            .clickable { showAvatarSheet = true },
                        contentAlignment = Alignment.Center,
                    ) {
                        val shown = avatar
                        if (shown != null) {
                            Image(
                                bitmap = shown,
                                contentDescription = "头像",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                            )
                        } else {
                            Icon(
                                Icons.Filled.Person,
                                contentDescription = "头像",
                                tint = AdminColors.TextMuted,
                                modifier = Modifier.size(46.dp),
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("点击设置头像", fontSize = 12.sp, color = AdminColors.TextMuted)
                }
            }

            Spacer(Modifier.height(20.dp))

            FormTextField(
                label = "昵称",
                value = nickname,
                placeholder = "中文 / 英文 / 数字 / 标点",
                onValueChange = { nickname = sanitizeNickname(it) },
            )

            FormTextField(
                label = "ID",
                value = displayId,
                placeholder = "自动生成，可自定义，唯一",
                onValueChange = { displayId = it.filter { c -> c.isLetterOrDigit() || c == '-' }.take(20) },
            )

            FormLabel("手机号")
            Row(
                Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FormSelectCompact(
                    value = dialCode,
                    options = DialCodes,
                    onSelect = { dialCode = it },
                    modifier = Modifier.width(110.dp),
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = sanitizeDigits(it) },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("仅数字", color = AdminColors.TextMuted) },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = formFieldColors(),
                )
            }

            FormTextField(
                label = "邮箱地址",
                value = email,
                placeholder = "英文 / 数字 / 标点",
                onValueChange = { email = sanitizeEmail(it) },
            )
            FormTextField(
                label = "微信号",
                value = wechat,
                placeholder = "选填 · 英文 / 数字 / 标点",
                onValueChange = { wechat = sanitizeWechat(it) },
            )

            Spacer(Modifier.height(6.dp))
        }

        FormPrimaryButton(
            text = "保存更改",
            enabled = changed,
            onClick = {
                dialogTitle = "保存成功"
                dialogBody = "个人信息已更新"
                showDialog = true
            },
        )
        Spacer(Modifier.height(16.dp))
    }

    if (showAvatarSheet) {
        Dialog(onDismissRequest = { showAvatarSheet = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = AdminColors.Card,
                shadowElevation = 12.dp,
            ) {
                Column(Modifier.fillMaxWidth().padding(20.dp)) {
                    Text("设置头像", color = AdminColors.TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(12.dp))
                    Row(
                        Modifier.fillMaxWidth().clickable {
                            showAvatarSheet = false
                            pickMedia(MediaSource.CAMERA) { media -> media?.let { avatarUri = it.uri } }
                        }.padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Filled.PhotoCamera, contentDescription = null, tint = AdminColors.Primary)
                        Spacer(Modifier.width(12.dp))
                        Text("拍照", color = AdminColors.TextPrimary, fontSize = 15.sp)
                    }
                    Row(
                        Modifier.fillMaxWidth().clickable {
                            showAvatarSheet = false
                            pickMedia(MediaSource.GALLERY) { media -> media?.let { avatarUri = it.uri } }
                        }.padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Filled.PhotoLibrary, contentDescription = null, tint = AdminColors.Primary)
                        Spacer(Modifier.width(12.dp))
                        Text("从相册选择", color = AdminColors.TextPrimary, fontSize = 15.sp)
                    }
                    Spacer(Modifier.height(4.dp))
                    TextButton(
                        onClick = { showAvatarSheet = false },
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        Text("取消", color = AdminColors.TextMuted)
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = AdminColors.Card,
            title = { Text(dialogTitle, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = AdminColors.TextPrimary) },
            text = { Text(dialogBody, fontSize = 14.sp, color = AdminColors.TextMuted) },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    if (dialogTitle == "保存成功") onBack()
                }) {
                    Text("确定", color = AdminColors.Primary, fontWeight = FontWeight.SemiBold)
                }
            },
        )
    }
}
