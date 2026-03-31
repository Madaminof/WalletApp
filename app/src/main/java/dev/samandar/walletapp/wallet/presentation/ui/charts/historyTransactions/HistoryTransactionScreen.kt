package dev.samandar.walletapp.wallet.presentation.ui.charts.historyTransactions

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.samandar.walletapp.R
import dev.samandar.walletapp.navigation.Screen
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.domain.model.TransactionType
import dev.samandar.walletapp.wallet.presentation.ui.charts.EmptyChartState
import dev.samandar.walletapp.wallet.presentation.ui.charts.categoryStatistic.FilterActionButton
import dev.samandar.walletapp.wallet.presentation.ui.topbars.topbarScreen.HistoryCustomTopBar
import dev.samandar.walletapp.wallet.presentation.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Locale

enum class TabItem(val titleResId: Int) {
    EXPENSE(R.string.tab_expense),
    INCOME(R.string.tab_income),
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HistorytransactionScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController,
    accountId: String? = null
) {
    val transactions by viewModel.transactions.collectAsState()
    var isSortMenuExpanded by remember { mutableStateOf(false) }
    var currentPeriod by remember { mutableStateOf(TransactionPeriod.MONTHLY) }

    // 1. Search uchun State
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    // Tanlangan vaqtga qarab filtrlash
    val filteredTransactions = remember(transactions, currentPeriod, searchQuery, accountId) {
        transactions.filter { transaction ->
            // 1. Agar accountId kelsa, faqat o'sha account tranzaksiyalarini olamiz
            val matchesAccount = accountId == null || transaction.account.id == accountId

            // 2. Vaqt bo'yicha filtr
            val isInPeriod = when (currentPeriod) {
                TransactionPeriod.ALL -> true
                TransactionPeriod.MONTHLY -> DateFilterUtils.isSameMonth(transaction.date)
                TransactionPeriod.YEARLY -> DateFilterUtils.isSameYear(transaction.date)
            }

            // 3. Search bo'yicha filtr
            val matchesSearch = transaction.category.name.contains(searchQuery, ignoreCase = true) ||
                    (transaction.note?.contains(searchQuery, ignoreCase = true) ?: false)

            matchesAccount && isInPeriod && matchesSearch
        }.sortedByDescending { it.date }
    }
    val all = stringResource(Strings.summary_balance_all)
    val mothly = stringResource(Strings.summary_balance_monthly)
    val year = stringResource(Strings.summary_balance_yearly)


    val summaryTitle = remember(currentPeriod) {
        when (currentPeriod) {
            TransactionPeriod.ALL -> all
            TransactionPeriod.MONTHLY -> mothly
            TransactionPeriod.YEARLY -> year
        }
    }

    val totalIncome = remember(filteredTransactions) {
        filteredTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
    }
    val totalExpense = remember(filteredTransactions) {
        filteredTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    }
    val monthlyBalance = totalIncome - totalExpense

    val groupedTransactions = remember(filteredTransactions) {
        filteredTransactions.groupBy { transaction ->
            SimpleDateFormat("d MMMM, yyyy", Locale("uz")).format(transaction.date)
        }
    }

    val focusRequester = remember { FocusRequester() }
    val softwareKeyboardController =
        LocalSoftwareKeyboardController.current // Klaviatura boshqaruvi

    LaunchedEffect(isSearchActive) {
        if (isSearchActive) {
            focusRequester.requestFocus() // TextField'ga fokus beradi
            softwareKeyboardController?.show() // Klaviaturani chiqaradi
        }
    }

    Scaffold(
        topBar = {
            HistoryCustomTopBar(
                isSearchMode = isSearchActive,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                onSearchClose = {
                    isSearchActive = false
                    searchQuery = ""
                },
                title = stringResource(Strings.title_transactions),
                focusRequester = focusRequester,
                onBackClick = { navController.popBackStack() },
                actions = {
                    if (!isSearchActive) {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(
                                painter = painterResource(R.drawable.search_ic),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onTertiary.copy(0.5f),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(contentAlignment = Alignment.TopEnd) {
                            FilterActionButton(
                                onClick = { isSortMenuExpanded = true },
                                icon = R.drawable.filter_ic,
                                modifier = Modifier.padding(end = 12.dp),
                                size = 32.dp,
                                bgColor = Color.Transparent,
                                icColor = MaterialTheme.colorScheme.onTertiary.copy(0.5f)
                            )
                            PeriodSelectionMenu(
                                isExpanded = isSortMenuExpanded,
                                onDismiss = { isSortMenuExpanded = false },
                                currentPeriod = currentPeriod,
                                onPeriodSelected = { currentPeriod = it },
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(paddingValues)
        ) {
            MonthlySummaryCard(
                title = summaryTitle,
                balance = monthlyBalance,
                income = totalIncome,
                expense = totalExpense
            )
            if (groupedTransactions.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyChartState(
                        title = stringResource(Strings.no_transactions_title),
                        description = stringResource(Strings.no_transactions_desc),
                        icon = painterResource(R.drawable.empty_ic)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(bottom = 70.dp, top = 6.dp)
                ) {
                    groupedTransactions.forEach { (date, dailyItems) ->
                        item(key = date) {
                            DailyTransactionCard(
                                date = date,
                                transactions = dailyItems,
                                onItemClick = { item ->
                                    navController.navigate("${Screen.detailTransaction.route}/${item.id}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
