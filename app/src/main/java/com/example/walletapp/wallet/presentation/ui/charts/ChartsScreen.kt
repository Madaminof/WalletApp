package com.example.walletapp.wallet.presentation.ui.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.walletapp.wallet.domain.model.Transaction
import com.example.walletapp.wallet.presentation.ui.charts.expenseListComponents.EditTransactionDialog
import com.example.walletapp.wallet.presentation.ui.charts.expenseListComponents.ExpenseTransactionItem
import com.example.walletapp.wallet.presentation.ui.charts.tabRowComponents.BalanceTab
import com.example.walletapp.wallet.presentation.ui.charts.tabRowComponents.ReportsTab
import com.example.walletapp.wallet.presentation.ui.home.diogramCharts.DoughnutChart
import com.example.walletapp.wallet.presentation.ui.otherScreens.topbar.CustomTopBar
import com.example.walletapp.wallet.presentation.viewmodel.CategoryData
import com.example.walletapp.wallet.presentation.viewmodel.HomeViewModel
import com.example.walletapp.wallet.presentation.viewmodel.categoryColors

enum class ChartTab(val title: String) {
    BALANCE("Balance"),
    EXPENSE("Expense"),
    INCOME("Income"),
    REPORTS("Reports")
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
                navController = navController,
                title = "Statistics",
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
                        text = { Text(tab.title, fontWeight = FontWeight.SemiBold) },
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
                    currentTab = ChartTab.EXPENSE
                )
                ChartTab.INCOME -> TransactionsTab(
                    transactions.filter { it.type.name == "INCOME" },
                    viewModel,
                    selectedCategory,
                    currentTab = ChartTab.INCOME

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
    currentTab: ChartTab
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var currentTransaction by remember { mutableStateOf<Transaction?>(null) }


    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }
    val filteredTransactions = transactions
        .filter { it.type.name == currentTab.name }
        .let { list ->
            selectedCategory?.let { category ->
                list.filter { it.category?.name == category }
            } ?: list
        }
        .sortedByDescending { it.date }

    val categoryData = filteredTransactions
        .groupBy { it.category?.name }
        .map { (name, list) ->
            CategoryData(
                categoryName = name?:"",
                amount = list.sumOf { it.amount },
                color = categoryColors[name] ?: Color.Gray
            )
        }

    val totalAmount = categoryData.sumOf { it.amount }
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primaryContainer)) {
        if (categoryData.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp),
                shape = RectangleShape,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
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
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (currentTab == ChartTab.EXPENSE)
                        "Hozircha hech qanday chiqimlar kiritilmagan."
                    else
                        "Hozircha hech qanday daromadlar kiritilmagan.",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentPadding = PaddingValues(vertical = 0.dp)
            ) {
                items(filteredTransactions, key = { it.id }) { transaction ->
                    ExpenseTransactionItem(
                        transaction = transaction,
                        onItemClick = { selectedTransaction = it }
                    )
                }
            }
            selectedTransaction?.let { transaction ->
                TransactionDetailBottomSheet(
                    transaction = transaction,
                    onDismiss = { selectedTransaction = null },
                    onUpdate = {
                        currentTransaction = transaction   // MUHIM
                        showEditDialog = true
                    },
                    onDelete = {
                        viewModel.deleteTransaction(
                            transactionId = transaction.id
                        )
                        selectedTransaction = null

                    }
                )
            }

            if (showEditDialog && currentTransaction != null) {
                EditTransactionDialog(
                    transaction = currentTransaction!!,
                    homeViewModel = viewModel,
                    onClose = {
                        showEditDialog = false
                        currentTransaction = null
                    }
                )
            }
        }
    }
}


