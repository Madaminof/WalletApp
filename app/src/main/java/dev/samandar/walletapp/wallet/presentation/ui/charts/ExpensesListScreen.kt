package dev.samandar.walletapp.wallet.presentation.ui.charts

import CommonTabRow
import android.annotation.SuppressLint
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.samandar.walletapp.R
import dev.samandar.walletapp.navigation.Screen
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.domain.model.TransactionType
import dev.samandar.walletapp.wallet.presentation.ui.charts.categoryStatistic.FilterActionButton
import dev.samandar.walletapp.wallet.presentation.ui.charts.categoryStatistic.viewmodel.CategoryStatisticsViewModel
import dev.samandar.walletapp.wallet.presentation.ui.charts.expenseListComponents.ExpenseTransactionItem
import dev.samandar.walletapp.wallet.presentation.ui.charts.expenseListComponents.SortSelectionMenu
import dev.samandar.walletapp.wallet.presentation.ui.topbars.topbarScreen.CustomTopBar
import dev.samandar.walletapp.wallet.presentation.viewmodel.HomeViewModel
import dev.samandar.walletapp.wallet.presentation.viewmodel.chartViewmodel.CategoryData
import dev.samandar.walletapp.wallet.presentation.viewmodel.chartViewmodel.categoryColors
import dev.samandar.walletapp.wallet.presentation.viewmodel.chartViewmodel.getCategoryIcon

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
    categoryViewModel: CategoryStatisticsViewModel = hiltViewModel(),
    navController: NavController,
) {
    val transactions by viewModel.transactions.collectAsState()
    val selectedTabType by categoryViewModel.selectedTab.collectAsState()
    val selectedFilter by categoryViewModel.selectedFilter.collectAsState()

    var sortState by remember { mutableStateOf(SortState.DATE_DESC) }
    var isSortMenuExpanded by remember { mutableStateOf(false) }
    val tabs = listOf(TabItem.EXPENSE, TabItem.INCOME)

    val selectedTabIndex = if (selectedTabType == TransactionType.EXPENSE) 0 else 1

    val currentTransactions = remember(transactions, selectedTabType, selectedFilter, sortState) {
        transactions
            .filter { it.type == selectedTabType }
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
                val catName = name ?: unknown
                CategoryData(
                    categoryName = catName,
                    amount = list.sumOf { it.amount },
                    color = categoryColors[catName] ?: Color.Gray,
                    iconResId = getCategoryIcon(catName)
                )
            }
            .sortedByDescending { it.amount }
    }

    val totalAmount = remember(categoryData) { categoryData.sumOf { it.amount } }
    Scaffold(
        topBar = {
            CustomTopBar(
                title = stringResource(Strings.title_transactions),
                onBackClick = { navController.popBackStack() },
                actions = {
                    Box(
                        modifier = Modifier.wrapContentSize(Alignment.TopEnd),
                        contentAlignment = Alignment.TopEnd
                    ) {
                        FilterActionButton(
                            onClick = { isSortMenuExpanded = true }, // Menyu ochiladi
                            icon = Icons.Default.FilterList,
                            modifier = Modifier.padding(end = 12.dp),
                            size = 32.dp
                        )
                        SortSelectionMenu(
                            isExpanded = isSortMenuExpanded,
                            onDismiss = { isSortMenuExpanded = false },
                            currentSortState = sortState,
                            onSortSelected = { newSort ->
                                sortState = newSort
                                isSortMenuExpanded = false
                            }
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
                CommonTabRow(
                    selectedTabIndex = selectedTabIndex,
                    tabs = tabs,
                    onTabSelected = { index ->
                        val type =
                            if (index == 0) TransactionType.EXPENSE else TransactionType.INCOME
                        categoryViewModel.onTabChanged(type)
                    }
                )
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
                        item {
                            Spacer(modifier = Modifier.height(60.dp))
                        }
                    }
                }

            }
        }
    )

}
