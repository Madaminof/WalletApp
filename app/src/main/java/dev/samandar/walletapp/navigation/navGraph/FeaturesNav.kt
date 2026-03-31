package dev.samandar.walletapp.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import com.google.accompanist.navigation.animation.composable
import androidx.navigation.navArgument
import dev.samandar.walletapp.wallet.domain.model.TransactionType
import dev.samandar.walletapp.wallet.domain.model.debt.DebtType
import dev.samandar.walletapp.wallet.presentation.ui.account.EditAccountScreen
import dev.samandar.walletapp.wallet.presentation.ui.account.accountScreen.WalletScreen
import dev.samandar.walletapp.wallet.presentation.ui.account.addAccount.AddAccountScreen
import dev.samandar.walletapp.wallet.presentation.ui.charts.categoryStatistic.CategoryDetail.CategoryDetailScreen
import dev.samandar.walletapp.wallet.presentation.ui.charts.categoryStatistic.CategoryStatisticsScreen
import dev.samandar.walletapp.wallet.presentation.ui.charts.categoryStatistic.viewmodel.CategoryStatisticsViewModel
import dev.samandar.walletapp.wallet.presentation.ui.charts.detailScreen.TransactionDetailScreen
import dev.samandar.walletapp.wallet.presentation.ui.charts.historyTransactions.HistorytransactionScreen
import dev.samandar.walletapp.wallet.presentation.ui.features.debts.DebtsViewModel
import dev.samandar.walletapp.wallet.presentation.ui.features.debts.addDebt.AddEditDebtScreen
import dev.samandar.walletapp.wallet.presentation.ui.features.debts.debtScreen.DebtsScreen
import dev.samandar.walletapp.wallet.presentation.ui.features.goals.GoalsScreen
import dev.samandar.walletapp.wallet.presentation.ui.features.shoppingLists.shoppingListDetail.ShoppingListDetailScreen
import dev.samandar.walletapp.wallet.presentation.ui.features.shoppingLists.shoppingListScreen.ShoppingListScreen
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.presentation.view.SplitBillScreen
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.presentation.view.listScreen.SplitBillListScreen
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.AddTransactionScreenPremium
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.viewmodel.ExportViewModel
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.SettingsScreen
import dev.samandar.walletapp.wallet.presentation.viewmodel.AccountViewModel
import dev.samandar.walletapp.wallet.presentation.viewmodel.HomeViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.featuresGraph(
    navController: NavHostController,
    viewModel: HomeViewModel,
    addAccountViewModel: AccountViewModel,
    categoryViewModel: CategoryStatisticsViewModel,
    export: ExportViewModel
) {
    val zoomScreens = listOf(
        Screen.Charts.route,
        Screen.Wallet.route,
        Screen.ShoppingLists.route,
        Screen.Goals.route,
        Screen.Category.route,
        Screen.addTransaction.route,
        Screen.SplitBillList.route
    )

    zoomScreens.forEach { route ->
        composable(route = route) {
            val accounts by viewModel.accounts.collectAsState()
            when (route) {
                Screen.Wallet.route -> WalletScreen(
                    accounts = accounts,
                    navController = navController,
                    accountViewModel = addAccountViewModel
                )
                Screen.ShoppingLists.route -> ShoppingListScreen(navController = navController)
                Screen.Goals.route -> GoalsScreen(navController)
                Screen.SplitBillList.route -> SplitBillListScreen(
                    onAddNewSplit = {
                        navController.navigate(Screen.SplitBill.createRoute())
                    },
                    onViewDetail = { billId ->
                        navController.navigate(Screen.SplitBill.createRoute(billId))
                    },
                    navController = navController,
                )
                else -> {}
            }
        }
    }

    // zoomScreens ichidan Screen.SplitBill.route ni olib tashlang

// Keyin NavHost ichida alohida e'lon qiling:
    composable(
        route = Screen.SplitBill.route + "?billId={billId}", // billId ixtiyoriy (optional)
        arguments = listOf(
            navArgument("billId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { backStackEntry ->
        val billId = backStackEntry.arguments?.getString("billId")

        SplitBillScreen(
            billId = billId,
            onBack = { navController.popBackStack() }
        )
    }

    composable(
        route = Screen.ExpenseList.route + "?accountId={accountId}",
        arguments = listOf(
            navArgument("accountId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            }
        )
    ) { backStackEntry ->
        val accountId = backStackEntry.arguments?.getString("accountId")
        HistorytransactionScreen(
            navController = navController,
            accountId = accountId
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
            },
            navController = navController
        )
    }

    // 2. Account Management (SHU YERDA CRASH BERAYOTGAN EDI)
    composable(
        route = Screen.addAccound.route + "/{accountType}",
        enterTransition = { ModalEnterTransition },
        exitTransition = { ModalExitTransition },
        arguments = listOf(
            navArgument("accountType") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val accountType = backStackEntry.arguments?.getString("accountType") ?: "CARD"
        val accounts by viewModel.accounts.collectAsState()

        AddAccountScreen(
            navController = navController,
            accountType = accountType,
            onSave = { account ->
                addAccountViewModel.addAccount(account)
            },
            existingAccounts = accounts
        )
    }

    composable(
        route = Screen.editAccount.route + "/{accountId}",
        arguments = listOf(navArgument("accountId") { type = NavType.StringType })
    ) { backStackEntry ->
        val accountId = backStackEntry.arguments?.getString("accountId")
        val state by addAccountViewModel.uiState.collectAsState()
        val accountToEdit = state.accounts.find { it.id == accountId }

        accountToEdit?.let { account ->
            EditAccountScreen(
                account = account,
                onSaveClick = { updatedAccount ->
                    addAccountViewModel.updateAccount(updatedAccount)
                    navController.popBackStack()
                },
                navController = navController,
            )
        }
    }

    // 3. Transactions Detail
    composable(
        route = "${Screen.detailTransaction.route}/{transactionId}"
    ) { backStackEntry ->
        val transactionId = backStackEntry.arguments?.getString("transactionId")
        val transactions by viewModel.transactions.collectAsState()
        val transaction = transactions.find { it.id == transactionId }

        transaction?.let { it ->
            TransactionDetailScreen(
                transaction = it,
                onBack = { navController.popBackStack() },
                onEdit = { updated -> viewModel.updateTransaction(updated) },
                onDelete = {
                    // 'it.id' o'rniga butun 'it' obyektini yuboramiz
                    viewModel.deleteTransaction(it)
                    navController.popBackStack()
                }
            )
        }
    }

    // 4. Shopping Details
    composable(
        route = "${Screen.ShoppingDetail.route}/{listId}",
        arguments = listOf(navArgument("listId") { type = NavType.StringType })
    ) { backStackEntry ->
        val listId = backStackEntry.arguments?.getString("listId") ?: ""
        ShoppingListDetailScreen(listId = listId, navController = navController)
    }

    // 5. Category & Statistics
    composable(Screen.CategoryStatisticsScreen.route) {
        CategoryStatisticsScreen(navController = navController, categoryViewModel = categoryViewModel)
    }

    composable(
        route = Screen.CategoryDetail.route,
        arguments = listOf(
            navArgument("categoryName") { type = NavType.StringType },
            navArgument("transactionType") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
        val typeStr = backStackEntry.arguments?.getString("transactionType") ?: "EXPENSE"
        val type = try { TransactionType.valueOf(typeStr) } catch (e: Exception) { TransactionType.EXPENSE }

        CategoryDetailScreen(categoryName = categoryName, transactionType = type, navController = navController)
    }

    // 6. Debts (Full logic)
    composable(Screen.DebtsScreen.route) { backStackEntry ->
        val debtVM: DebtsViewModel = hiltViewModel(backStackEntry)
        DebtsScreen(navController = navController, viewModel = debtVM)
    }

    composable(
        route = Screen.AddEditDebt.route + "?debtId={debtId}&debtType={debtType}",
        enterTransition = { ModalEnterTransition },
        exitTransition = { ModalExitTransition },
        arguments = listOf(
            navArgument("debtId") { type = NavType.StringType; nullable = true; defaultValue = null },
            navArgument("debtType") { type = NavType.StringType; nullable = true; defaultValue = null }
        )
    ) { backStackEntry ->
        val debtId = backStackEntry.arguments?.getString("debtId")
        val debtTypeStr = backStackEntry.arguments?.getString("debtType")
        val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(Screen.DebtsScreen.route) }
        val debtVM: DebtsViewModel = hiltViewModel(parentEntry)
        val uiState by debtVM.state.collectAsState()

        val debtToEdit = remember(debtId, uiState.debts) { if (debtId != null) uiState.debts.find { it.id == debtId } else null }
        val initialType = remember(debtTypeStr, debtToEdit) {
            debtToEdit?.type ?: debtTypeStr?.let { runCatching { DebtType.valueOf(it) }.getOrNull() } ?: DebtType.LENT
        }

        AddEditDebtScreen(navController = navController, viewModel = debtVM, initialDebt = debtToEdit, initialType = initialType)
    }

    // 7. Settings
    composable(Screen.SettingScreen.route) { SettingsScreen(
        navController,
        viewModel = export
    ) }
}