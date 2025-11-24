package com.example.walletapp.wallet.presentation.ui.charts.tabRowComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.wallet.domain.model.Category
import com.example.walletapp.wallet.domain.model.Transaction
import com.example.walletapp.wallet.domain.model.TransactionType

@Composable
fun ReportsTab(
    transactions: List<Transaction>,
    allCategories: List<Category>
) {
    val incomeCategories = allCategories.filter { it.type == TransactionType.INCOME }
    val expenseCategories = allCategories.filter { it.type == TransactionType.EXPENSE }

    val incomeTransactions = transactions.filter { it.type == TransactionType.INCOME }
    val expenseTransactions = transactions.filter { it.type == TransactionType.EXPENSE }

    val totalIncome = incomeTransactions.sumOf { it.amount }
    val totalExpense = expenseTransactions.sumOf { it.amount }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onBackground)
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Daromad", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiary)
                Text("Miqdor: ${totalIncome.toLong()} so'm", color = MaterialTheme.colorScheme.onTertiary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        items(incomeCategories) { category ->
            val balance = incomeTransactions
                .filter { it.category.id == category.id }
                .sumOf { it.amount }

            CategoryReportItem(
                icon = category.iconResId,
                name = category.name,
                balance = balance.toLong(),
                balanceColor = Color(0xFF4CAF50)
            )
        }
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Divider(color = Color.Gray.copy(alpha = 0.3f), thickness = 1.dp)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Xarajat", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiary)
                Text("Miqdor: ${totalExpense.toLong()} so'm", color = MaterialTheme.colorScheme.onTertiary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        items(expenseCategories) { category ->
            val balance = expenseTransactions
                .filter { it.category.id == category.id }
                .sumOf { it.amount }

            CategoryReportItem(
                icon = category.iconResId,
                name = category.name,
                balance = balance.toLong(),
                balanceColor = Color(0xFFE91E63)
            )
        }
    }
}

@Composable
fun CategoryReportItem(
    icon: Int?,
    name: String,
    balance: Long,
    balanceColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(balanceColor.copy(0.5f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = name,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onTertiary
        )

        Text(
            text = "$balance so'm",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = balanceColor
        )
    }
}
