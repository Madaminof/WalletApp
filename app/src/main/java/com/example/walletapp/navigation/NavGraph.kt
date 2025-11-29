package com.example.walletapp.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.composable
import com.example.walletapp.auth.presentation.AuthViewModel
import com.example.walletapp.auth.presentation.ui.LoginScreen
import com.example.walletapp.wallet.presentation.ui.Navigation
import com.example.walletapp.wallet.presentation.ui.account.AddAccountScreen
import com.example.walletapp.wallet.presentation.ui.account.WalletScreen
import com.example.walletapp.wallet.presentation.ui.budjets.AddBudjetScreen
import com.example.walletapp.wallet.presentation.ui.budjets.BudgetViewModel
import com.example.walletapp.wallet.presentation.ui.budjets.BudgetsScreen
import com.example.walletapp.wallet.presentation.ui.charts.ChartsScreen
import com.example.walletapp.wallet.presentation.ui.charts.ExpensesListScreen
import com.example.walletapp.wallet.presentation.ui.home.SplashScreen
import com.example.walletapp.wallet.presentation.ui.home.addTransaction.AddTransactionScreen
import com.example.walletapp.wallet.presentation.ui.home.addTransaction.addVoiceTransaction.AddvoiceScreen
import com.example.walletapp.wallet.presentation.ui.home.addTransaction.addversion1.AddTransactionScreenV1
import com.example.walletapp.wallet.presentation.ui.otherScreens.goals.GoalsScreen
import com.example.walletapp.wallet.presentation.ui.otherScreens.shoppingLists.ShoppingListScreen
import com.example.walletapp.wallet.presentation.viewmodel.AccountViewModel
import com.example.walletapp.wallet.presentation.viewmodel.AddTransactionViewModel
import com.example.walletapp.wallet.presentation.viewmodel.HomeViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

const val TRANSITION_DURATION = 400
const val QUICK_TRANSITION_DURATION = 250
const val MODAL_TRANSITION_DURATION = 750

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

    object Settings : Screen("settings")

    object Add : Screen("add")
    object AddVoice : Screen("add_voice")

    object budjetAdd : Screen("add_budjet")
    object addTransaction: Screen("add_transaction")
    object addAccound: Screen("add_accound")


}


val SlideInForward = slideInHorizontally(
    initialOffsetX = { fullWidth -> fullWidth },
    animationSpec = tween(TRANSITION_DURATION, easing = StandardEasing)
) + fadeIn(
    initialAlpha = 0f,
    animationSpec = tween(TRANSITION_DURATION)
)

val SlideOutForward = slideOutHorizontally(
    targetOffsetX = { fullWidth -> -fullWidth },
    animationSpec = tween(QUICK_TRANSITION_DURATION)
) + fadeOut(
    targetAlpha = 0f,
    animationSpec = tween(QUICK_TRANSITION_DURATION)
)

val SlideInBackward = slideInHorizontally(
    initialOffsetX = { fullWidth -> -fullWidth / 2 },
    animationSpec = tween(QUICK_TRANSITION_DURATION, easing = StandardEasing)
) + fadeIn(
    initialAlpha = 0f,
    animationSpec = tween(QUICK_TRANSITION_DURATION)
)

val SlideOutBackward = slideOutHorizontally(
    targetOffsetX = { fullWidth -> fullWidth },
    animationSpec = tween(QUICK_TRANSITION_DURATION)
) + fadeOut(
    targetAlpha = 0f,
    animationSpec = tween(QUICK_TRANSITION_DURATION)
)


val ModalEnterTransition = slideInVertically(
    initialOffsetY = { fullHeight -> fullHeight },
    animationSpec = tween(MODAL_TRANSITION_DURATION, easing = ModalEasing)
) + fadeIn(initialAlpha = 0f, animationSpec = tween(MODAL_TRANSITION_DURATION))

val ModalExitTransition = slideOutVertically(
    targetOffsetY = { fullHeight -> fullHeight },
    animationSpec = tween(MODAL_TRANSITION_DURATION, easing = ModalEasing)
) + fadeOut(targetAlpha = 0f, animationSpec = tween(MODAL_TRANSITION_DURATION))

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavGraph(
    onSignInClicked: () -> Unit,
    viewModel: HomeViewModel,
    viewModel1: AddTransactionViewModel,
    authViewModel: AuthViewModel,
    addAccountViewModel: AccountViewModel,
    budgetViewModel: BudgetViewModel,
) {
    val accounts by viewModel.accounts.collectAsState()
    val navController = rememberAnimatedNavController()
    val isUser by authViewModel.currentUser.collectAsState(initial = null)

    val modalRoutes = listOf(
        Screen.Add.route,
        Screen.budjetAdd.route,
        Screen.addTransaction.route,
        Screen.addAccound.route
    )

    LaunchedEffect(isUser) {
        if (isUser == null) {
            navController.navigate("login") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    AnimatedNavHost(
        navController = navController,
        startDestination = "splash",
        enterTransition = { fadeIn(tween(QUICK_TRANSITION_DURATION)) },
        exitTransition = { fadeOut(tween(QUICK_TRANSITION_DURATION)) },
        popEnterTransition = { fadeIn(tween(QUICK_TRANSITION_DURATION)) },
        popExitTransition = { fadeOut(tween(QUICK_TRANSITION_DURATION)) }
    ) {

        composable("splash") {
            SplashScreen(navController = navController, authViewModel = authViewModel)
        }
        composable("login") {
            LoginScreen(
                authViewModel = authViewModel,
                navController = navController,
                onLoginClicked = onSignInClicked
            )
        }
        composable(
            Screen.Home.route,
            enterTransition = { SlideInForward },
            popExitTransition = { SlideOutBackward },
            exitTransition = {
                val targetRoute = targetState.destination.route
                if (targetRoute in modalRoutes) {
                    ModalExitTransition
                } else {
                    SlideOutForward
                }
            },
            popEnterTransition = {
                if (initialState.destination.route in modalRoutes) {
                    fadeIn(
                        animationSpec = tween(MODAL_TRANSITION_DURATION)
                    )
                } else {
                    SlideInBackward
                }
            }
        ) {
            val liveUser by authViewModel.currentUser.collectAsState(initial = null)
            Navigation(
                user = liveUser,
                navController = navController,
                viewModel = viewModel,
                accountViewModel = addAccountViewModel,
                budgetViewModel = budgetViewModel
            )
        }
        val slideScreens = listOf(
            Screen.Charts.route, Screen.Budgets.route, Screen.Wallet.route,
            Screen.ShoppingLists.route, Screen.Goals.route, Screen.ExpenseList.route,
            Screen.Category.route
        )

        slideScreens.forEach { route ->
            composable(
                route = route,
                enterTransition = { SlideInForward },
                exitTransition = { SlideOutForward },
                popEnterTransition = { SlideInBackward },
                popExitTransition = { SlideOutBackward }
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
                }
            }
        }
        composable(
            route = Screen.Add.route,
            enterTransition = { ModalEnterTransition },
            exitTransition = { ModalExitTransition }
        ) {
            AddTransactionScreen(
                navController = navController,
                viewModel = viewModel1
            )
        }
        composable(
            route = Screen.budjetAdd.route,
            enterTransition = { ModalEnterTransition },
            exitTransition = { ModalExitTransition }
        ) {
            AddBudjetScreen(navController = navController)
        }
        composable(
            route = Screen.addTransaction.route,
            enterTransition = { ModalEnterTransition },
            exitTransition = { ModalExitTransition }
        ) {
            AddTransactionScreenV1(navController = navController)
        }
        composable(
            route = Screen.AddVoice.route,
            enterTransition = { ModalEnterTransition },
            exitTransition = { ModalExitTransition }
        ) {
            AddvoiceScreen(navController = navController)
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
                },
                existingAccounts = accounts
            )
        }
    }
}