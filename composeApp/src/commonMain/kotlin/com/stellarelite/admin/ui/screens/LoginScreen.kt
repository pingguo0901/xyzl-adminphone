package com.stellarelite.admin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 登录页专属配色（深蓝渐变 + 金色按钮）
private val LoginBgTop = Color(0xFF050F33)
private val LoginBgBottom = Color(0xFF0A1F4A)
private val GoldPrimary = Color(0xFFE8CB7D)
private val GoldDeep = Color(0xFFC9A34E)
private val LoginTextMain = Color(0xFFF5EED8)
private val LoginTextSub = Color(0xFFB8BFD6)

@Composable
fun LoginScreen(
    onLogin: (email: String, password: String) -> Unit,
    onGoRegister: () -> Unit,
    onForgotPassword: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(LoginBgTop, LoginBgBottom)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(90.dp))

            // 标题
            Text("星域臻旅", color = LoginTextMain, fontSize = 30.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Text("董事长 · 后台管理端", color = GoldPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(52.dp))

            // 邮箱地址输入框
            LoginField(
                value = email,
                onValueChange = { email = it },
                placeholder = "邮箱地址"
            )
            Spacer(modifier = Modifier.height(14.dp))

            // 密码输入框
            LoginField(
                value = password,
                onValueChange = { password = it },
                placeholder = "密码",
                isPassword = true
            )
            Spacer(modifier = Modifier.height(34.dp))

            // 登录按钮（金色）
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(GoldDeep, GoldPrimary, GoldDeep)
                        )
                    )
                    .clickable { onLogin(email, password) }
                    .padding(vertical = 15.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("登 录", color = Color(0xFF3A2E0F), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            // 登录按钮正下方：还没账号？注册
            Spacer(modifier = Modifier.height(18.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("还没账号？", color = LoginTextSub, fontSize = 13.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "立即注册",
                    color = GoldPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onGoRegister() }
                )
            }

            // 登录按钮右下方：忘记密码？
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "忘记密码？",
                color = LoginTextSub,
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { onForgotPassword() }
            )
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
private fun LoginField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.10f))
            .padding(horizontal = 16.dp, vertical = 15.dp)
    ) {
        if (value.isEmpty()) {
            Text(placeholder, color = LoginTextSub.copy(alpha = 0.7f), fontSize = 15.sp)
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(color = LoginTextMain, fontSize = 15.sp),
            cursorBrush = SolidColor(GoldPrimary),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
        )
    }
}
