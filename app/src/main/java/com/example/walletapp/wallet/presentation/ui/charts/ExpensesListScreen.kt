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
import com.example.walletapp.wallet.presentation.ui.charts.expenseListComponents.ExpenseTransactionItem
import com.example.walletapp.wallet.presentation.ui.home.diogramCharts.DoughnutChart
import com.example.walletapp.wallet.presentation.ui.otherScreens.topbar.CustomTopBar
import com.example.walletapp.wallet.presentation.viewmodel.CategoryData
import com.example.walletapp.wallet.presentation.viewmodel.HomeViewModel
import com.example.walletapp.wallet.presentation.viewmodel.categoryColors

enum class TabItem(val title: String) {
    EXPENSE("Chiqimlar"),
    INCOME("Daromadlar"),
}

@Composable
fun ExpensesListScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController
) {
    val transactions by viewModel.transactions.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf(TabItem.EXPENSE, TabItem.INCOME)

    Scaffold(
        topBar = {
            CustomTopBar(
                navController = navController,
                title = "Hisobotlar",
                onBackClick = { navController.popBackStack() }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.onBackground)
                    .padding(paddingValues)
            ) {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    tabs.forEachIndexed { index, tabItem ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(tabItem.title, fontWeight = FontWeight.SemiBold) },
                            selectedContentColor = MaterialTheme.colorScheme.primary,
                            unselectedContentColor = Color.Gray,
                        )
                    }
                }
                val currentTransactions = transactions
                    .filter { it.type.name == tabs[selectedTabIndex].name }
                    .sortedByDescending { it.date }

                val categoryData = currentTransactions
                    .groupBy { it.category.name }
                    .map { (name, list) ->
                        CategoryData(
                            categoryName = name,
                            amount = list.sumOf { it.amount },
                            color = categoryColors[name] ?: Color.Gray
                        )
                    }

                val totalAmount = categoryData.sumOf { it.amount }

                Spacer(modifier = Modifier.height(4.dp))
                DoughnutChart(
                    data = categoryData,
                    totalAmount = totalAmount,
                    chartSize = 150.dp
                )
                if (currentTransactions.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (tabs[selectedTabIndex] == TabItem.EXPENSE)
                                "Hozircha hech qanday chiqimlar kiritilmagan."
                            else
                                "Hozircha hech qanday daromadlar kiritilmagan.",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyColumn(modifier = Modifier.fillMaxSize()
                        .background(MaterialTheme.colorScheme.onBackground)) {
                        items(currentTransactions, key = { it.id }) { transaction ->
                            ExpenseTransactionItem(
                                transaction = transaction,
                                onDelete = { viewModel.deleteTransaction(transaction.id) }
                            )
                        }
                    }
                }
            }
        }
    )
}