package com.example.walletapp.wallet.presentation.ui.charts.tabRowComponents

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.ui.theme.expenseColor
import com.example.walletapp.ui.theme.incomeColor
import com.example.walletapp.wallet.domain.model.Category
import com.example.walletapp.wallet.domain.model.Transaction
import com.example.walletapp.wallet.domain.model.TransactionType
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

@Composable
fun ReportsTab(
    transactions: List<Transaction>,
    allCategories: List<Category>
) {
    val formatter = remember { DecimalFormat("#,##0") }

    val allIncomeCategories = allCategories.filter { it.type == TransactionType.INCOME }
    val allExpenseCategories = allCategories.filter { it.type == TransactionType.EXPENSE }

    val groupedIncomeData = remember(transactions, allCategories) {
        allIncomeCategories.map { category ->
            category to transactions.filter { it.category?.id == category.id }
        }.sortedByDescending { it.second.sumOf { tx -> tx.amount } }
    }

    val groupedExpenseData = remember(transactions, allCategories) {
        allExpenseCategories.map { category ->
            category to transactions.filter { it.category?.id == category.id }
        }.sortedByDescending { it.second.sumOf { tx -> tx.amount } }
    }

    val totalIncome = groupedIncomeData.sumOf { it.second.sumOf { tx -> tx.amount } }
    val totalExpense = groupedExpenseData.sumOf { it.second.sumOf { tx -> tx.amount } }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ReportContainerCard(
                title = "Income",
                totalAmount = totalIncome,
                data = groupedIncomeData,
                color = incomeColor,
                formatter = formatter
            )
        }

        item {
            ReportContainerCard(
                title = "Expense",
                totalAmount = totalExpense,
                data = groupedExpenseData,
                color = expenseColor,
                formatter = formatter
            )
        }
    }
}

@Composable
fun ReportContainerCard(
    title: String,
    totalAmount: Double,
    data: List<Pair<Category, List<Transaction>>>,
    color: Color,
    formatter: DecimalFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.6f)
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            TotalSectionHeader(
                title = title,
                totalAmount = totalAmount,
                formatter = formatter,
                amountColor = color,
                modifier = Modifier.fillMaxWidth()
            )
            Divider(thickness = 0.5.dp, modifier = Modifier.background(MaterialTheme.colorScheme.outline))
            data.forEach { (category, transactions) ->
                DetailedCategorySection(
                    category = category,
                    transactions = transactions,
                    color = color,
                    formatter = formatter
                )
            }
        }
    }
}

@Composable
fun DetailedCategorySection(
    category: Category,
    transactions: List<Transaction>,
    color: Color,
    formatter: DecimalFormat
) {
    val totalBalance = remember { transactions.sumOf { it.amount } }
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 8.dp)
                .clip(RectangleShape)
                .clickable { expanded = !expanded },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(35.dp)
                    .clip(shape = CircleShape)
                    .background(Color(category.colorArgb)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = category.iconResId ?: 0),
                    contentDescription = category.name,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = category.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
            )

            Text(
                text = "${formatter.format(totalBalance)} so'm",
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                textAlign = TextAlign.End
            )

            Icon(
                imageVector = if (expanded) Icons.Filled.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = if (expanded) "Yopish" else "Ochish",
                tint = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                modifier = Modifier.size(20.dp).padding(start = 4.dp)
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)
        ) {
            Column(modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)) {
                if (transactions.isEmpty()) {
                    Text(
                        text = "Bu kategoriyada hozircha tranzaksiyalar mavjud emas.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onTertiary.copy(0.7f),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        textAlign = TextAlign.Center
                    )
                } else {
                    transactions
                        .sortedByDescending { it.date }
                        .forEach { transaction ->
                            TransactionItem(
                                transaction = transaction,
                                color = color.copy(alpha = 0.8f),
                                formatter = formatter
                            )
                            if (transactions.last() != transaction) {
                            }
                        }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    color: Color,
    formatter: DecimalFormat
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }
    val formattedDateTime = remember { dateFormat.format(Date(transaction.date)) }

    val accountName = transaction.account.name
    val note = transaction.note.takeIf { !it.isNullOrBlank() }

    var noteExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Filled.AccountBalanceWallet,
                    contentDescription = "Account",
                    tint = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = accountName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "${formatter.format(transaction.amount)} so'm",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                textAlign = TextAlign.End,
                lineHeight = 13.sp
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = formattedDateTime,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onTertiary.copy(0.5f),
                lineHeight = 9.sp
            )
        }

        if (note != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { noteExpanded = !noteExpanded }
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Izoh:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = note,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal,
                        maxLines = if (noteExpanded) Int.MAX_VALUE else 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.7f),
                        modifier = Modifier.weight(1f)
                    )

                    // Icon
                    Icon(
                        imageVector = if (noteExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = if (noteExpanded) "Izohni yopish" else "Izohni ochish",
                        tint = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp).padding(start = 4.dp)
                    )
                }

            }
        }
    }
    Divider(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp),
        color = MaterialTheme.colorScheme.onTertiary.copy(0.1f),
        thickness = 0.5.dp
    )
}

@Composable
fun TotalSectionHeader(
    title: String,
    totalAmount: Double,
    formatter: DecimalFormat,
    amountColor: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier) {
                Text(
                    text = title.uppercase(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = amountColor,
                    lineHeight = 14.sp
                )
                Text(
                    text = "Umumiy Hisobot",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.7f),
                    lineHeight = 11.sp
                )
            }
            Text(
                text = "${formatter.format(totalAmount)} so'm",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = amountColor,
                textAlign = TextAlign.End
            )
        }
    }
}