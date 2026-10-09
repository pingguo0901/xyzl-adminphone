package com.stellarelite.admin.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// =====================  星域臻旅 登录页面 KMP 完整代码  =====================
@Composable
fun XingYuLoginScreen(
    onLogin: (phoneOrEmail: String, password: String) -> Unit,
    onRegister: () -> Unit,
    onForgetPassword: () -> Unit,
    onWechatLogin: () -> Unit,
    onAppleLogin: () -> Unit,
    onUserAgreementClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var accountText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var agreedPolicy by remember { mutableStateOf(false) }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF081226),
            Color(0xFF0F2040),
            Color(0xFF102244)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "星域臻旅",
                fontSize = 44.sp,
                color = Color(0xFFD4AF37),
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                text = "— 尊荣邀制 · 至尊之旅 —",
                fontSize = 18.sp,
                color = Color(0xFFE2C260),
                modifier = Modifier.padding(bottom = 36.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0x22345688)
                ),
                border = BorderStroke(
                    1.dp,
                    Color(0xFF4A72BB)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(26.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TabItem(
                            title = "登录",
                            selected = selectedTabIndex == 0,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedTabIndex = 0 }
                        )
                        TabItem(
                            title = "注册",
                            selected = selectedTabIndex == 1,
                            modifier = Modifier.weight(1f),
                            onClick = { selectedTabIndex = 1 }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = accountText,
                        onValueChange = { accountText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("请输入手机号 / 邮箱", color = Color(0xFFAAAAAA)) },
                        leadingIcon = {
                            Icon(Icons.Filled.Person, null, tint = Color(0xFFC8C8C8))
                        },
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD6B442),
                            unfocusedBorderColor = Color(0xFF5478AA),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = passwordText,
                        onValueChange = { passwordText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("请输入密码", color = Color(0xFFAAAAAA)) },
                        leadingIcon = {
                            Icon(Icons.Filled.Lock, null, tint = Color(0xFFC8C8C8))
                        },
                        trailingIcon = {
                            val icon = if (passwordVisible) Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(icon, "切换密码可见", tint = Color(0xFFCCCCCC))
                            }
                        },
                        shape = RoundedCornerShape(18.dp),
                        visualTransformation = if (passwordVisible) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD6B442),
                            unfocusedBorderColor = Color(0xFF5478AA),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "忘记密码？",
                            color = Color(0xFFE6C765),
                            modifier = Modifier.clickable { onForgetPassword() }
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = {
                            if (selectedTabIndex == 0) {
                                onLogin(accountText, passwordText)
                            } else {
                                onRegister()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(999.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD8B645)
                        ),
                        enabled = agreedPolicy
                    ) {
                        Text(
                            if (selectedTabIndex == 0) "登录" else "注册",
                            fontSize = 20.sp,
                            color = Color(0xFF1A1A1A)
                        )
                    }

                    Spacer(modifier = Modifier.height(26.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(Color(0xFF496899))
                        )
                        Text(
                            "其他登录方式",
                            color = Color(0xFFB0C4E2),
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(Color(0xFF496899))
                        )
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF07C160))
                                .clickable { onWechatLogin() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("微信", color = Color.White, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.width(36.dp))
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF111111))
                                .clickable { onAppleLogin() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Apple", color = Color.White, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(26.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = agreedPolicy,
                            onCheckedChange = { agreedPolicy = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFFD6B442)
                            )
                        )
                        Text(
                            "我已阅读并同意 ",
                            color = Color(0xFFDDE6F7)
                        )
                        Text(
                            "《用户协议》",
                            color = Color(0xFFE6C765),
                            modifier = Modifier.clickable { onUserAgreementClick() }
                        )
                        Text(
                            " 与 ",
                            color = Color(0xFFDDE6F7)
                        )
                        Text(
                            "《隐私政策》",
                            color = Color(0xFFE6C765),
                            modifier = Modifier.clickable { onPrivacyPolicyClick() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TabItem(
    title: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 24.sp,
            color = if (selected) Color.White else Color(0xFF889CC0)
        )
        Spacer(modifier = Modifier.height(6.dp))
        if (selected) {
            Box(
                modifier = Modifier
                    .width(44.dp)
                    .height(3.dp)
                    .background(Color(0xFFD8B645))
            )
        }
    }
}
