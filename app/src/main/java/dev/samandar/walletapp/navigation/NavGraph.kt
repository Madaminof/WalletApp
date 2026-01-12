package dev.samandar.walletapp.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dev.samandar.walletapp.wallet.domain.model.debt.DebtType
import dev.samandar.walletapp.wallet.presentation.ui.Navigation
import dev.samandar.walletapp.wallet.presentation.ui.account.AddAccountScreen
import dev.samandar.walletapp.wallet.presentation.ui.account.accountScreen.WalletScreen
import dev.samandar.walletapp.wallet.presentation.ui.budjets.BudgetViewModel
import dev.samandar.walletapp.wallet.presentation.ui.budjets.budgetScreen.BudgetsScreen
import dev.samandar.walletapp.wallet.presentation.ui.budjets.addBudget.AddBudgetScreen
import dev.samandar.walletapp.wallet.presentation.ui.budjets.budgetDetail.BudgetDetailScreen
import dev.samandar.walletapp.wallet.presentation.ui.charts.ChartsScreen
import dev.samandar.walletapp.wallet.presentation.ui.charts.ExpensesListScreen
import dev.samandar.walletapp.wallet.presentation.ui.charts.detailScreen.TransactionDetailScreen
import dev.samandar.walletapp.wallet.presentation.ui.home.SplashScreen
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.AddTransactionScreenPremium
import dev.samandar.walletapp.wallet.presentation.ui.premiumFunctions.debts.debtScreen.DebtsScreen
import dev.samandar.walletapp.wallet.presentation.ui.premiumFunctions.goals.GoalsScreen
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.SettingsScreen
import dev.samandar.walletapp.wallet.presentation.ui.premiumFunctions.shoppingLists.ShoppingListDetailScreen
import dev.samandar.walletapp.wallet.presentation.ui.premiumFunctions.shoppingLists.ShoppingListScreen
import dev.samandar.walletapp.wallet.presentation.viewmodel.AccountViewModel
import dev.samandar.walletapp.wallet.presentation.viewmodel.AddTransactionViewModel
import dev.samandar.walletapp.wallet.presentation.ui.premiumFunctions.debts.DebtsViewModel
import dev.samandar.walletapp.wallet.presentation.ui.premiumFunctions.debts.addDebt.AddEditDebtScreen
import dev.samandar.walletapp.wallet.presentation.viewmodel.HomeViewModel

const val TRANSITION_DURATION = 350
const val QUICK_TRANSITION_DURATION = 350
const val MODAL_TRANSITION_DURATION = 450

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
    object ADD_EDIT_DEBT_SCREEN:Screen("add_edit_debt?debtId={debtId}")

    object detailTransaction : Screen("detail")
    object BudgetDetail : Screen("budget_detail/{budgetId}") {
        fun createRoute(budgetId: Int) = "budget_detail/$budgetId"
    }
    object DebtDetail : Screen("debt_detail/{debtId}") {
        fun createRoute(debtId: String) = "debt_detail/$debtId"
    }

    object ShoppingDetail : Screen("shopDetail")
    object DebtsScreen: Screen("debts_screen")
    object SettingScreen: Screen("settings")

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
            AddBudgetScreen(
                navController = navController,
            )
        }

        composable(
            route = Screen.addTransaction.route,
            enterTransition = { ModalEnterTransition },
            exitTransition = { ModalExitTransition }
        ) {
            AddTransactionScreenPremium(
                onSuccess = { message ->
                    navController.previousBackStackEntry?.savedStateHandle?.set("success_key", message)
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "${Screen.detailTransaction.route}/{transactionId}",
            enterTransition = { ZoomInForward },
            exitTransition = { ZoomOutForward },
            popEnterTransition = { ZoomInBackward },
            popExitTransition = { ZoomOutBackward }
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getString("transactionId")
            val transactions by viewModel.transactions.collectAsState()
            val transaction = transactions.find { it.id == transactionId }

            if (transaction != null) {
                TransactionDetailScreen(
                    transaction = transaction,
                    onBack = { navController.popBackStack() },
                    onEdit = { updated -> viewModel.updateTransaction(updated) },
                    onDelete = {
                        viewModel.deleteTransaction(it.id)
                        navController.popBackStack()
                    },
                )
            }
        }
        composable(
            route = Screen.BudgetDetail.route,
            arguments = listOf(navArgument("budgetId") { type = NavType.StringType }),
            enterTransition = { ZoomInForward },
            exitTransition = { ZoomOutForward },
            popEnterTransition = { ZoomInBackward },
            popExitTransition = { ZoomOutBackward }
        ) { backStackEntry ->
            val budgetId = backStackEntry.arguments?.getString("budgetId") ?: ""
            val budgetStatus by budgetViewModel.getBudgetStatusById(budgetId)
                .collectAsState(initial = null)
            budgetStatus?.let { status ->
                BudgetDetailScreen(
                    budgetStatus = status,
                    onBack = { navController.popBackStack() },
                    onDelete = {
                        budgetViewModel.deleteBudjet(status.budget)
                        navController.popBackStack()
                    },
                    viewModel = budgetViewModel,
                )
            } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
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
            val debtViewmodel: DebtsViewModel = hiltViewModel(backStackEntry)
            DebtsScreen(
                navController = navController,
                viewModel = debtViewmodel
            )
        }
        composable(
            route = Screen.ADD_EDIT_DEBT_SCREEN.route + "?debtId={debtId}&debtType={debtType}",
            enterTransition = { ModalEnterTransition },
            exitTransition = { ModalExitTransition },
            arguments = listOf(
                navArgument("debtId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("debtType") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val debtId = backStackEntry.arguments?.getString("debtId")
            val debtTypeStr = backStackEntry.arguments?.getString("debtType")

            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.DebtsScreen.route)
            }
            val debtViewModel: DebtsViewModel = hiltViewModel(parentEntry)
            val uiState by debtViewModel.state.collectAsState()

            val debtToEdit = remember(debtId, uiState.debts) {
                if (debtId != null) uiState.debts.find { it.id == debtId } else null
            }
            val initialType = remember(debtTypeStr, debtToEdit) {
                debtToEdit?.type ?: debtTypeStr?.let {
                    runCatching { DebtType.valueOf(it) }.getOrNull()
                } ?: DebtType.LENT
            }

            AddEditDebtScreen(
                navController = navController,
                viewModel = debtViewModel,
                initialDebt = debtToEdit,
                initialType = initialType
            )
        }

        composable(
            route = Screen.SettingScreen.route,

            enterTransition = { ZoomInForward },
            exitTransition = { ZoomOutForward },
            popEnterTransition = { ZoomInBackward },
            popExitTransition = { ZoomOutBackward }
        ) { backStackEntry ->
            SettingsScreen(navController)
        }

    }
}