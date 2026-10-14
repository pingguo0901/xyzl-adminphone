package com.stellarelite.admin.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellarelite.admin.ui.theme.AdminColors
import kotlin.math.roundToInt

// ─── 五大页面导航 ───

enum class AdminTab(val label: String, val icon: ImageVector) {
    Home("首页", Icons.Outlined.Home),
    Chat("聊天", Icons.AutoMirrored.Outlined.Chat),
    Gps("GPS", Icons.Outlined.LocationOn),
    Trips("行程", Icons.Outlined.Map),
    Me("我", Icons.Outlined.Person)
}

@Composable
fun AdminBottomNavBar(
    tabs: List<AdminTab>,
    currentTab: AdminTab,
    onTabSelected: (AdminTab) -> Unit
) {
    val density = LocalDensity.current
    val itemPositions = remember { mutableStateMapOf<AdminTab, IntOffset>() }
    val itemWidths = remember { mutableStateMapOf<AdminTab, Int>() }

    val targetX = itemPositions[currentTab]?.x ?: 0
    val targetW = itemWidths[currentTab] ?: 0

    val animatedX by animateDpAsState(
        targetValue = with(density) { targetX.toDp() },
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "tabX",
    )
    val animatedW by animateDpAsState(
        targetValue = with(density) { targetW.toDp() },
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "tabW",
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(50))
                .background(AdminColors.SurfaceVariant, RoundedCornerShape(50))
                .border(1.dp, AdminColors.Border, RoundedCornerShape(50))
                .height(52.dp)
                .padding(4.dp),
        ) {
            Box(
                modifier = Modifier
                    .offset(x = animatedX)
                    .width(animatedW)
                    .fillMaxHeight()
                    .shadow(2.dp, RoundedCornerShape(50))
                    .background(AdminColors.Background, RoundedCornerShape(50)),
            )

            Row(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                tabs.forEach { tab ->
                    val isSelected = currentTab == tab
                    val contentColor = if (isSelected) AdminColors.TextPrimary else AdminColors.TextMuted
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .onGloballyPositioned { coords ->
                                itemPositions[tab] = IntOffset(coords.positionInParent().x.roundToInt(), 0)
                                itemWidths[tab] = coords.size.width
                            }
                            .clip(RoundedCornerShape(50))
                            .clickable { onTabSelected(tab) },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            tab.icon,
                            contentDescription = tab.label,
                            tint = contentColor,
                            modifier = Modifier.size(22.dp)
                        )
                        if (isSelected) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                tab.label,
                                color = contentColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─── 页面标题（顶部居中） ───

@Composable
fun PageTitle(title: String, onBellClick: (() -> Unit)? = null) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(title, color = AdminColors.TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Black)
        if (onBellClick != null) {
            // 右上角铃铛按钮
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(AdminColors.SurfaceVariant)
                    .clickable { onBellClick() }
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Notifications,
                    contentDescription = "通知",
                    tint = AdminColors.TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ─── 胶囊式筛选栏 ───

@Composable
fun FilterChipBar(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { opt ->
            val isSelected = selected == opt
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) AdminColors.Primary else AdminColors.SurfaceVariant)
                    .clickable { onSelect(opt) }
                    .padding(horizontal = 16.dp, vertical = 7.dp)
            ) {
                Text(
                    opt,
                    color = if (isSelected) Color.White else AdminColors.TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// ─── 通用卡片容器 ───

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AdminColors.Card)
            .padding(16.dp),
        content = content
    )
}

// ─── 通用组件 ───

@Composable
fun SectionTitle(title: String) {
    Text(
        title,
        color = AdminColors.TextSecondary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 4.dp)
    )
}

@Composable
fun InfoRow(label: String, value: String, valueColor: Long = 0xFFFFFFFF) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = AdminColors.TextMuted, fontSize = 12.sp)
        Text(value, color = Color(valueColor), fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.End, modifier = Modifier.widthIn(max = 180.dp))
    }
}

@Composable
fun StatusBadge(status: String) {
    val color = AdminColors.statusColor(status)
    val label = AdminColors.statusLabel(status)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(label, color = color, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun LoadingView(message: String = "加载中...") {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(message, color = AdminColors.TextMuted, fontSize = 14.sp)
    }
}

@Composable
fun EmptyView(message: String = "暂无数据") {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(message, color = AdminColors.TextMuted, fontSize = 14.sp)
    }
}

@Composable
fun ActionButton(
    label: String,
    onClick: () -> Unit,
    color: Color = AdminColors.Primary,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(color)
            .clickable { onClick() }
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}
