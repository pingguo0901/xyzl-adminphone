package com.stellarelite.admin.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 界面与显示偏好（深色 / 浅色 / 跟随系统）—— 跨页面共享的本地状态
 */
object DisplayPrefs {
    var followSystem by mutableStateOf(true)
    var darkMode by mutableStateOf(true)
    var fontSizeLevel by mutableStateOf(4)

    val modeLabel: String
        get() = if (followSystem) "跟随系统" else if (darkMode) "深色" else "浅色"
}

/** 主题色板（随 深色 / 浅色 切换） */
data class AdminThemeColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val card: Color,
    val cardAlt: Color,
    val border: Color,
    val navBar: Color,
    val navBarBorder: Color,
    val ink: Color,
    val secondary: Color,
    val muted: Color,
    val disabled: Color,
)

private val LightThemeColors = AdminThemeColors(
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFF6F6F8),
    surfaceVariant = Color(0xFFE7E7ED),
    card = Color(0xFFF2F2F6),
    cardAlt = Color(0xFFE9E9EF),
    border = Color(0x24000000),
    navBar = Color(0xFFFFFFFF),
    navBarBorder = Color(0x14000000),
    ink = Color(0xFF111111),
    secondary = Color(0xFF5F6470),
    muted = Color(0xFF8A8F98),
    disabled = Color(0xFFA6A6AE),
)

private val DarkThemeColors = AdminThemeColors(
    background = Color(0xFF0A0A0A),
    surface = Color(0xFF18181B),
    surfaceVariant = Color(0xFF27272A),
    card = Color(0xFF1C1C1E),
    cardAlt = Color(0xFF222226),
    border = Color(0x15FFFFFF),
    navBar = Color(0xFF0A0A0A),
    navBarBorder = Color(0x10FFFFFF),
    ink = Color(0xFFFFFFFF),
    secondary = Color(0xFFA1A1AA),
    muted = Color(0xFF71717A),
    disabled = Color(0xFF52525B),
)

/** 当前生效的色板 */
val LocalAdminThemeColors = staticCompositionLocalOf { DarkThemeColors }

/**
 * 星域臻旅 后台端 主题色。
 * 品牌色为常量；背景/卡片/文字等随 深色 / 浅色 模式变化（读 LocalAdminThemeColors）。
 */
object AdminColors {
    // ── 品牌 / 功能色（深浅色一致，常量） ──
    val Primary = Color(0xFF3B82F6)       // blue-500
    val PrimaryDim = Color(0xFF2563EB)    // blue-600
    val PrimaryBg = Color(0x1A3B82F6)

    val Success = Color(0xFF10B981)
    val Danger = Color(0xFFF43F5E)
    val Warning = Color(0xFFFBBF24)
    val Info = Color(0xFF06B6D4)

    val RoleDSZ = Color(0xFF6366F1)
    val RoleMS = Color(0xFF14B8A6)
    val RoleKJ = Color(0xFF10B981)
    val RoleZG = Color(0xFFF59E0B)
    val RoleCF = Color(0xFF3B82F6)
    val RoleGJ = Color(0xFF8B5CF6)

    val StatusPending = Color(0xFFFBBF24)
    val StatusConfirmed = Color(0xFF3B82F6)
    val StatusInProgress = Color(0xFF10B981)
    val StatusCompleted = Color(0xFF6B7280)

    // ── 随主题变化的色 ──
    val Background: Color @Composable get() = LocalAdminThemeColors.current.background
    val Surface: Color @Composable get() = LocalAdminThemeColors.current.surface
    val SurfaceVariant: Color @Composable get() = LocalAdminThemeColors.current.surfaceVariant
    val Card: Color @Composable get() = LocalAdminThemeColors.current.card
    val CardAlt: Color @Composable get() = LocalAdminThemeColors.current.cardAlt
    val Border: Color @Composable get() = LocalAdminThemeColors.current.border
    val NavBar: Color @Composable get() = LocalAdminThemeColors.current.navBar
    val NavBarBorder: Color @Composable get() = LocalAdminThemeColors.current.navBarBorder
    val TextPrimary: Color @Composable get() = LocalAdminThemeColors.current.ink
    val TextSecondary: Color @Composable get() = LocalAdminThemeColors.current.secondary
    val TextMuted: Color @Composable get() = LocalAdminThemeColors.current.muted
    val TextDisabled: Color @Composable get() = LocalAdminThemeColors.current.disabled

    fun roleColor(role: String): Color = when (role) {
        "DSZ" -> RoleDSZ
        "MS" -> RoleMS
        "KJ" -> RoleKJ
        "ZG" -> RoleZG
        "CF" -> RoleCF
        "GJ" -> RoleGJ
        else -> Primary
    }

    fun statusColor(status: String): Color = when (status) {
        "pending" -> StatusPending
        "confirmed" -> StatusConfirmed
        "in_progress" -> StatusInProgress
        "completed" -> StatusCompleted
        else -> Color(0xFF71717A)
    }

    fun statusLabel(status: String): String = when (status) {
        "pending" -> "待处理"
        "confirmed" -> "已确认"
        "in_progress" -> "进行中"
        "completed" -> "已完成"
        "cancelled" -> "已取消"
        else -> status
    }
}

private val LightScheme = lightColorScheme(
    primary = AdminColors.Primary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E3A8A),
    secondary = AdminColors.Primary,
    onSecondary = Color.White,
    secondaryContainer = LightThemeColors.surfaceVariant,
    onSecondaryContainer = Color(0xFF3F3F46),
    background = LightThemeColors.background,
    onBackground = LightThemeColors.ink,
    surface = LightThemeColors.background,
    onSurface = LightThemeColors.ink,
    surfaceVariant = LightThemeColors.surfaceVariant,
    onSurfaceVariant = LightThemeColors.secondary,
    surfaceTint = AdminColors.Primary,
    inverseSurface = Color(0xFF27272A),
    inverseOnSurface = Color(0xFFF4F4F5),
    error = AdminColors.Danger,
    onError = Color.White,
    outline = Color(0x33000000),
    outlineVariant = LightThemeColors.border,
    scrim = Color(0xCC000000),
)

private val DarkScheme = darkColorScheme(
    primary = AdminColors.Primary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = AdminColors.Primary,
    onSecondary = Color.White,
    secondaryContainer = DarkThemeColors.surfaceVariant,
    onSecondaryContainer = Color(0xFFE4E4E7),
    background = DarkThemeColors.background,
    onBackground = DarkThemeColors.ink,
    surface = DarkThemeColors.surface,
    onSurface = DarkThemeColors.ink,
    surfaceVariant = DarkThemeColors.surfaceVariant,
    onSurfaceVariant = DarkThemeColors.secondary,
    surfaceTint = AdminColors.Primary,
    inverseSurface = Color(0xFFE4E4E7),
    inverseOnSurface = Color(0xFF18181B),
    error = AdminColors.Danger,
    onError = Color.White,
    outline = Color(0x33FFFFFF),
    outlineVariant = DarkThemeColors.border,
    scrim = Color(0xCC000000),
)

private val AdminShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(26.dp),
)

/** 主题：跟随系统 → 用系统深浅色；否则用手动选择 */
@Composable
fun AdminAppTheme(content: @Composable () -> Unit) {
    val dark = if (DisplayPrefs.followSystem) isSystemInDarkTheme() else DisplayPrefs.darkMode
    CompositionLocalProvider(LocalAdminThemeColors provides if (dark) DarkThemeColors else LightThemeColors) {
        MaterialTheme(
            colorScheme = if (dark) DarkScheme else LightScheme,
            shapes = AdminShapes,
            content = content,
        )
    }
}
