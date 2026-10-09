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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellarelite.admin.ui.theme.AdminColors

@Composable
fun LoginScreen(
    onLogin: (email: String, password: String) -> Unit,
    onGoRegister: () -> Unit,
    onForgotPassword: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))
        Text("星域臻旅 · 后台端", color = AdminColors.TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Black)
        Spacer(modifier = Modifier.height(6.dp))
        Text("欢迎回来，请登录", color = AdminColors.TextMuted, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(40.dp))

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
        Spacer(modifier = Modifier.height(28.dp))

        // 登录按钮
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(AdminColors.Primary)
                .clickable { onLogin(email, password) }
                .padding(vertical = 15.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("登录", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        // 登录按钮正下方：还没账号？注册按钮
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("还没账号？", color = AdminColors.TextMuted, fontSize = 13.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                "注册",
                color = AdminColors.Primary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onGoRegister() }
            )
        }

        // 登录按钮右下方：忘记密码？
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            "忘记密码？",
            color = AdminColors.TextMuted,
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.End)
                .clickable { onForgotPassword() }
        )
        Spacer(modifier = Modifier.height(40.dp))
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
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
        )
    }
}
