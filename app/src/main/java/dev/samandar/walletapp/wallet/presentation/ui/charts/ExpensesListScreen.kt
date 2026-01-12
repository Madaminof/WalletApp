package dev.samandar.walletapp.wallet.presentation.ui.charts

import android.annotation.SuppressLint
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.samandar.walletapp.R
import dev.samandar.walletapp.navigation.Screen
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.presentation.ui.charts.expenseListComponents.ExpenseTransactionItem
import dev.samandar.walletapp.wallet.presentation.ui.charts.expenseListComponents.SortSelectionDialog
import dev.samandar.walletapp.wallet.presentation.ui.home.diogramCharts.DoughnutChart
import dev.samandar.walletapp.wallet.presentation.ui.home.totalBalanceCard.CircularIconButton
import dev.samandar.walletapp.wallet.presentation.ui.home.totalBalanceCard.primaryAccent
import dev.samandar.walletapp.wallet.presentation.ui.topbars.topbarScreen.CustomTopBar
import dev.samandar.walletapp.wallet.presentation.viewmodel.CategoryData
import dev.samandar.walletapp.wallet.presentation.viewmodel.HomeViewModel
import dev.samandar.walletapp.wallet.presentation.viewmodel.categoryColors

enum class TabItem(val titleResId: Int) {
    EXPENSE(R.string.tab_expense),
    INCOME(R.string.tab_income),
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
    val currentTransactions = remember(transactions, selectedTabIndex, sortState) {

        transactions
            .filter { it.type.name == tabs[selectedTabIndex].name }
            .let { list ->
                when (sortState) {
                    SortState.DATE_DESC -> list.sortedByDescending { it.date }
                    SortState.AMOUNT_DESC -> list.sortedByDescending { it.amount }
                    SortState.AMOUNT_ASC -> list.sortedBy { it.amount }
                }
            }
    }
    val unknown = stringResource(R.string.unknown_category)
    val categoryData = remember(currentTransactions) {
        currentTransactions.groupBy { it.category?.name }
            .map { (name, list) ->
                CategoryData(
                    categoryName = name ?: unknown,
                    amount = list.sumOf { it.amount },
                    color = categoryColors[name] ?: Color.Gray
                )
            }
    }
    val totalAmount = remember(categoryData) { categoryData.sumOf { it.amount } }
    Scaffold(
        topBar = {
            CustomTopBar(
                title = stringResource(Strings.title_transactions),
                onBackClick = { navController.popBackStack() },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularIconButton(
                            onClick = {showSortDialog = true},
                            icon = Icons.Default.FilterList,
                            contentDescription = "Filter",
                            tint = primaryAccent,
                            backgroundColor = primaryAccent.copy(alpha = 0.1f),
                            size = 32.dp,
                        )
                        Spacer(modifier = Modifier.width(12.dp))
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
                            text = { Text(stringResource(tabItem.titleResId), fontWeight = FontWeight.SemiBold) },
                            selectedContentColor = MaterialTheme.colorScheme.primary,
                            unselectedContentColor = Color.Gray,
                        )
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        EmptyChartState(
                            currentTab = if (tabs[selectedTabIndex] == TabItem.EXPENSE)
                                ChartTab.EXPENSE else ChartTab.INCOME
                        )
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
                            items = currentTransactions,
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
    )
    if (showSortDialog) {
        SortSelectionDialog(
            currentSortState = sortState,
            onSortSelected = { newState -> sortState = newState; showSortDialog = false },
            onDismiss = { showSortDialog = false }
        )
    }

}
