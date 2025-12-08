package com.example.walletapp.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.navigation.compose.composable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.walletapp.auth.presentation.AuthViewModel
import com.example.walletapp.wallet.presentation.ui.Navigation
import com.example.walletapp.wallet.presentation.ui.account.AddAccountScreen
import com.example.walletapp.wallet.presentation.ui.account.WalletScreen
import com.example.walletapp.wallet.presentation.ui.budjets.AddBudjetScreen
import com.example.walletapp.wallet.presentation.ui.budjets.BudgetViewModel
import com.example.walletapp.wallet.presentation.ui.budjets.BudgetsScreen
import com.example.walletapp.wallet.presentation.ui.charts.ChartsScreen
import com.example.walletapp.wallet.presentation.ui.charts.ExpensesListScreen
import com.example.walletapp.wallet.presentation.ui.home.SplashScreen
import com.example.walletapp.wallet.presentation.ui.home.addTransaction.addtransactionScreen2.AddTransactionBottomSheet
import com.example.walletapp.wallet.presentation.ui.otherScreens.debts.DebtsScreen
import com.example.walletapp.wallet.presentation.ui.otherScreens.goals.GoalsScreen
import com.example.walletapp.wallet.presentation.ui.otherScreens.shoppingLists.ShoppingListDetailScreen
import com.example.walletapp.wallet.presentation.ui.otherScreens.shoppingLists.ShoppingListScreen
import com.example.walletapp.wallet.presentation.viewmodel.AccountViewModel
import com.example.walletapp.wallet.presentation.viewmodel.AddTransactionViewModel
import com.example.walletapp.wallet.presentation.viewmodel.DebtsViewModel
import com.example.walletapp.wallet.presentation.viewmodel.HomeViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

const val TRANSITION_DURATION = 350
const val QUICK_TRANSITION_DURATION = 250
const val MODAL_TRANSITION_DURATION = 400

val StandardEasing = FastOutSlowInEasing
val ModalEasing = FastOutSlowInEasing

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Charts : Screen("charts")
    object Wallet : Screen("wallet")
    object Category : Screen("categories")
    object Budgets : Screen("budgets")
    object ShoppingLists : Screen("shopping_lists")
    object Goals : Screen("goals")
    object ExpenseList: Screen("expense_list")

    object Add : Screen("add")
    object budjetAdd : Screen("add_budjet")
    object addTransaction: Screen("add_transaction")
    object addAccound: Screen("add_accound")

    object ShoppingDetail : Screen("shopDetail")
    object DebtsScreen: Screen("debts_screen")

}


val ZoomInForward: EnterTransition = scaleIn(
    initialScale = 0.8f,
    animationSpec = tween(TRANSITION_DURATION, easing = StandardEasing)
) + fadeIn(
    initialAlpha = 0.5f,
    animationSpec = tween(TRANSITION_DURATION)
)

val ZoomOutForward: ExitTransition = scaleOut(
    targetScale = 0.8f,
    animationSpec = tween(QUICK_TRANSITION_DURATION)
) + fadeOut(
    targetAlpha = 0f,
    animationSpec = tween(QUICK_TRANSITION_DURATION)
)

val ZoomInBackward: EnterTransition = scaleIn(
    initialScale = 0.95f,
    animationSpec = tween(QUICK_TRANSITION_DURATION, easing = StandardEasing)
) + fadeIn(
    initialAlpha = 0.5f,
    animationSpec = tween(QUICK_TRANSITION_DURATION)
)

val ZoomOutBackward: ExitTransition = scaleOut(
    targetScale = 1.05f,
    animationSpec = tween(QUICK_TRANSITION_DURATION)
) + fadeOut(
    targetAlpha = 0f,
    animationSpec = tween(QUICK_TRANSITION_DURATION)
)


val ModalEnterTransition: EnterTransition = slideInVertically(
    initialOffsetY = { fullHeight -> fullHeight },
    animationSpec = tween(MODAL_TRANSITION_DURATION, easing = ModalEasing)
) + fadeIn(initialAlpha = 0f, animationSpec = tween(MODAL_TRANSITION_DURATION))

val ModalExitTransition: ExitTransition = slideOutVertically(
    targetOffsetY = { fullHeight -> fullHeight },
    animationSpec = tween(MODAL_TRANSITION_DURATION, easing = ModalEasing)
) + fadeOut(targetAlpha = 0f, animationSpec = tween(MODAL_TRANSITION_DURATION))


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavGraph(
    viewModel: HomeViewModel,
    viewModel1: AddTransactionViewModel,
    addAccountViewModel: AccountViewModel,
    budgetViewModel: BudgetViewModel,
    debtsViewModel: DebtsViewModel
) {
    val accounts by viewModel.accounts.collectAsState()
    val navController = rememberAnimatedNavController()

    val modalRoutes = listOf(
        Screen.Add.route,
        Screen.budjetAdd.route,
        Screen.addTransaction.route,
        Screen.addAccound.route
    )

    AnimatedNavHost(
        navController = navController,
        startDestination = "splash",
        enterTransition = { fadeIn(tween(QUICK_TRANSITION_DURATION)) },
        exitTransition = { fadeOut(tween(QUICK_TRANSITION_DURATION)) },
        popEnterTransition = { fadeIn(tween(QUICK_TRANSITION_DURATION)) },
        popExitTransition = { fadeOut(tween(QUICK_TRANSITION_DURATION)) }
    ) {

        composable("splash") {
            SplashScreen(navController = navController)
        }

        composable(
            Screen.Home.route,
            enterTransition = { ZoomInForward },
            popExitTransition = { ZoomOutBackward },
            exitTransition = {
                val targetRoute = targetState.destination.route
                if (targetRoute in modalRoutes) {
                    ModalExitTransition
                } else {
                    ZoomOutForward
                }
            },
            popEnterTransition = {
                if (initialState.destination.route in modalRoutes) {
                    fadeIn(
                        animationSpec = tween(MODAL_TRANSITION_DURATION)
                    )
                } else {
                    ZoomInBackward
                }
            }
        ) {
            Navigation(
                navController = navController,
                addTransactionViewModel = viewModel1,
            )
        }
        val zoomScreens = listOf(
            Screen.Charts.route, Screen.Budgets.route, Screen.Wallet.route,
            Screen.ShoppingLists.route, Screen.Goals.route, Screen.ExpenseList.route,
            Screen.Category.route
        )

        zoomScreens.forEach { route ->
            composable(
                route = route,
                enterTransition = { ZoomInForward },
                exitTransition = { ZoomOutForward },
                popEnterTransition = { ZoomInBackward },
                popExitTransition = { ZoomOutBackward }
            ) { backStackEntry ->
                when (backStackEntry.destination.route) {
                    Screen.Charts.route -> ChartsScreen(viewModel = viewModel, navController = navController)
                    Screen.Budgets.route -> BudgetsScreen(
                        viewModel = budgetViewModel,
                        navController = navController
                    )
                    Screen.Wallet.route -> WalletScreen(accounts = accounts, navController = navController, accountViewModel = addAccountViewModel)
                    Screen.ShoppingLists.route -> ShoppingListScreen(navController = navController)
                    Screen.Goals.route -> GoalsScreen(navController)
                    Screen.ExpenseList.route -> ExpensesListScreen(navController = navController)
                    else -> {}
                }
            }
        }
        composable(
            route = Screen.budjetAdd.route,
            enterTransition = { ModalEnterTransition },
            exitTransition = { ModalExitTransition }
        ) {
            AddBudjetScreen(
                navController = navController,
            )
        }

        composable(
            route = Screen.addTransaction.route,
            enterTransition = { ModalEnterTransition },
            exitTransition = { ModalExitTransition }
        ) {
            AddTransactionBottomSheet(
                viewModel = viewModel1,
                onClose = { navController.popBackStack() },
            )
        }

        composable(
            route = Screen.addAccound.route,
            enterTransition = { ModalEnterTransition },
            exitTransition = { ModalExitTransition }
        ) {
            AddAccountScreen(
                navController = navController,
                onSave = { account ->
                    addAccountViewModel.addAccount(account)
                    navController.popBackStack()
                },
                existingAccounts = accounts
            )
        }

        composable(
            route = "${Screen.ShoppingDetail.route}/{listId}",
            arguments = listOf(
                navArgument("listId") {
                    type = NavType.StringType
                }
            ),
            enterTransition = { ZoomInForward },
            exitTransition = { ZoomOutForward },
            popEnterTransition = { ZoomInBackward },
            popExitTransition = { ZoomOutBackward }
        ) { backStackEntry ->
            val listId = backStackEntry.arguments?.getString("listId") ?: ""
            ShoppingListDetailScreen(
                listId = listId,
                navController = navController
            )
        }


        composable(
            route = Screen.DebtsScreen.route,

            enterTransition = { ZoomInForward },
            exitTransition = { ZoomOutForward },
            popEnterTransition = { ZoomInBackward },
            popExitTransition = { ZoomOutBackward }
        ) { backStackEntry ->
            DebtsScreen(
                navController = navController,
                viewModel = debtsViewModel
            )
        }
    }
}