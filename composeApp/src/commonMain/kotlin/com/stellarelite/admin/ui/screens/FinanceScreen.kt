package com.stellarelite.admin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellarelite.admin.model.DriverExpense
import com.stellarelite.admin.ui.components.*
import com.stellarelite.admin.ui.theme.AdminColors

@Composable
fun FinanceScreen(
    expenses: List<DriverExpense>,
    loading: Boolean,
    onRefresh: () -> Unit
) {
    var selectedTab by remember { mutableStateOf("expenses") }

    Column(
        modifier = Modifier.fillMaxSize().background(AdminColors.Background)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("财务管理", color = AdminColors.TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Black)
            ActionButton("刷新", onClick = { onRefresh() }, color = AdminColors.SurfaceVariant)
        }

        // Tab selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip("司机开销", selected = selectedTab == "expenses", onClick = { selectedTab = "expenses" })
            FilterChip("财务申请", selected = selectedTab == "requests", onClick = { selectedTab = "requests" })
            FilterChip("钱包流水", selected = selectedTab == "ledger", onClick = { selectedTab = "ledger" })
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedTab) {
            "expenses" -> ExpenseListView(expenses = expenses, loading = loading)
            "requests" -> EmptyView("财务申请（建设中）")
            "ledger" -> EmptyView("钱包流水（建设中）")
        }
    }
}

@Composable
fun ExpenseListView(expenses: List<DriverExpense>, loading: Boolean) {
    if (loading) {
        LoadingView()
        return
    }
    if (expenses.isEmpty()) {
        EmptyView("暂无司机开销记录")
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(expenses) { expense ->
            ExpenseCard(expense = expense)
        }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun ExpenseCard(expense: DriverExpense) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AdminColors.Card)
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    expense.usage_type.ifEmpty { "开销" },
                    color = AdminColors.TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${expense.currency} ${"%.2f".format(expense.amount)}",
                    color = if (expense.currency == "RM") AdminColors.Warning else AdminColors.Info,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            InfoRow("日期", expense.expense_date)
            InfoRow("车牌", expense.plate_no.ifEmpty { "-" })
            if (expense.note.isNotEmpty()) InfoRow("备注", expense.note)
        }
    }
}
