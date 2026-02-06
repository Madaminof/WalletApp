package dev.samandar.walletapp.wallet.presentation.ui.charts.categoryStatistic.CategoryDetail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.samandar.walletapp.R
import dev.samandar.walletapp.navigation.Screen
import dev.samandar.walletapp.ui.theme.expenseColor
import dev.samandar.walletapp.ui.theme.incomeColor
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.domain.model.TransactionType
import dev.samandar.walletapp.wallet.presentation.ui.charts.categoryStatistic.CategoryDetail.viewmodel.CategoryDetailViewModel
import dev.samandar.walletapp.wallet.presentation.ui.charts.categoryStatistic.FilterActionButton
import dev.samandar.walletapp.wallet.presentation.ui.charts.categoryStatistic.UniversalFilterMenu
import dev.samandar.walletapp.wallet.presentation.ui.charts.historyTransactions.ExpenseTransactionItem
import dev.samandar.walletapp.wallet.presentation.ui.topbars.topbarScreen.CustomTopBar
import dev.samandar.walletapp.wallet.presentation.viewmodel.chartViewmodel.TimeFilter
import dev.samandar.walletapp.wallet.presentation.viewmodel.chartViewmodel.categoryColors
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoryDetailScreen(
    categoryName: String,
    transactionType: TransactionType,
    viewModel: CategoryDetailViewModel = hiltViewModel(),
    navController: NavController,
) {
    LaunchedEffect(categoryName, transactionType) {
        viewModel.setCategoryParams(categoryName, transactionType)
    }

    val uiState by viewModel.detailUiState.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    var isFilterMenuExpanded by remember { mutableStateOf(false) }
    val accentColor = if (transactionType == TransactionType.EXPENSE) expenseColor else incomeColor

    val transactions = uiState.transactions

    val groupedTransactions = remember(transactions) {
        transactions.groupBy { tx ->
            Instant.ofEpochMilli(tx.date) // sening modelingdagi Long date
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = categoryName,
                onBackClick = { navController.popBackStack() },
                actions = {
                    Box(contentAlignment = Alignment.Center) {
                        FilterActionButton(
                            onClick = { isFilterMenuExpanded = true },
                            icon = R.drawable.filter_ic,
                            modifier = Modifier.padding(end = 12.dp).size(32.dp),
                            bgColor = Color.Transparent,
                            icColor = MaterialTheme.colorScheme.onTertiary.copy(0.6f)
                        )
                        UniversalFilterMenu(
                            isExpanded = isFilterMenuExpanded,
                            onDismiss = { isFilterMenuExpanded = false },
                            selectedFilter = selectedFilter,
                            filters = TimeFilter.entries.toTypedArray(),
                            getLabel = { stringResource(it.titleResId) },
                            onFilterSelected = { viewModel.onFilterChanged(it) }
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            item {
                CategoryHeroCard(
                    totalAmount = uiState.totalAmount,
                    periodLabel = when(selectedFilter) {
                        TimeFilter.WEEKLY -> stringResource(Strings.period_weekly)
                        TimeFilter.MONTHLY -> stringResource(Strings.period_monthly)
                        TimeFilter.YEARLY -> stringResource(Strings.period_yearly)
                        else -> stringResource(Strings.period_all)
                    },
                    count = transactions.size,
                    transactionType = transactionType,
                    peakAmount = uiState.peakAmount,
                    peakDate = uiState.peakDate,
                )
            }

            item {
                Column(modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)) {
                    Text(
                        text = if (transactionType == TransactionType.EXPENSE)
                            stringResource(Strings.expense_dynamics)
                        else stringResource(Strings.income_dynamics),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiary.copy(0.5f),
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )

                    PremiumNativeChart(
                        transactions = transactions,
                        lineColor = accentColor,
                        modifier = Modifier.fillMaxWidth().height(180.dp).padding(horizontal = 16.dp)
                    )
                }
            }

            if (transactions.isEmpty()) {
                item { EmptyStatePlaceholder() }
            } else {
                // TreeMap bo'yicha saralangan sanalar (Yangi kundan eski kunga)
                val sortedDates = groupedTransactions.keys.sortedDescending()

                sortedDates.forEach { date ->
                    stickyHeader {
                        TransactionDateHeader(date = date)
                    }

                    itemsIndexed(groupedTransactions[date] ?: emptyList()) { index, transaction ->
                        Surface(
                            color = Color.Transparent,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        ) {
                            ExpenseTransactionItem(
                                transaction = transaction,
                                onItemClick = {
                                    navController.navigate("${Screen.detailTransaction.route}/${transaction.id}")
                                },
                                showDivider = index < (groupedTransactions[date]?.size ?: 0) - 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionDateHeader(date: LocalDate) {
    val today = LocalDate.now()
    val label = when (date) {
        today -> stringResource(Strings.today)
        today.minusDays(1) -> stringResource(Strings.yesterday)
        else -> date.format(DateTimeFormatter.ofPattern("d MMMM, yyyy"))
    }

    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onTertiary.copy(0.5f),
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun EmptyStatePlaceholder() {
    Box(
        modifier = Modifier.fillMaxWidth().height(300.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(Strings.no_data_found),
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}