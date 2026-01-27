package dev.samandar.walletapp.wallet.presentation.ui.home

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.samandar.walletapp.R
import dev.samandar.walletapp.navigation.Screen
import dev.samandar.walletapp.ui.theme.*
import dev.samandar.walletapp.wallet.presentation.ui.budjets.BudgetViewModel
import dev.samandar.walletapp.wallet.presentation.ui.home.budgetCard.BudgetCard
import dev.samandar.walletapp.wallet.presentation.ui.home.cardStatistics.ExpenseStatisticCardPremium
import dev.samandar.walletapp.wallet.presentation.ui.home.quickCards.QuickInCards
import dev.samandar.walletapp.wallet.presentation.ui.home.totalBalanceCard.TotalBalanceCard
import dev.samandar.walletapp.wallet.presentation.ui.home.totalBalanceCard.TotalBalanceCardViewModel

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("SuspiciousIndentation")
@Composable
fun HomeScreen(
    onActionClick: (String) -> Unit,
    navController: NavController,
    totalBalanceCardViewModel: TotalBalanceCardViewModel,
    budgetViewModel: BudgetViewModel = hiltViewModel(),
    listState: LazyListState,
) {
    val budgetState by budgetViewModel.budgetCardState.collectAsState()
    val homeItems = remember(budgetState) {
        listOf<@Composable () -> Unit>(
            { TotalBalanceCard(onFilterClick = totalBalanceCardViewModel::onFilterClick) },
            { QuickActionsRow(onActionClick) },
            { CashFlowCard() },
            { ExpenseStatisticCardPremium { navController.navigate(Screen.CategoryStatisticsScreen.route) } },
            { AccountCard(navController = navController) },
            {
                BudgetCard(
                    onCardClick = { navController.navigate(Screen.Budgets.route) },
                    hasActiveBudget = budgetState.hasActiveBudget,
                    budgetLimit = budgetState.totalBudgetLimit,
                    spentAmount = budgetState.totalSpentAmount,
                    navController = navController
                )
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds(),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
        ) {
            itemsIndexed(
                items = homeItems,
                key = { index, _ -> index }
            ) { index, itemContent ->
                HomeItemAnimator(index = index) {
                    itemContent()
                }
            }
            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@Composable
fun HomeItemAnimator(
    index: Int,
    content: @Composable () -> Unit
) {
    val isVisible = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(index * 25L)
        isVisible.value = true
    }

    val scale by animateFloatAsState(
        targetValue = if (isVisible.value) 1f else 0.92f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ), label = "scale"
    )

    val translateY by animateFloatAsState(
        targetValue = if (isVisible.value) 0f else 30f,
        animationSpec = spring(
            dampingRatio = 0.8f,
            stiffness = Spring.StiffnessMedium
        ), label = "translate"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isVisible.value) 1f else 0f,
        animationSpec = tween(durationMillis = 150, easing = LinearOutSlowInEasing),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .graphicsLayer {
                this.alpha = alpha
                this.scaleX = scale
                this.scaleY = scale
                this.translationY = translateY
            }
    ) {
        content()
    }
}

@Composable
fun QuickActionsRow(onActionClick: (String) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        val actions = listOf(
            Triple(R.drawable.budget_ic, R.string.quick_budjets, Screen.Budgets.route),
            Triple(R.drawable.shopp_list_ic, R.string.quick_shoppingList, Screen.ShoppingLists.route),
            Triple(R.drawable.debt_ic2, R.string.quick_Debts, Screen.DebtsScreen.route),
            Triple(R.drawable.statistic_icon, R.string.title_statistics, Screen.CategoryStatisticsScreen.route)
        )

        itemsIndexed(actions) { _, action ->
            QuickInCards(
                icon = action.first,
                title = stringResource(action.second),
                color = when(action.second) {
                    R.string.quick_budjets -> budjets
                    R.string.quick_shoppingList -> shoppingList
                    R.string.quick_Debts -> debts
                    else -> MaterialTheme.colorScheme.primary
                },
                onClick = {
                    onActionClick(action.third)
                }
            )
        }
    }
}