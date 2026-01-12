package dev.samandar.walletapp.wallet.presentation.ui.charts

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.samandar.walletapp.R
import dev.samandar.walletapp.navigation.Screen
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.domain.model.Transaction
import dev.samandar.walletapp.wallet.presentation.ui.charts.expenseListComponents.ExpenseTransactionItem
import dev.samandar.walletapp.wallet.presentation.ui.charts.tabRowComponents.BalanceTab
import dev.samandar.walletapp.wallet.presentation.ui.charts.tabRowComponents.ReportsTab
import dev.samandar.walletapp.wallet.presentation.ui.home.diogramCharts.DoughnutChart
import dev.samandar.walletapp.wallet.presentation.ui.topbars.topbarScreen.CustomTopBar
import dev.samandar.walletapp.wallet.presentation.viewmodel.CategoryData
import dev.samandar.walletapp.wallet.presentation.viewmodel.HomeViewModel
import dev.samandar.walletapp.wallet.presentation.viewmodel.categoryColors

enum class ChartTab(val titleResId: Int) {
    BALANCE(R.string.tab_balance),
    EXPENSE(R.string.tab_expense),
    INCOME(R.string.tab_income),
    REPORTS(R.string.tab_reports)
}

@Composable
fun ChartsScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController
) {
    val transactions by viewModel.transactions.collectAsState()
    var selectedTab by remember { mutableStateOf(ChartTab.BALANCE) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    val allCategories by viewModel.allCategories.collectAsState()

    Scaffold(
        topBar = {
            CustomTopBar(
                title = stringResource(Strings.title_statistics),
                onBackClick = {navController.popBackStack()},
            )
        }
    ) {paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            val tabs = ChartTab.values()
            TabRow(
                selectedTabIndex = tabs.indexOf(selectedTab),
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = {
                            selectedTab = tab
                            selectedCategory = null
                        },
                        text = { Text(stringResource(tab.titleResId), fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = Color.Gray
                    )
                }
            }
            when (selectedTab) {
                ChartTab.BALANCE -> BalanceTab()
                ChartTab.EXPENSE -> TransactionsTab(
                    transactions.filter { it.type.name == "EXPENSE" },
                    viewModel,
                    selectedCategory,
                    currentTab = ChartTab.EXPENSE,
                    navController = navController
                )
                ChartTab.INCOME -> TransactionsTab(
                    transactions.filter { it.type.name == "INCOME" },
                    viewModel,
                    selectedCategory,
                    currentTab = ChartTab.INCOME,
                    navController = navController
                )
                ChartTab.REPORTS -> ReportsTab(
                    transactions = transactions,
                    allCategories = allCategories
                )
            }
        }
    }
}

@Composable
fun TransactionsTab(
    transactions: List<Transaction>,
    viewModel: HomeViewModel,
    selectedCategory: String? = null,
    currentTab: ChartTab,
    navController: NavController
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var currentTransaction by remember { mutableStateOf<Transaction?>(null) }
    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }

    val filteredTransactions = remember(transactions, currentTab, selectedCategory) {
        transactions
            .filter { it.type.name == currentTab.name }
            .let { list ->
                selectedCategory?.let { category ->
                    list.filter { it.category?.name == category }
                } ?: list
            }
            .sortedByDescending { it.date }
    }
    val categoryData = remember(filteredTransactions) {
        filteredTransactions
            .groupBy { it.category?.name }
            .map { (name, list) ->
                CategoryData(
                    categoryName = name ?: "",
                    amount = list.sumOf { it.amount },
                    color = categoryColors[name] ?: Color.Gray
                )
            }
    }
    val totalAmount = categoryData.sumOf { it.amount }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        if (categoryData.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RectangleShape,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    DoughnutChart(
                        data = categoryData,
                        totalAmount = totalAmount,
                    )
                }
            }
        }
        if (filteredTransactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                EmptyChartState(currentTab = currentTab)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                itemsIndexed(
                    items = filteredTransactions,
                    key = { _, transaction -> transaction.id }
                ) { index, transaction ->
                    ExpenseTransactionItem(
                        transaction = transaction,
                        index = index,
                        onItemClick = { item ->
                            navController.navigate("${Screen.detailTransaction.route}/${item.id}")
                        },
                        modifier = Modifier.animateItem(
                            fadeInSpec = null,
                            placementSpec = spring(stiffness = Spring.StiffnessMedium)
                        )
                    )
                }
            }
        }
    }

}