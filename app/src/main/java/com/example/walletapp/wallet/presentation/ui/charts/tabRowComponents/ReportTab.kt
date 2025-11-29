package com.example.walletapp.wallet.presentation.ui.charts.tabRowComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.ui.theme.expenseColor
import com.example.walletapp.ui.theme.incomeColor
import com.example.walletapp.wallet.domain.model.Category
import com.example.walletapp.wallet.domain.model.Transaction
import com.example.walletapp.wallet.domain.model.TransactionType
import java.text.DecimalFormat


@Composable
fun ReportsTab(
    transactions: List<Transaction>,
    allCategories: List<Category>
) {
    val formatter = remember { DecimalFormat("#,##0") }

    val allIncomeData = allCategories
        .filter { it.type == TransactionType.INCOME }
        .map { category ->
            val balance = transactions.filter { it.category.id == category.id }.sumOf { it.amount }
            category to balance
        }

    val allExpenseData = allCategories
        .filter { it.type == TransactionType.EXPENSE }
        .map { category ->
            val balance = transactions.filter { it.category.id == category.id }.sumOf { it.amount }
            category to balance
        }

    val sortedIncome = allIncomeData
        .sortedWith(compareByDescending<Pair<Category, Double>> { it.second }
            .thenBy { it.second == 0.0 }
            .thenBy { it.first.name }
        )

    val sortedExpense = allExpenseData
        .sortedWith(compareByDescending<Pair<Category, Double>> { it.second }
            .thenBy { it.second == 0.0 }
            .thenBy { it.first.name }
        )

    val totalIncome = sortedIncome.sumOf { it.second }
    val totalExpense = sortedExpense.sumOf { it.second }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onPrimaryContainer),
        contentPadding = PaddingValues(top = 12.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            TotalSectionHeader(
                title = "Daromad",
                totalAmount = totalIncome,
                formatter = formatter,
                amountColor = incomeColor
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(sortedIncome) { (category, balance) ->
            CategoryReportItemPremium(
                icon = category.iconResId,
                name = category.name,
                balance = balance,
                balanceColor = incomeColor
            )
        }
        if (sortedIncome.isNotEmpty() || sortedExpense.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Divider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    thickness = 0.5.dp
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
        item {
            TotalSectionHeader(
                title = "Xarajat",
                totalAmount = totalExpense,
                formatter = formatter,
                amountColor = expenseColor
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(sortedExpense) { (category, balance) ->
            CategoryReportItemPremium(
                icon = category.iconResId,
                name = category.name,
                balance = balance,
                balanceColor = expenseColor
            )
        }
    }
}

@Composable
fun TotalSectionHeader(
    title: String,
    totalAmount: Double,
    formatter: DecimalFormat,
    amountColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onTertiary
        )
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "JAMI MIQDOR",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onTertiary.copy(0.7f)
            )
            Text(
                text = "${formatter.format(totalAmount)} so'm",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
        }
    }
}


@Composable
fun CategoryReportItemPremium(
    icon: Int?,
    name: String,
    balance: Double,
    balanceColor: Color
) {
    val formatter = remember { DecimalFormat("#,##0") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(balanceColor.copy(alpha = 0.2f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = name,
                    tint = balanceColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = name,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "${formatter.format(balance)} so'm",
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = balanceColor
        )
    }
}