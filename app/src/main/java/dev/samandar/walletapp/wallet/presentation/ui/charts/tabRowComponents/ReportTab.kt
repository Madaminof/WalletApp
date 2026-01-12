package dev.samandar.walletapp.wallet.presentation.ui.charts.tabRowComponents

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
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.ui.theme.expenseColor
import dev.samandar.walletapp.ui.theme.incomeColor
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.domain.model.Category
import dev.samandar.walletapp.wallet.domain.model.Transaction
import dev.samandar.walletapp.wallet.domain.model.TransactionType
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.categories.getTranslatedName
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencyManager
import dev.samandar.walletapp.wallet.presentation.utils.formatAmountWithCurrency
import java.text.SimpleDateFormat
import java.util.*
val activeCurrency by CurrencyManager.currentCurrency

@Composable
fun ReportsTab(
    transactions: List<Transaction>,
    allCategories: List<Category>
) {
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
                title = stringResource(Strings.report_title_income).uppercase(),
                totalAmount = totalIncome,
                data = groupedIncomeData,
                color = incomeColor,
            )
        }

        item {
            ReportContainerCard(
                title = stringResource(Strings.report_title_expense).uppercase(),
                totalAmount = totalExpense,
                data = groupedExpenseData,
                color = expenseColor,
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
                amountColor = color,
                modifier = Modifier.fillMaxWidth()
            )
            Divider(thickness = 0.5.dp, modifier = Modifier.background(MaterialTheme.colorScheme.outline))
            data.forEach { (category, transactions) ->
                DetailedCategorySection(
                    category = category,
                    transactions = transactions,
                    color = color,
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
) {
    val totalBalance = remember { transactions.sumOf { it.amount } }
    var expanded by remember { mutableStateOf(false) }
    val categoryName = getTranslatedName(category.name)


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 7.dp, horizontal = 8.dp)
                .clip(RectangleShape)
                .clickable { expanded = !expanded },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(35.dp)
                    .clip(shape = CircleShape)
                    .background(Color(category.colorArgb).copy(0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = category.iconResId ?: 0),
                    contentDescription = category.name,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = categoryName.toString(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
            )

            Text(
                text = formatAmountWithCurrency(totalBalance),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onTertiary.copy(0.7f),
                textAlign = TextAlign.End
            )

            Icon(
                imageVector = if (expanded) Icons.Filled.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = if (expanded) stringResource(Strings.icon_content_description_close_note) else stringResource(Strings.icon_content_description_open_note),
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
                        text = stringResource(Strings.report_no_transactions_in_category),
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
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }
    val formattedDateTime = remember { dateFormat.format(Date(transaction.date)) }

    val accountName = getTranslatedName(transaction.account.name)
    val note = transaction.note.takeIf { !it.isNullOrBlank() }

    var noteExpanded by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 10.dp)
            .padding(top = 2.dp, bottom = 2.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(
                   painter = painterResource(transaction.account.iconResId?: R.drawable.ic_wallet_2),
                    contentDescription = stringResource(Strings.icon_content_description_account),
                    tint = Color.Unspecified,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = accountName.toString(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = formatAmountWithCurrency(transaction.amount),
                fontSize = 12.sp,
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
                        text = stringResource(Strings.transaction_note_label),
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
                    Icon(
                        imageVector = if (noteExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = if (noteExpanded)stringResource(Strings.icon_content_description_close_note) else stringResource(Strings.icon_content_description_open_note),
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
                    text = stringResource(Strings.report_general_summary),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.7f),
                    lineHeight = 11.sp
                )
            }
            Text(
                text = formatAmountWithCurrency(totalAmount),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = amountColor,
                textAlign = TextAlign.End
            )
        }
    }
}