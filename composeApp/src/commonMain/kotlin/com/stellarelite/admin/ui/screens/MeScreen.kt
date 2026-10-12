package com.stellarelite.admin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Policy
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellarelite.admin.ui.theme.AdminColors
import com.stellarelite.admin.ui.components.GlassCard
import com.stellarelite.admin.ui.components.PageTitle

@Composable
fun MeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        PageTitle("我")

        // 顶部个人卡片
        GlassCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(56.dp).clip(RoundedCornerShape(28.dp)).background(AdminColors.SurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Person, contentDescription = null, tint = AdminColors.TextPrimary, modifier = Modifier.size(28.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("星域集团", color = AdminColors.TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("管理员", color = AdminColors.TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.height(3.dp))
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(AdminColors.PrimaryBg).padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("Admin · 老板", color = AdminColors.Primary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Box(
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(AdminColors.SurfaceVariant).clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.QrCode2, contentDescription = "二维码", tint = AdminColors.TextPrimary, modifier = Modifier.size(22.dp))
                }
            }
        }

        MeSection("我的信息") {
            MenuRow(Icons.Outlined.Person, "个人信息")
            MenuRow(Icons.Outlined.Lock, "隐私信息")
            MenuRow(Icons.Outlined.DirectionsCar, "车辆信息")
        }
        MeSection("账号管理") {
            MenuRow(Icons.Outlined.Lock, "司机端密码")
            MenuRow(Icons.Outlined.Devices, "登录设备管理")
            MenuRow(Icons.Outlined.Shield, "更多安全设置")
        }
        MeSection("邀请码") {
            MenuRow(Icons.Outlined.CardGiftcard, "客户邀请码")
            MenuRow(Icons.Outlined.Badge, "公司邀请码")
        }
        MeSection("支付与收款") {
            MenuRow(Icons.Outlined.Wallet, "我的钱包")
        }
        MeSection("通用") {
            MenuRow(Icons.Outlined.Notifications, "通知")
            MenuRow(Icons.Outlined.Palette, "界面与显示")
            MenuRow(Icons.AutoMirrored.Outlined.Chat, "聊天")
            MenuRow(Icons.Outlined.ReceiptLong, "聊天记录管理")
            MenuRow(Icons.Outlined.Star, "我的评价")
        }
        MeSection("平台政策与协议") {
            MenuRow(Icons.Outlined.Policy, "用户服务协议")
            MenuRow(Icons.Outlined.Policy, "隐私政策")
            MenuRow(Icons.Outlined.Shield, "KYC实名认证合规条款")
            MenuRow(Icons.Outlined.ReceiptLong, "退款与交易合规政策")
            MenuRow(Icons.Outlined.ReceiptLong, "数据留存与审计合规政策")
            MenuRow(Icons.Outlined.Info, "免责声明")
            MenuRow(Icons.Outlined.Info, "合规最终说明")
        }
        MeSection("帮助与关于") {
            MenuRow(Icons.AutoMirrored.Outlined.HelpOutline, "帮助与反馈")
            MenuRow(Icons.Outlined.Info, "关于我们")
            MenuRow(Icons.Outlined.Info, "关于平台")
        }

        // 退出登录
        Box(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(top = 16.dp, bottom = 24.dp)
                .clip(RoundedCornerShape(14.dp)).background(AdminColors.Card).clickable { }.padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = null, tint = AdminColors.Danger, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("退出登录", color = AdminColors.Danger, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun MeSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Text(
        title,
        color = AdminColors.TextSecondary,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 20.dp, top = 18.dp, bottom = 8.dp)
    )
    GlassCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) { content() }
}

@Composable
private fun MenuRow(icon: ImageVector, label: String) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { }.padding(vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = label, tint = AdminColors.TextSecondary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(label, color = AdminColors.TextPrimary, fontSize = 13.sp, modifier = Modifier.weight(1f))
        Text("›", color = AdminColors.TextMuted, fontSize = 18.sp)
    }
}
