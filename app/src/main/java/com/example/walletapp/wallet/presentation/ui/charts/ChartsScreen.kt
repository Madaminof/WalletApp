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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.walletapp.wallet.domain.model.Transaction
import com.example.walletapp.wallet.presentation.ui.charts.expenseListComponents.ExpenseTransactionItem
import com.example.walletapp.wallet.presentation.ui.charts.tabRowComponents.BalanceTab
import com.example.walletapp.wallet.presentation.ui.charts.tabRowComponents.ReportsTab
import com.example.walletapp.wallet.presentation.ui.home.diogramCharts.DoughnutChart
import com.example.walletapp.wallet.presentation.viewmodel.CategoryData
import com.example.walletapp.wallet.presentation.viewmodel.HomeViewModel
import com.example.walletapp.wallet.presentation.viewmodel.categoryColors

enum class ChartTab(val title: String) {
    BALANCE("Balance"),
    EXPENSE("Chiqim"),
    INCOME("Kirim"),
    REPORTS("Hisobotlar")
}

@Composable
fun ChartsScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController
) {
    val transactions by viewModel.transactions.collectAsState()
    val accounts by viewModel.accounts.collectAsState()
    var selectedTab by remember { mutableStateOf(ChartTab.BALANCE) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }


    val allCategories by viewModel.allCategories.collectAsState()



    Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.onBackground)
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
                ChartTab.BALANCE -> BalanceTab(accounts,transactions)
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

@Composable
fun TransactionsTab(
    transactions: List<Transaction>,
    viewModel: HomeViewModel,
    selectedCategory: String? = null,
    currentTab: ChartTab
) {
    val filteredTransactions = transactions
        .filter { it.type.name == currentTab.name }
        .let { list ->
            selectedCategory?.let { category ->
                list.filter { it.category.name == category }
            } ?: list
        }
        .sortedByDescending { it.date }

    val categoryData = filteredTransactions
        .groupBy { it.category.name }
        .map { (name, list) ->
            CategoryData(
                categoryName = name,
                amount = list.sumOf { it.amount },
                color = categoryColors[name] ?: Color.Gray
            )
        }

    val totalAmount = categoryData.sumOf { it.amount }
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.onBackground)) {
        if (categoryData.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))

            DoughnutChart(
                data = categoryData,
                totalAmount = totalAmount,
                chartSize = 150.dp
            )
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
                modifier = Modifier.fillMaxSize()
                    .background(MaterialTheme.colorScheme.onBackground),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(filteredTransactions, key = { it.id }) { transaction ->
                    ExpenseTransactionItem(
                        transaction = transaction,
                        onDelete = { viewModel.deleteTransaction(transaction.id) }
                    )
                }
            }
        }
    }
}


