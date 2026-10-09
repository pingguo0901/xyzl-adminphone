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
import androidx.compose.material3.Icon
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
fun RegisterScreen(
    onRegister: (email: String, password: String, confirmPassword: String) -> Unit,
    onBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
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
        Spacer(modifier = Modifier.height(40.dp))

        RegisterField(
            value = email,
            onValueChange = { email = it },
            placeholder = "邮箱地址"
        )
        Spacer(modifier = Modifier.height(14.dp))

        RegisterField(
            value = password,
            onValueChange = { password = it },
            placeholder = "密码",
            isPassword = true
        )
        Spacer(modifier = Modifier.height(14.dp))

        RegisterField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            placeholder = "确认密码",
            isPassword = true
        )
        Spacer(modifier = Modifier.height(28.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(AdminColors.Primary)
                .clickable { onRegister(email, password, confirmPassword) }
                .padding(vertical = 15.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("注册", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun RegisterField(
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
