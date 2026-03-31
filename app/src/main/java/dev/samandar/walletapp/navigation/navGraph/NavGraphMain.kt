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
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dev.samandar.walletapp.core.onBoarding.onboardingScreen.OnboardingScreen
import dev.samandar.walletapp.wallet.presentation.ui.home.NavBarActionButton.ModernBottomActions
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.AddTransactionBottomSheet
import dev.samandar.walletapp.wallet.presentation.viewmodel.AccountViewModel
import dev.samandar.walletapp.wallet.presentation.viewmodel.HomeViewModel
import dev.samandar.walletapp.wallet.presentation.ui.budjets.BudgetViewModel
import dev.samandar.walletapp.wallet.presentation.ui.charts.categoryStatistic.viewmodel.CategoryStatisticsViewModel
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.viewmodel.ExportViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavGraphMain(
    viewModel: HomeViewModel,
    addAccountViewModel: AccountViewModel,
    budgetViewModel: BudgetViewModel,
    categoryViewModel: CategoryStatisticsViewModel,
    startDestination: String,
    exportViewModel: ExportViewModel
) {
    val navController = rememberAnimatedNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route?.substringBefore("?")
    val listState = rememberLazyListState()
    var showAddSheet by remember { mutableStateOf(false) }

    val mainScreens = listOf(Screen.Home.route, Screen.ExpenseList.route, Screen.Budgets.route)

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedNavHost(
            navController = navController,
            startDestination = startDestination, // Dinamik startDestination,
            enterTransition = { ZoomInForward },
            exitTransition = { ZoomOutForward },
            popEnterTransition = { ZoomInBackward },
            popExitTransition = { ZoomOutBackward }
        ) {
            composable(Screen.Onboarding.route) {
                OnboardingScreen(
                    onFinish = {
                        navController.navigate(Screen.Splash.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }

            homeAndBudgetGraph(navController, budgetViewModel)
            featuresGraph(
                navController, viewModel, addAccountViewModel, categoryViewModel,
                export = exportViewModel
            )
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
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onFabClick = {
                        when (currentRoute) {
                            Screen.Home.route, Screen.ExpenseList.route -> {
                                showAddSheet = true
                            }
                            Screen.Budgets.route -> {
                                navController.navigate(Screen.budjetAdd.route)
                            }
                        }
                    }
                )
            }
        }
    }
}