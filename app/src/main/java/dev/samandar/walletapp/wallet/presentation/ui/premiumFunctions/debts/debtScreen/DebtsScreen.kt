package dev.samandar.walletapp.wallet.presentation.ui.premiumFunctions.debts.debtScreen

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import dev.samandar.walletapp.R
import dev.samandar.walletapp.ui.theme.expenseColor
import dev.samandar.walletapp.ui.theme.incomeColor
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.domain.model.debt.Debt
import dev.samandar.walletapp.wallet.domain.model.debt.DebtType
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.snackbar.ModernSnackbar
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.snackbar.MySnackbarVisuals
import dev.samandar.walletapp.wallet.presentation.ui.premiumFunctions.debts.DebtArgs
import dev.samandar.walletapp.wallet.presentation.ui.premiumFunctions.debts.addDebt.AddPaymentDialog
import dev.samandar.walletapp.wallet.presentation.ui.premiumFunctions.debts.DebtEvent
import dev.samandar.walletapp.wallet.presentation.ui.premiumFunctions.debts.DebtsViewModel
import dev.samandar.walletapp.wallet.presentation.ui.topbars.topbarScreen.CustomTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtsScreen(
    navController: NavController,
    viewModel: DebtsViewModel
) {
    val uiState by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedDebtForDetails by remember { mutableStateOf<Debt?>(null) }
    var showPaymentDialog by remember { mutableStateOf<Debt?>(null) }

    val currentType = remember(selectedTab) {
        if (selectedTab == 0) DebtType.LENT else DebtType.BORROWED
    }
    val filteredDebts = remember(uiState.debts, currentType) {
        uiState.debts.filter { it.type == currentType }
    }
    val fabColor by animateColorAsState(
        targetValue = if (selectedTab == 0) MaterialTheme.colorScheme.primary else expenseColor,
        label = "fabColor"
    )

    val tabs = listOf(
        stringResource(R.string.tab_i_lent),    // Bergan qarzlarim
        stringResource(R.string.tab_i_borrowed) // Olgan qarzlarim
    )
    val activeColor = if (selectedTab == 0) MaterialTheme.colorScheme.primary else expenseColor

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            if (event is DebtEvent.ShowSnackbar) {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(
                    visuals = MySnackbarVisuals(
                        message = context.getString(event.messageResId),
                        isError = event.isError,
                        duration = SnackbarDuration.Short
                    )
                )
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                CustomTopBar(
                    title = stringResource(R.string.title_debts),
                    onBackClick = { navController.popBackStack() }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        val type = if (selectedTab == 0) DebtType.LENT else DebtType.BORROWED
                        navController.navigate("add_edit_debt?${DebtArgs.DEBT_TYPE}=${type.name}")
                    },
                    containerColor = fabColor,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                PremiumDebtsSummaryCard(
                    totalLent = uiState.totalLent,
                    totalBorrowed = uiState.totalBorrowed
                )

                DebtTabRow(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    tabs = tabs,
                    activeColor = activeColor
                )

                if (uiState.isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(strokeWidth = 3.dp)
                    }
                } else if (filteredDebts.isEmpty()) {
                    EmptyDebtsState(
                        message = if (selectedTab == 0) stringResource(Strings.empty_lent_debts) else stringResource(Strings.empty_borrowed_debts),
                        activeColor = activeColor
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        val sortedList = filteredDebts.sortedBy { it.isSettled }
                        itemsIndexed(
                            items = sortedList,
                            key = { _, debt -> debt.id }
                        ) { index, debt ->
                            DebtItemRow(
                                debt = debt,
                                showDivider = index < sortedList.lastIndex,
                                onClick = { selectedDebtForDetails = it },
                                onQuickAddPayment = { showPaymentDialog = it }
                            )
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 8.dp)
                .fillMaxWidth()
                .zIndex(2f)
        ) { data ->
            ModernSnackbar(snackbarData = data)
        }
    }

    DebtDetailManager(
        selectedDebtForDetails = selectedDebtForDetails,
        viewModel = viewModel,
        sheetState = sheetState,
        navController = navController,
        onDismiss = { selectedDebtForDetails = null },
        onAddPayment = { showPaymentDialog = it }
    )

    if (showPaymentDialog != null) {
        AddPaymentDialog(
            debt = showPaymentDialog!!,
            accounts = uiState.accounts,
            onDismiss = { showPaymentDialog = null },
            onConfirm = { amount, account, date, note ->
                viewModel.addPayment(
                    debt = showPaymentDialog!!,
                    amount = amount,
                    note = note
                )
                showPaymentDialog = null
            }
        )
    }
}