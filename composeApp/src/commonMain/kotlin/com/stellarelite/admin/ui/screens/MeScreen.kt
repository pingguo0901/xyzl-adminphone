package com.stellarelite.admin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellarelite.admin.ui.components.PageTitle
import com.stellarelite.admin.ui.theme.AdminColors

private val meSections: List<Pair<String, List<String>>> = listOf(
    "我的信息" to listOf("个人信息", "隐私信息", "车辆信息"),
    "账号管理" to listOf("司机端密码", "登录设备管理", "更多安全设置"),
    "邀请码" to listOf("客户邀请码", "公司邀请码"),
    "支付与收款" to listOf("我的钱包"),
    "通用" to listOf("我的值班", "通知", "界面与显示", "聊天", "聊天记录管理", "公司评价"),
    "平台政策与协议" to listOf(
        "用户服务协议",
        "隐私政策",
        "KYC实名认证合规条款",
        "退款与交易合规政策",
        "数据留存与审计合规政策",
        "免责声明",
        "合规最终说明",
        "终端用户软件许可协议",
    ),
    "帮助与关于" to listOf("帮助与反馈", "关于我们", "关于平台"),
)

/** 我的页：头像卡（右侧二维码按钮）+ 分组菜单 + 退出登录（复刻司机端我的页） */
@Composable
fun MeScreen(
    onBellClick: () -> Unit = {},
    onOpenAboutUs: () -> Unit = {},
    onOpenAbout: () -> Unit = {},
    onOpenPolicy: (Int) -> Unit = {},
    onOpenNotificationSettings: () -> Unit = {},
    onOpenDuty: () -> Unit = {},
    onOpenDisplay: () -> Unit = {},
    onOpenChatSettings: () -> Unit = {},
    onOpenChatRecords: () -> Unit = {},
    onOpenMyReviews: () -> Unit = {},
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    val onItemClick: (String) -> Unit = { label ->
        when (label) {
            "用户服务协议" -> onOpenPolicy(0)
            "隐私政策" -> onOpenPolicy(1)
            "KYC实名认证合规条款" -> onOpenPolicy(2)
            "退款与交易合规政策" -> onOpenPolicy(3)
            "数据留存与审计合规政策" -> onOpenPolicy(4)
            "免责声明" -> onOpenPolicy(5)
            "合规最终说明" -> onOpenPolicy(6)
            "终端用户软件许可协议" -> onOpenPolicy(7)
            "关于我们" -> onOpenAboutUs()
            "关于平台" -> onOpenAbout()
            "我的值班" -> onOpenDuty()
            "通知" -> onOpenNotificationSettings()
            "界面与显示" -> onOpenDisplay()
            "聊天" -> onOpenChatSettings()
            "聊天记录管理" -> onOpenChatRecords()
            "公司评价" -> onOpenMyReviews()
            else -> { }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = AdminColors.Card,
            title = {
                Text("退出登录", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = AdminColors.TextPrimary)
            },
            text = {
                Text("确定要退出当前账号吗？", fontSize = 14.sp, color = AdminColors.TextMuted)
            },
            confirmButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("退出", color = AdminColors.Danger, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("取消", color = AdminColors.TextMuted)
                }
            },
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        PageTitle("我", onBellClick = onBellClick)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            // 头像卡：右侧二维码按钮
            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AdminColors.Card),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier.size(64.dp).background(AdminColors.SurfaceVariant, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = AdminColors.TextMuted,
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text("管理员", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = AdminColors.TextPrimary)
                        Spacer(Modifier.height(4.dp))
                        Text("ID：AM-0001", fontSize = 13.sp, color = AdminColors.TextMuted)
                        Text("所属公司：星域集团", fontSize = 13.sp, color = AdminColors.TextMuted)
                    }
                    Spacer(Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(AdminColors.SurfaceVariant)
                            .clickable { },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.QrCode2,
                            contentDescription = "我的二维码",
                            tint = AdminColors.TextPrimary,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }
            }

            meSections.forEach { (title, items) ->
                Spacer(Modifier.height(18.dp))
                Text(
                    title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AdminColors.TextMuted,
                    modifier = Modifier.padding(start = 6.dp),
                )
                Spacer(Modifier.height(8.dp))
                Card(
                    Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = AdminColors.Card),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                ) {
                    Column {
                        items.forEachIndexed { index, label ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onItemClick(label) }
                                    .padding(horizontal = 20.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(label, fontSize = 15.sp, color = AdminColors.TextPrimary, modifier = Modifier.weight(1f))
                                Icon(
                                    imageVector = Icons.Filled.ChevronRight,
                                    contentDescription = null,
                                    tint = AdminColors.TextMuted,
                                    modifier = Modifier.size(18.dp),
                                )
                            }
                            if (index != items.lastIndex) {
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(start = 20.dp)
                                        .height(1.dp)
                                        .background(AdminColors.SurfaceVariant),
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Card(
                Modifier.fillMaxWidth().clickable { showLogoutDialog = true },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = AdminColors.Card),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            ) {
                Box(Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                    Text("退出登录", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = AdminColors.Danger)
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
