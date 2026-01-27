package dev.samandar.walletapp.navigation

import android.Manifest
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.composable
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import dev.samandar.walletapp.wallet.domain.model.debt.DebtType
import dev.samandar.walletapp.wallet.presentation.ui.Navigation
import dev.samandar.walletapp.wallet.presentation.ui.account.addAccount.AddAccountScreen
import dev.samandar.walletapp.wallet.presentation.ui.account.accountScreen.WalletScreen
import dev.samandar.walletapp.wallet.presentation.ui.budjets.BudgetViewModel
import dev.samandar.walletapp.wallet.presentation.ui.budjets.budgetScreen.BudgetsScreen
import dev.samandar.walletapp.wallet.presentation.ui.budjets.addBudget.AddBudgetScreen
import dev.samandar.walletapp.wallet.presentation.ui.budjets.budgetDetail.BudgetDetailScreen
import dev.samandar.walletapp.wallet.presentation.ui.charts.ExpensesListScreen
import dev.samandar.walletapp.wallet.presentation.ui.charts.detailScreen.TransactionDetailScreen
import dev.samandar.walletapp.wallet.presentation.ui.home.NavBarActionButton.ModernBottomActions
import dev.samandar.walletapp.wallet.presentation.ui.home.SplashScreen
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.AddTransactionScreenPremium
import dev.samandar.walletapp.wallet.presentation.ui.features.debts.debtScreen.DebtsScreen
import dev.samandar.walletapp.wallet.presentation.ui.features.goals.GoalsScreen
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.SettingsScreen
import dev.samandar.walletapp.wallet.presentation.ui.features.shoppingLists.shoppingListDetail.ShoppingListDetailScreen
import dev.samandar.walletapp.wallet.presentation.ui.features.shoppingLists.shoppingListScreen.ShoppingListScreen
import dev.samandar.walletapp.wallet.presentation.viewmodel.AccountViewModel
import dev.samandar.walletapp.wallet.presentation.ui.features.debts.DebtsViewModel
import dev.samandar.walletapp.wallet.presentation.ui.features.debts.addDebt.AddEditDebtScreen
import dev.samandar.walletapp.wallet.presentation.viewmodel.HomeViewModel
import dev.samandar.walletapp.wallet.smartScann.scannScreen.ReceiptScannerScreen
import dev.samandar.walletapp.wallet.smartScann.ReviewViewModel
import dev.samandar.walletapp.wallet.smartScann.scanReviewScreen.ScanReviewScreen
import dev.samandar.walletapp.wallet.smartScann.ScannerViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.shouldShowRationale
import dev.samandar.walletapp.wallet.domain.model.TransactionType
import dev.samandar.walletapp.wallet.presentation.ui.account.EditAccountScreen
import dev.samandar.walletapp.wallet.presentation.ui.charts.categoryStatistic.CategoryDetail.CategoryDetailScreen
import dev.samandar.walletapp.wallet.presentation.ui.charts.categoryStatistic.CategoryStatisticsScreen
import dev.samandar.walletapp.wallet.presentation.ui.charts.categoryStatistic.viewmodel.CategoryStatisticsViewModel
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.AddTransactionBottomSheet
import dev.samandar.walletapp.wallet.smartScann.PermissionRationaleUI

/*

const val TRANSITION_DURATION = 360
const val MODAL_TRANSITION_DURATION = 400

val StandardEasing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)

val ZoomInForward: EnterTransition = scaleIn(
    initialScale = 0.9f,
    animationSpec = tween(TRANSITION_DURATION, easing = StandardEasing)
) + fadeIn(
    animationSpec = tween(TRANSITION_DURATION)
)

val ZoomOutForward: ExitTransition = scaleOut(
    targetScale = 0.9f,
    animationSpec = tween(TRANSITION_DURATION, easing = StandardEasing)
) + fadeOut(
    animationSpec = tween(150)
)

val ZoomInBackward: EnterTransition = scaleIn(
    initialScale = 1.05f,
    animationSpec = tween(TRANSITION_DURATION, easing = StandardEasing)
) + fadeIn(
    animationSpec = tween(TRANSITION_DURATION)
)

val ZoomOutBackward: ExitTransition = scaleOut(
    targetScale = 1.05f,
    animationSpec = tween(TRANSITION_DURATION, easing = StandardEasing)
) + fadeOut(
    animationSpec = tween(150)
)

val ModalEnterTransition = slideInVertically(
    initialOffsetY = { it },
    animationSpec = tween(MODAL_TRANSITION_DURATION, easing = StandardEasing)
)

val ModalExitTransition = slideOutVertically(
    targetOffsetY = { it },
    animationSpec = tween(MODAL_TRANSITION_DURATION, easing = StandardEasing)
)

*/




@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavGraph(
    viewModel: HomeViewModel,
    addAccountViewModel: AccountViewModel,
    budgetViewModel: BudgetViewModel,
    categoryViewModel: CategoryStatisticsViewModel

    ) {
    val accounts by viewModel.accounts.collectAsState()
    val navController = rememberAnimatedNavController()

    val listState = rememberLazyListState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route


    val mainScreens = listOf(
        Screen.Home.route,
        Screen.ExpenseList.route,
        Screen.Budgets.route
    )


    val modalRoutes = listOf(
        Screen.Add.route,
        Screen.budjetAdd.route,
        Screen.addTransaction.route,
        Screen.addAccound.route
    )

    Box(modifier = Modifier.fillMaxSize()) {

        AnimatedNavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            enterTransition = { ZoomInForward },
            exitTransition = { ZoomOutForward },
            popEnterTransition = { ZoomInBackward },
            popExitTransition = { ZoomOutBackward }
        ){

            composable(Screen.Splash.route) {
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
                route = Screen.CategoryStatisticsScreen.route,
                enterTransition = { ZoomInForward },
                exitTransition = { ZoomOutForward },
                popEnterTransition = { ZoomInBackward },
                popExitTransition = { ZoomOutBackward }
            ) { backStackEntry ->
                CategoryStatisticsScreen(
                    navController = navController,
                    categoryViewModel = categoryViewModel
                )
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

                // Stringdan Enumga o'tkazamiz
                val type = try {
                    TransactionType.valueOf(typeStr)
                } catch (e: Exception) {
                    TransactionType.EXPENSE
                }

                CategoryDetailScreen(
                    categoryName = categoryName,
                    transactionType = type,
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
                route = Screen.AddEditDebt.route + "?debtId={debtId}&debtType={debtType}",
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

            composable(
                route = Screen.editAccount.route + "/{accountId}",
                arguments = listOf(navArgument("accountId") { type = NavType.StringType }) // UUID bo'lgani uchun StringType qilamiz
            ) { backStackEntry ->
                val accountId = backStackEntry.arguments?.getString("accountId")
                val state by addAccountViewModel.cardState.collectAsState()

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



            @OptIn(ExperimentalPermissionsApi::class)
            composable(Screen.SCANNER.route) {
                val context = LocalContext.current as ComponentActivity
                val scannerViewModel: ScannerViewModel = hiltViewModel()
                val reviewViewModel: ReviewViewModel = hiltViewModel(context)
                val uiState by scannerViewModel.uiState.collectAsState()
                val cameraPermissionState = rememberPermissionState(
                    Manifest.permission.CAMERA
                )
                LaunchedEffect(Unit) {
                    if (!cameraPermissionState.status.isGranted) {
                        cameraPermissionState.launchPermissionRequest()
                    }
                }
                LaunchedEffect(uiState.scanResult) {
                    uiState.scanResult?.let { receipt ->
                        reviewViewModel.setScannedReceipt(receipt)
                        navController.navigate(Screen.REVIEW.route) {
                            popUpTo(Screen.SCANNER.route) { inclusive = true }
                        }
                    }
                }
                when {
                    cameraPermissionState.status.isGranted -> {
                        ReceiptScannerScreen(
                            viewModel = scannerViewModel,
                            onClose = { navController.popBackStack() }
                        )
                    }
                    cameraPermissionState.status.shouldShowRationale || !cameraPermissionState.status.isGranted -> {
                        PermissionRationaleUI(
                            onRequestPermission = { cameraPermissionState.launchPermissionRequest() }
                        )
                    }
                }
            }
            composable(Screen.REVIEW.route) {
                val context = LocalContext.current as ComponentActivity
                val reviewViewModel: ReviewViewModel = hiltViewModel(context)
                val state by reviewViewModel.uiState.collectAsState()
                state.receipt?.let { receipt ->
                    ScanReviewScreen(
                        state = state,
                        onConfirmed = {
                            reviewViewModel.saveFinalReceipt {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Home.route) { inclusive = true }
                                }
                            }
                        },
                        viewModel = reviewViewModel
                    )
                } ?: run {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(strokeWidth = 3.dp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Ma'lumotlar yuklanmoqda...", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }



        }






        var showAddSheet by remember { mutableStateOf(false) }
        if (showAddSheet) {
            AddTransactionBottomSheet(
                onDismiss = { showAddSheet = false },
                onManualInput = {
                    navController.navigate(Screen.addTransaction.route)
                },
                onQrScan = {
                    navController.navigate(Screen.SCANNER.route)
                }
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
                            Screen.Home.route -> {
                                showAddSheet = true
                            }
                            Screen.Budgets.route -> navController.navigate(Screen.budjetAdd.route)
                        }
                    }
                )
            }
        }
    }
}