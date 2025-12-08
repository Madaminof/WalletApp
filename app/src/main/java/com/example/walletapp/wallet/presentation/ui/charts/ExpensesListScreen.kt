package com.example.walletapp.wallet.presentation.ui.charts

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.walletapp.wallet.domain.model.TransactionType
import com.example.walletapp.wallet.presentation.ui.charts.expenseListComponents.EditTransactionDialog
import com.example.walletapp.wallet.presentation.ui.charts.expenseListComponents.ExpenseTransactionItem
import com.example.walletapp.wallet.presentation.ui.charts.expenseListComponents.SortSelectionDialog
import com.example.walletapp.wallet.presentation.ui.home.addTransaction.addtransactionScreen2.AddTransactionBottomSheet
import com.example.walletapp.wallet.presentation.ui.home.diogramCharts.DoughnutChart
import com.example.walletapp.wallet.presentation.ui.otherScreens.topbar.CustomTopBar
import com.example.walletapp.wallet.presentation.viewmodel.CategoryData
import com.example.walletapp.wallet.presentation.viewmodel.HomeViewModel
import com.example.walletapp.wallet.presentation.viewmodel.categoryColors

enum class TabItem(val title: String) {
    EXPENSE("Xarajat"),
    INCOME("Daromad"),
}

enum class SortState {
    DATE_DESC,
    AMOUNT_DESC,
    AMOUNT_ASC
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ExpensesListScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController
) {
    val transactions by viewModel.transactions.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf(TabItem.EXPENSE, TabItem.INCOME)

    var sortState by remember { mutableStateOf(SortState.DATE_DESC) }
    var showSortDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var currentTransaction by remember { mutableStateOf<Transaction?>(null) }
    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }

    Scaffold(
        topBar = {
            CustomTopBar(
                navController = navController,
                title = "Transactions",
                onBackClick = { navController.popBackStack() },
                actions = {
                    IconButton(onClick = { showSortDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.SortByAlpha,
                            contentDescription = "Saralash",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
            ) {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = MaterialTheme.colorScheme.primary,
                            height = 3.dp
                        )
                    }
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
                val filteredTransactions = transactions
                    .filter { it.type.name == tabs[selectedTabIndex].name }

                val currentTransactions by remember(filteredTransactions, sortState) {
                    derivedStateOf {
                        when (sortState) {
                            SortState.DATE_DESC -> filteredTransactions.sortedByDescending { it.date }
                            SortState.AMOUNT_DESC -> filteredTransactions.sortedByDescending { it.amount }
                            SortState.AMOUNT_ASC -> filteredTransactions.sortedBy { it.amount }
                        }
                    }
                }
                val categoryData by remember(currentTransactions) {
                    derivedStateOf {
                        currentTransactions.groupBy { it.category?.name }
                            .map { (name, list) ->
                                CategoryData(
                                    categoryName = name ?: "Noma'lum",
                                    amount = list.sumOf { it.amount },
                                    color = categoryColors[name] ?: Color.Gray
                                )
                            }
                    }
                }
                val totalAmount by remember(categoryData) {
                    derivedStateOf {
                        categoryData.sumOf { it.amount }
                    }
                }

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
                if (currentTransactions.isEmpty()) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Hozircha hech qanday tranzaksiyalar kiritilmagan.",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentPadding = PaddingValues(vertical = 0.dp)
                    ) {
                        items(currentTransactions, key = { it.id }) { transaction ->
                            ExpenseTransactionItem(
                                transaction = transaction,
                                onItemClick = { selectedTransaction = it }
                            )
                        }
                    }
                }

            }
        }
    )
    if (showSortDialog) {
        SortSelectionDialog(
            currentSortState = sortState,
            onSortSelected = { newState ->
                sortState = newState
                showSortDialog = false
            },
            onDismiss = { showSortDialog = false }
        )
    }



    selectedTransaction?.let { transaction ->
        TransactionDetailBottomSheet(
            transaction = transaction,
            onDismiss = { selectedTransaction = null },
            onUpdate = {
                currentTransaction = transaction
                showEditDialog = true
            },
            onDelete = {
                viewModel.deleteTransaction(transactionId = transaction.id)
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
