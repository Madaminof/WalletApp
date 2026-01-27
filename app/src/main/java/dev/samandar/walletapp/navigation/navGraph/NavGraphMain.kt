package dev.samandar.walletapp.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dev.samandar.walletapp.wallet.presentation.ui.home.NavBarActionButton.ModernBottomActions
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.AddTransactionBottomSheet
import dev.samandar.walletapp.wallet.presentation.viewmodel.AccountViewModel
import dev.samandar.walletapp.wallet.presentation.viewmodel.HomeViewModel
import dev.samandar.walletapp.wallet.presentation.ui.budjets.BudgetViewModel
import dev.samandar.walletapp.wallet.presentation.ui.charts.categoryStatistic.viewmodel.CategoryStatisticsViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavGraphMain(
    viewModel: HomeViewModel,
    addAccountViewModel: AccountViewModel,
    budgetViewModel: BudgetViewModel,
    categoryViewModel: CategoryStatisticsViewModel
) {
    val navController = rememberAnimatedNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val listState = rememberLazyListState()
    var showAddSheet by remember { mutableStateOf(false) }

    val mainScreens = listOf(Screen.Home.route, Screen.ExpenseList.route, Screen.Budgets.route)

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedNavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            enterTransition = { ZoomInForward },
            exitTransition = { ZoomOutForward },
            popEnterTransition = { ZoomInBackward },
            popExitTransition = { ZoomOutBackward }
        ) {
            homeAndBudgetGraph(navController, budgetViewModel)
            featuresGraph(navController, viewModel, addAccountViewModel, categoryViewModel)
            scannerGraph(navController)
        }

        if (showAddSheet) {
            AddTransactionBottomSheet(
                onDismiss = { showAddSheet = false },
                onManualInput = { navController.navigate(Screen.addTransaction.route) },
                onQrScan = { navController.navigate(Screen.SCANNER.route) }
            )
        }

        if (currentRoute in mainScreens) {
            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                ModernBottomActions(
                    currentRoute = currentRoute,
                    listState = listState,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onFabClick = {
                        if (currentRoute == Screen.Home.route) showAddSheet = true
                        else if (currentRoute == Screen.Budgets.route) navController.navigate(Screen.budjetAdd.route)
                    }
                )
            }
        }
    }
}