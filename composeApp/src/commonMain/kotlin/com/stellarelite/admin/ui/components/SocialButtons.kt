package com.stellarelite.admin.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.stellarelite.admin.ui.theme.AdminColors

/** 圆形社交登录按钮容器（复刻司机端 SocialButtons） */
@Composable
fun SocialCircleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    backgroundColor: Color = Color.White,
    borderColor: Color = Color(0xFFE0E0E0),
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(1.dp, borderColor, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) { content() }
}

@Composable
fun WeChatButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    logoSize: Dp = 26.dp,
) {
    SocialCircleButton(
        onClick = onClick,
        modifier = modifier,
        size = size,
        backgroundColor = Color(0xFF07C160),
        borderColor = Color.Transparent,
    ) {
        WeChatLogo(logoSize)
    }
}

@Composable
private fun WeChatLogo(logoSize: Dp) {
    Canvas(modifier = Modifier.size(logoSize)) {
        val w = size.width
        val h = size.height
        drawOval(
            color = Color.White,
            topLeft = Offset(w * 0.08f, h * 0.15f),
            size = Size(w * 0.55f, h * 0.5f),
        )
        drawOval(
            color = Color.White,
            topLeft = Offset(w * 0.38f, h * 0.42f),
            size = Size(w * 0.55f, h * 0.45f),
        )
        drawCircle(Color(0xFF07C160), radius = w * 0.04f, center = Offset(w * 0.25f, h * 0.4f))
        drawCircle(Color(0xFF07C160), radius = w * 0.04f, center = Offset(w * 0.42f, h * 0.4f))
        drawCircle(Color(0xFF07C160), radius = w * 0.035f, center = Offset(w * 0.55f, h * 0.62f))
        drawCircle(Color(0xFF07C160), radius = w * 0.035f, center = Offset(w * 0.72f, h * 0.62f))
    }
}

@Composable
fun AppleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    logoSize: Dp = 26.dp,
) {
    SocialCircleButton(
        onClick = onClick,
        modifier = modifier,
        size = size,
        backgroundColor = AdminColors.SurfaceVariant,
        borderColor = AdminColors.Border,
    ) {
        AppleLogo(logoSize)
    }
}

@Composable
private fun AppleLogo(logoSize: Dp) {
    val ink = AdminColors.TextPrimary
    val bite = AdminColors.SurfaceVariant
    Canvas(modifier = Modifier.size(logoSize)) {
        val w = size.width
        val h = size.height
        val apple = Path().apply {
            moveTo(w * 0.5f, h * 0.32f)
            cubicTo(w * 0.25f, h * 0.25f, w * 0.15f, h * 0.45f, w * 0.18f, h * 0.65f)
            cubicTo(w * 0.22f, h * 0.88f, w * 0.4f, h * 0.92f, w * 0.5f, h * 0.85f)
            cubicTo(w * 0.6f, h * 0.92f, w * 0.78f, h * 0.88f, w * 0.82f, h * 0.65f)
            cubicTo(w * 0.85f, h * 0.45f, w * 0.75f, h * 0.25f, w * 0.5f, h * 0.32f)
            close()
        }
        drawPath(apple, ink)
        val leaf = Path().apply {
            moveTo(w * 0.5f, h * 0.3f)
            cubicTo(w * 0.45f, h * 0.15f, w * 0.55f, h * 0.08f, w * 0.65f, h * 0.1f)
            cubicTo(w * 0.62f, h * 0.22f, w * 0.55f, h * 0.3f, w * 0.5f, h * 0.3f)
            close()
        }
        drawPath(leaf, ink)
        drawCircle(bite, radius = w * 0.12f, center = Offset(w * 0.78f, h * 0.5f))
    }
}
