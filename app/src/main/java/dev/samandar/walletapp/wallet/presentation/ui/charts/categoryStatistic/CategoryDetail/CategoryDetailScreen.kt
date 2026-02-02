package dev.samandar.walletapp.wallet.presentation.ui.charts.categoryStatistic.CategoryDetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.domain.model.TransactionType
import dev.samandar.walletapp.wallet.presentation.ui.charts.categoryStatistic.CategoryDetail.viewmodel.CategoryDetailViewModel
import dev.samandar.walletapp.wallet.presentation.ui.charts.categoryStatistic.FilterActionButton
import dev.samandar.walletapp.wallet.presentation.ui.charts.categoryStatistic.UniversalFilterMenu
import dev.samandar.walletapp.wallet.presentation.ui.charts.historyTransactions.ExpenseTransactionItem
import dev.samandar.walletapp.wallet.presentation.ui.topbars.topbarScreen.CustomTopBar
import dev.samandar.walletapp.wallet.presentation.viewmodel.chartViewmodel.TimeFilter
import dev.samandar.walletapp.wallet.presentation.viewmodel.chartViewmodel.categoryColors


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
    val accentColor = categoryColors[categoryName] ?: MaterialTheme.colorScheme.primary

    val categoryTransactions = uiState.transactions
    val totalAmount = uiState.totalAmount
    val avgAmount = uiState.avgAmount
    val maxAmount = uiState.maxAmount


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
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .size(32.dp),
                            bgColor = Color.Transparent,
                            icColor = MaterialTheme.colorScheme.onTertiary.copy(0.5f)

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
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                CategoryAnalysisHeader(
                    totalAmount = totalAmount,
                    count = categoryTransactions.size,
                    avgAmount = avgAmount,
                    maxAmount = maxAmount,
                    color = accentColor,
                    transactionType = transactionType
                )
            }

            item {
                Column(modifier = Modifier.padding(vertical = 12.dp)) {
                    Text(
                        text = if (transactionType == TransactionType.EXPENSE) stringResource(
                            Strings.expense_dynamics
                        ) else stringResource(Strings.income_dynamics),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                    )

                    PremiumNativeChart(
                        transactions = categoryTransactions,
                        lineColor = accentColor,
                        modifier = Modifier
                    )
                }
            }

            item {
                Text(
                    text = stringResource(Strings.all_transactions),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(top = 28.dp, bottom = 12.dp)
                        .padding(horizontal = 20.dp)
                )
            }

            itemsIndexed(
                items = categoryTransactions,
                key = { _, tx -> tx.id }
            ) { index, transaction ->
                Surface(
                    color = Color.Transparent,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    ExpenseTransactionItem(
                        transaction = transaction,
                        onItemClick = {navController.navigate("${Screen.detailTransaction.route}/${transaction.id}")},
                        showDivider = index < categoryTransactions.size - 1
                    )
                }

                if (index < categoryTransactions.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        color = Color.White.copy(0.05f),
                        thickness = 0.5.dp
                    )
                }
            }
            if (categoryTransactions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(stringResource(Strings.no_data_found), color = Color.Gray)
                    }
                }
            }
        }
    }
}