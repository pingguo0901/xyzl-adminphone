package com.stellarelite.admin.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellarelite.admin.ui.components.AppleButton
import com.stellarelite.admin.ui.components.WeChatButton
import com.stellarelite.admin.ui.theme.AdminColors

// =====================  星域臻旅 后台端 登录页面（复刻司机端）  =====================
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LoginScreen(
    onBack: () -> Unit = {},
    onLoginSuccess: () -> Unit = {},
    onRegister: () -> Unit = {},
    onForgetPassword: () -> Unit = {},
    onWechatLogin: () -> Unit = {},
    onAppleLogin: () -> Unit = {},
    onOpenPolicy: (Int) -> Unit = {},
) {
    var accountText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var agreedPolicy by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AdminColors.Background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(horizontal = 28.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // ==========  顶部品牌  ==========
            Spacer(Modifier.height(24.dp))
            Text(
                text = "星域臻旅",
                fontSize = 44.sp,
                color = AdminColors.TextPrimary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 6.sp,
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "— 尊荣邀制 · 至尊之旅 —",
                fontSize = 15.sp,
                color = AdminColors.TextMuted,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(bottom = 36.dp),
            )

            // ==========  登录卡片  ==========
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0x1A3B82F6)),
                border = BorderStroke(1.dp, AdminColors.SurfaceVariant),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(26.dp),
                ) {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "登录",
                            fontSize = 24.sp,
                            color = AdminColors.TextPrimary,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 手机号 / 邮箱
                    OutlinedTextField(
                        value = accountText,
                        onValueChange = { accountText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("请输入手机号 / 邮箱", color = AdminColors.TextMuted) },
                        leadingIcon = {
                            Icon(Icons.Filled.Person, null, tint = AdminColors.TextMuted)
                        },
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AdminColors.Primary,
                            unfocusedBorderColor = AdminColors.SurfaceVariant,
                            focusedTextColor = AdminColors.TextPrimary,
                            unfocusedTextColor = AdminColors.TextPrimary,
                        ),
                        singleLine = true,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 密码
                    OutlinedTextField(
                        value = passwordText,
                        onValueChange = { passwordText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("请输入密码", color = AdminColors.TextMuted) },
                        leadingIcon = {
                            Icon(Icons.Filled.Lock, null, tint = AdminColors.TextMuted)
                        },
                        trailingIcon = {
                            val icon = if (passwordVisible) Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(icon, "切换密码可见", tint = AdminColors.TextMuted)
                            }
                        },
                        shape = RoundedCornerShape(18.dp),
                        visualTransformation = if (passwordVisible) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AdminColors.Primary,
                            unfocusedBorderColor = AdminColors.SurfaceVariant,
                            focusedTextColor = AdminColors.TextPrimary,
                            unfocusedTextColor = AdminColors.TextPrimary,
                        ),
                        singleLine = true,
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        Text(
                            text = "忘记密码？",
                            color = AdminColors.Primary,
                            fontSize = 13.sp,
                            modifier = Modifier.clickable { onForgetPassword() },
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = { onLoginSuccess() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(999.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AdminColors.Primary),
                        enabled = agreedPolicy,
                    ) {
                        Text("登录", fontSize = 20.sp, color = Color(0xFFFFFFFF), fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(26.dp))

                    // 分割线 + 其他登录方式
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(AdminColors.Card),
                        )
                        Text(
                            "其他登录方式",
                            color = AdminColors.TextMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp),
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(AdminColors.Card),
                        )
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        WeChatButton(onClick = { onWechatLogin() }, size = 64.dp, logoSize = 34.dp)
                        Spacer(modifier = Modifier.width(36.dp))
                        AppleButton(onClick = { onAppleLogin() }, size = 64.dp, logoSize = 34.dp)
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("没有帐号？", color = AdminColors.TextMuted, fontSize = 13.sp)
                        Text(
                            "注册",
                            color = AdminColors.Primary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { onRegister() },
                        )
                    }

                    Spacer(Modifier.height(18.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = agreedPolicy,
                            onCheckedChange = { agreedPolicy = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = AdminColors.Primary,
                                uncheckedColor = AdminColors.TextMuted,
                                checkmarkColor = Color(0xFFFFFFFF),
                            ),
                        )
                        FlowRow(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text("我已阅读并同意", color = AdminColors.TextPrimary, fontSize = 12.sp)
                            AdminPolicyLink("《用户服务协议》") { onOpenPolicy(0) }
                            Text("与", color = AdminColors.TextPrimary, fontSize = 12.sp)
                            AdminPolicyLink("《隐私政策》") { onOpenPolicy(1) }
                        }
                    }
                }
            }

            Spacer(Modifier.height(40.dp))
        }

        // 左上角返回
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(top = 4.dp, start = 8.dp),
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "返回",
                tint = AdminColors.TextPrimary,
            )
        }
    }
}

@Composable
internal fun AdminPolicyLink(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        color = AdminColors.Primary,
        fontSize = 12.sp,
        modifier = Modifier.clickable { onClick() },
    )
}
