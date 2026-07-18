package com.stellarelite.admin.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellarelite.admin.ui.theme.AdminColors

@Composable
fun LoginScreen(
    onLogin: (String, String) -> Unit,
    loading: Boolean,
    error: String?
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AdminColors.Background)
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        // Logo area
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(AdminColors.PrimaryBg),
            contentAlignment = Alignment.Center
        ) {
            Text("🛡️", fontSize = 44.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "星域臻旅 · 后台端",
            color = AdminColors.TextPrimary,
            fontSize = 26.sp,
            fontWeight = FontWeight.Black
        )

        Text(
            "ADMIN CONTROL PANEL",
            color = AdminColors.TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 4.sp,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Username field
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                "职员账号",
                color = AdminColors.TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
            )
            BasicTextField(
                value = username,
                onValueChange = { username = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(AdminColors.Surface)
                    .padding(16.dp),
                textStyle = TextStyle(color = AdminColors.TextPrimary, fontSize = 15.sp),
                cursorBrush = SolidColor(AdminColors.Primary),
                singleLine = true,
                decorationBox = { inner ->
                    Box {
                        if (username.isEmpty()) Text("输入职员账号", color = AdminColors.TextDisabled, fontSize = 15.sp)
                        inner()
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Password field
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                "密码",
                color = AdminColors.TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
            )
            BasicTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(AdminColors.Surface)
                    .padding(16.dp),
                textStyle = TextStyle(color = AdminColors.TextPrimary, fontSize = 15.sp),
                cursorBrush = SolidColor(AdminColors.Primary),
                singleLine = true,
                decorationBox = { inner ->
                    Box {
                        if (password.isEmpty()) Text("输入密码", color = AdminColors.TextDisabled, fontSize = 15.sp)
                        inner()
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Error message
        if (error != null) {
            Text(
                error,
                color = AdminColors.Danger,
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Login button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    if (username.isNotEmpty() && password.isNotEmpty() && !loading)
                        AdminColors.Primary
                    else
                        AdminColors.SurfaceVariant
                )
                .clickable(enabled = username.isNotEmpty() && password.isNotEmpty() && !loading) {
                    onLogin(username, password)
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                if (loading) "认证中..." else "登 录",
                color = if (username.isNotEmpty() && password.isNotEmpty()) Color.White else AdminColors.TextMuted,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "仅限授权职员访问",
            color = AdminColors.TextDisabled,
            fontSize = 11.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(60.dp))
    }
}
