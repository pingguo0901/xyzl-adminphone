package com.stellarelite.admin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellarelite.admin.ui.components.FormSectionTitle
import com.stellarelite.admin.ui.components.SubPageHeader
import com.stellarelite.admin.ui.theme.AdminColors

private data class CompanyReview(
    val id: String,
    val customer: String,
    val stars: Int,
    val date: String,
    val comment: String,
    val trip: String,
)

/** 旗下司机：评分 + 好评率 + 评价条数 + 该司机的评价记录 */
private data class DriverEntry(
    val name: String,
    val rating: String,
    val goodRate: String,
    val count: Int,
    val reviews: List<CompanyReview>,
)

private val companyReviews = listOf(
    CompanyReview("c1", "陈太太", 5, "2026-10-11", "调度很及时，司机准时到达，全程很稳，满意！", "吉隆坡 → 槟城 · 送机"),
    CompanyReview("c2", "Mr. Tan", 5, "2026-10-09", "客服响应很快，改单也很顺畅，下次还会用。", "布城 → 云顶高原"),
    CompanyReview("c3", "李小姐", 4, "2026-10-05", "整体安排到位，等车时间稍微久了一点。", "机场接机"),
    CompanyReview("c4", "Ahmad", 5, "2026-09-30", "Nice service, punctual and friendly.", "市区包车 4 小时"),
    CompanyReview("c5", "王先生", 4, "2026-09-25", "整体不错，会再介绍给朋友。", "马六甲一日游"),
)

private val drivers = listOf(
    DriverEntry(
        name = "张师傅", rating = "4.9", goodRate = "97%", count = 64,
        reviews = listOf(
            CompanyReview("d1r1", "陈太太", 5, "2026-10-11", "司机很准时，车也干净，全程很稳，满意！", "吉隆坡 → 槟城 · 送机"),
            CompanyReview("d1r2", "Mr. Tan", 5, "2026-10-09", "帮忙搬行李，态度好，下次还会预约。", "布城 → 云顶高原"),
            CompanyReview("d1r3", "李小姐", 4, "2026-10-05", "路线熟悉，沟通也顺畅。", "机场接机"),
        ),
    ),
    DriverEntry(
        name = "王师傅", rating = "4.8", goodRate = "95%", count = 52,
        reviews = listOf(
            CompanyReview("d2r1", "Ahmad", 5, "2026-10-07", "Nice driver, punctual and friendly.", "市区包车 4 小时"),
            CompanyReview("d2r2", "黄先生", 4, "2026-09-28", "开车稳，就是等待时间有点长。", "新山 → 新加坡 · 送机"),
        ),
    ),
    DriverEntry(
        name = "Ahmad", rating = "4.7", goodRate = "93%", count = 38,
        reviews = listOf(
            CompanyReview("d3r1", "王先生", 5, "2026-10-02", "沟通顺畅，车子很新。", "马六甲一日游"),
            CompanyReview("d3r2", "林小姐", 4, "2026-09-20", "准时到达，服务态度好。", "机场接机"),
        ),
    ),
)

/** 我的评价 → 公司评价：公司汇总卡 + 旗下司机评价卡 + 公司评价记录 */
@Composable
fun CompanyReviewsScreen(onBack: () -> Unit = {}) {
    var selectedDriver by remember { mutableStateOf<DriverEntry?>(null) }

    val driver = selectedDriver
    if (driver != null) {
        DriverReviewsScreen(driver = driver, onBack = { selectedDriver = null })
        return
    }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        SubPageHeader(title = "公司评价", onBack = onBack)
        Spacer(Modifier.height(16.dp))

        SummaryCard(rating = "4.9", count = "共 128 条评价", goodRate = "好评率 96%")

        Spacer(Modifier.height(16.dp))
        FormSectionTitle("旗下司机")
        drivers.forEach { entry ->
            DriverCard(entry = entry, onClick = { selectedDriver = entry })
            Spacer(Modifier.height(10.dp))
        }

        Spacer(Modifier.height(16.dp))
        FormSectionTitle("评价记录")
        companyReviews.forEach { review ->
            ReviewCard(review)
            Spacer(Modifier.height(10.dp))
        }

        Spacer(Modifier.height(40.dp))
    }
}

/** 某位司机的评价记录页 */
@Composable
private fun DriverReviewsScreen(driver: DriverEntry, onBack: () -> Unit = {}) {
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        SubPageHeader(title = driver.name + " 的评价", onBack = onBack)
        Spacer(Modifier.height(16.dp))

        SummaryCard(
            rating = driver.rating,
            count = "共 " + driver.count + " 条评价",
            goodRate = "好评率 " + driver.goodRate,
        )

        Spacer(Modifier.height(16.dp))
        FormSectionTitle("评价记录")
        driver.reviews.forEach { review ->
            ReviewCard(review)
            Spacer(Modifier.height(10.dp))
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun SummaryCard(rating: String, count: String, goodRate: String) {
    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AdminColors.Card),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(rating, fontSize = 30.sp, fontWeight = FontWeight.Bold, color = AdminColors.Primary)
                Spacer(Modifier.height(2.dp))
                Text("平均评分", fontSize = 12.sp, color = AdminColors.TextMuted)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(count, fontSize = 15.sp, color = AdminColors.TextPrimary, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(4.dp))
                Text(goodRate, fontSize = 12.sp, color = AdminColors.TextMuted)
            }
        }
    }
}

@Composable
private fun DriverCard(entry: DriverEntry, onClick: () -> Unit) {
    Card(
        Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AdminColors.Card),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier.size(40.dp).clip(CircleShape).background(AdminColors.SurfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                Text(entry.name.take(1), color = AdminColors.TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(entry.name, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = AdminColors.TextPrimary)
                Spacer(Modifier.height(3.dp))
                Text("共 " + entry.count + " 条评价 · 好评率 " + entry.goodRate, fontSize = 12.sp, color = AdminColors.TextMuted)
            }
            Text(entry.rating, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AdminColors.Primary)
            Spacer(Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = AdminColors.TextMuted,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun ReviewCard(review: CompanyReview) {
    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AdminColors.Card),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(36.dp).clip(CircleShape).background(AdminColors.Primary),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        review.customer.take(1),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(review.customer, fontSize = 15.sp, color = AdminColors.TextPrimary, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(2.dp))
                    Text(review.date, fontSize = 12.sp, color = AdminColors.TextMuted)
                }
                Text(
                    text = "★".repeat(review.stars) + "☆".repeat(5 - review.stars),
                    fontSize = 14.sp,
                    color = Color(0xFFFBBF24),
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(review.comment, fontSize = 14.sp, color = AdminColors.TextPrimary, lineHeight = 20.sp)
            Spacer(Modifier.height(8.dp))
            Text(review.trip, fontSize = 12.sp, color = AdminColors.TextMuted)
        }
    }
}
