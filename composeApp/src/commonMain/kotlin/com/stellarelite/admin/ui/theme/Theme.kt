package com.stellarelite.admin.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * 星域臻旅 后台端 - 暗色主题
 */
object AdminColors {
    val Background = Color(0xFF0A0A0A)
    val Surface = Color(0xFF18181B)
    val SurfaceVariant = Color(0xFF27272A)
    val Card = Color(0xFF1C1C1E)
    val CardAlt = Color(0xFF222226)

    val Primary = Color(0xFF3B82F6)       // blue-500
    val PrimaryDim = Color(0xFF2563EB)     // blue-600
    val PrimaryBg = Color(0x1A3B82F6)

    val Success = Color(0xFF10B981)        // emerald-500
    val Danger = Color(0xFFF43F5E)         // rose-500
    val Warning = Color(0xFFFBBF24)        // amber-500
    val Info = Color(0xFF06B6D4)           // cyan-500

    val TextPrimary = Color(0xFFFFFFFF)
    val TextSecondary = Color(0xFFA1A1AA)
    val TextMuted = Color(0xFF71717A)
    val TextDisabled = Color(0xFF52525B)

    val NavBar = Color(0xFF0A0A0A)
    val NavBarBorder = Color(0x10FFFFFF)
    val Border = Color(0x15FFFFFF)

    // Role colors
    val RoleDSZ = Color(0xFF6366F1)        // indigo-500
    val RoleMS = Color(0xFF14B8A6)         // teal-500
    val RoleKJ = Color(0xFF10B981)         // emerald-500
    val RoleZG = Color(0xFFF59E0B)         // amber-500
    val RoleCF = Color(0xFF3B82F6)         // blue-500
    val RoleGJ = Color(0xFF8B5CF6)         // violet-500

    // Status colors
    val StatusPending = Color(0xFFFBBF24)
    val StatusConfirmed = Color(0xFF3B82F6)
    val StatusInProgress = Color(0xFF10B981)
    val StatusCompleted = Color(0xFF6B7280)

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
        else -> TextMuted
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
