package com.example.walletapp.wallet.presentation.ui.charts.tabRowComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.ui.theme.expenseColor // Bu ranglar loyihangizda mavjud deb hisoblanadi
import com.example.walletapp.ui.theme.incomeColor   // Bu ranglar loyihangizda mavjud deb hisoblanadi
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
            val balance = transactions.filter { it.category?.id == category.id }.sumOf { it.amount }
            category to balance
        }

    val allExpenseData = allCategories
        .filter { it.type == TransactionType.EXPENSE }
        .map { category ->
            val balance = transactions.filter { it.category?.id == category.id }.sumOf { it.amount }
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

    val screenBackgroundColor = MaterialTheme.colorScheme.primaryContainer

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBackgroundColor),
        contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item {
            TotalSectionHeader(
                title = "Daromad",
                totalAmount = totalIncome,
                formatter = formatter,
                amountColor = incomeColor,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        items(sortedIncome) { (category, balance) ->
            CategoryReportItem(
                icon = category.iconResId,
                name = category.name,
                balance = balance,
                balanceColor = incomeColor
            )
        }

        item {
            TotalSectionHeader(
                title = "Xarajat",
                totalAmount = totalExpense,
                formatter = formatter,
                amountColor = expenseColor,
                modifier = Modifier.padding(bottom = 4.dp, top = 4.dp)
            )
        }

        items(sortedExpense) { (category, balance) ->
            CategoryReportItem(
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
    amountColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = amountColor.copy(0.1f)
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp)
                .padding(end = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                ) {
                Text(
                    text = title.uppercase(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = amountColor
                )
                Text(
                    text = "Hisobot Jami",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "${formatter.format(totalAmount)} so'm",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = amountColor,
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
fun CategoryReportItem(
    icon: Int?,
    name: String,
    balance: Double,
    balanceColor: Color
) {
    val formatter = remember { DecimalFormat("#,##0") }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(35.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(balanceColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = name,
                        tint = balanceColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = name,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onTertiary.copy(0.7f)
            )
            Text(
                text = "${formatter.format(balance)} so'm",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = balanceColor.copy(alpha = 0.9f),
                textAlign = TextAlign.End
            )
        }
    }
}