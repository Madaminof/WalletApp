package com.example.walletapp.wallet.presentation.ui.home

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.walletapp.R
import com.example.walletapp.navigation.Screen
import com.example.walletapp.ui.theme.balance
import com.example.walletapp.ui.theme.budjets
import com.example.walletapp.ui.theme.debts
import com.example.walletapp.ui.theme.goals
import com.example.walletapp.ui.theme.shoppingList
import com.example.walletapp.wallet.presentation.ui.home.cardGoals.GoalsCard
import com.example.walletapp.wallet.presentation.ui.home.cardStatistics.ExpenseStatisticCardPremium

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("SuspiciousIndentation")
@Composable
fun HomeScreen(
    onActionClick: (Int) -> Unit,
    navController: NavController,
) {
    val listState = rememberLazyListState()
    val scrollOffset = listState.firstVisibleItemScrollOffset
    val progress = (scrollOffset / 180f).coerceIn(0f, 1f)
    val cardAlpha by animateFloatAsState(1f - progress)
    val cardOffset by animateDpAsState((progress * (-40)).dp)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds()
                .animateContentSize(),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
        ) {
            item { IncomeExpenseCard(
                modifier = Modifier
                    .alpha(cardAlpha)
                    .offset(y = cardOffset)
            ) }
            item { QuickActionsRow(onActionClick,navController) }
            item { ExpenseStatisticCardPremium(
                onMoreClick = {navController.navigate(Screen.ExpenseList.route)},
            ) }
            item { GoalsCard(onCreateGoalClick = {navController.navigate(Screen.Goals.route)}) }
        }
    }
}

@Composable
fun QuickActionsRow(onActionClick: (Int) -> Unit,navController: NavController) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        item {
            QuickInCards(
                Icons.Default.AccountBalanceWallet,
                title = stringResource(R.string.quick_budjets),
                color = budjets,
                onClick = { onActionClick(2) }
            )
        }
        item {
            QuickInCards(
                Icons.Default.ShoppingCart,
                title = stringResource(R.string.quick_shoppingList),
                color = shoppingList,
                onClick = { navController.navigate(Screen.ShoppingLists.route) }
            )
        }
        item {
            QuickInCards(
                Icons.Default.Money,
                title = stringResource(R.string.quick_Debts),
                color = debts,
                onClick = {  }
            )
        }
        item {
            QuickInCards(
                Icons.Default.Balance,
                title = stringResource(R.string.quick_Balance),
                color = balance,
                onClick = { onActionClick(3) }
            )
        }
        item {
            QuickInCards(
                Icons.Default.TrackChanges,
                title = stringResource(R.string.quick_Goals),
                color = goals,
                onClick = {navController.navigate(Screen.Goals.route)}
            )
        }
    }
}