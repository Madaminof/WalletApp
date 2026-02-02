package dev.samandar.walletapp.wallet.presentation.ui.charts.categoryStatistic

import CommonTabRow
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.samandar.walletapp.R
import dev.samandar.walletapp.navigation.Screen
import dev.samandar.walletapp.ui.theme.expenseColor
import dev.samandar.walletapp.ui.theme.incomeColor
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.domain.model.TransactionType
import dev.samandar.walletapp.wallet.presentation.ui.charts.EmptyChartState
import dev.samandar.walletapp.wallet.presentation.ui.charts.historyTransactions.TabItem
import dev.samandar.walletapp.wallet.presentation.ui.charts.categoryStatistic.viewmodel.CategoryStatisticsViewModel
import dev.samandar.walletapp.wallet.presentation.ui.home.diogramCharts.PremiumPieChart
import dev.samandar.walletapp.wallet.presentation.ui.topbars.topbarScreen.CustomTopBar
import dev.samandar.walletapp.wallet.presentation.utils.formatAmountWithCurrency
import java.net.URLEncoder
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.wallet.presentation.viewmodel.chartViewmodel.TimeFilter

@Composable
fun CategoryStatisticsScreen(
    categoryViewModel: CategoryStatisticsViewModel = hiltViewModel(),
    navController: NavController
) {
    val uiState by categoryViewModel.statisticsUiState.collectAsState()
    val selectedFilter by categoryViewModel.selectedFilter.collectAsState()
    val selectedTabType by categoryViewModel.selectedTab.collectAsState()

    var isFilterMenuExpanded by remember { mutableStateOf(false) }
    val tabs = listOf(TabItem.EXPENSE, TabItem.INCOME)

    val currentTabIndex = if (selectedTabType == TransactionType.EXPENSE) 0 else 1

    Scaffold(
        topBar = {
            CustomTopBar(
                title = stringResource(R.string.title_statistics),
                onBackClick = { navController.popBackStack() },
                actions = {
                    Box(contentAlignment = Alignment.Center) {
                        FilterActionButton(
                            onClick = {isFilterMenuExpanded = true},
                            icon = R.drawable.filter_ic,
                            modifier = Modifier.padding(end = 12.dp).size(32.dp),
                            bgColor = Color.Transparent,
                            icColor = MaterialTheme.colorScheme.onTertiary.copy(0.5f)

                        )

                        UniversalFilterMenu(
                            isExpanded = isFilterMenuExpanded,
                            onDismiss = { isFilterMenuExpanded = false },
                            selectedFilter = selectedFilter,
                            filters = TimeFilter.entries.toTypedArray(),
                            getLabel = { stringResource(it.titleResId) },
                            onFilterSelected = { categoryViewModel.onFilterChanged(it) }
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            CommonTabRow(
                selectedTabIndex = currentTabIndex,
                tabs = tabs,
                onTabSelected = { index ->
                    val type = if (index == 0) TransactionType.EXPENSE else TransactionType.INCOME
                    categoryViewModel.onTabChanged(type)
                }
            )

            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.categoryData.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyChartState()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RectangleShape,
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp, horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                PremiumPieChart(
                                    data = uiState.categoryData,
                                    totalAmount = uiState.totalAmount
                                )
                            }
                        }
                    }
                    item {
                        TotalAmountSection(
                            selectedTab = selectedTabType,
                            total = uiState.totalAmount,
                            filterTitle = stringResource(selectedFilter.titleResId)
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                    itemsIndexed(uiState.categoryData) { index, category ->
                        CategoryStatItem(
                            category = category,
                            total = uiState.totalAmount,
                            showDivider = index < uiState.categoryData.lastIndex,
                            onClick = {
                                val typeStr = if (selectedTabType == TransactionType.EXPENSE) "EXPENSE" else "INCOME"
                                val encodedName = URLEncoder.encode(category.categoryName, "UTF-8").replace("+", "%20")
                                navController.navigate(Screen.CategoryDetail.passArgs(encodedName, typeStr))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TotalAmountSection(selectedTab: TransactionType, total: Double, filterTitle: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$filterTitle - " + if (selectedTab == TransactionType.EXPENSE)
                stringResource(Strings.total_expense) else stringResource(Strings.total_income),
            color = Color.Gray,
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = formatAmountWithCurrency(total),
            color = if (selectedTab == TransactionType.EXPENSE) expenseColor else incomeColor,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            letterSpacing = (-0.5).sp
        )
    }
}