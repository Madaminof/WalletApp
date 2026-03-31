package dev.samandar.walletapp.wallet.presentation.ui.features.debts.addDebt

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.zIndex
import dev.samandar.walletapp.R
import dev.samandar.walletapp.ui.theme.expenseColor
import dev.samandar.walletapp.wallet.domain.model.debt.Debt
import dev.samandar.walletapp.wallet.domain.model.debt.DebtType
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.snackbar.ModernSnackbar
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.snackbar.MySnackbarVisuals
import dev.samandar.walletapp.wallet.presentation.ui.features.debts.DebtEvent
import dev.samandar.walletapp.wallet.presentation.ui.features.debts.DebtsViewModel
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencyManager
import dev.samandar.walletapp.wallet.presentation.ui.topbars.addTopbar.AddTopBar


@Composable
fun AddEditDebtScreen(
    navController: NavController,
    viewModel: DebtsViewModel,
    initialDebt: Debt?,
    initialType: DebtType?
) {
    var selectedType by remember {
        mutableStateOf(initialDebt?.type ?: initialType ?: DebtType.LENT)
    }
    val uiState by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var personName by remember { mutableStateOf(initialDebt?.personName ?: "") }
    var amountText by remember { mutableStateOf(initialDebt?.totalAmount?.toString()?.replace(".0", "") ?: "") }

    val accentColor = if (selectedType == DebtType.LENT) MaterialTheme.colorScheme.primary else expenseColor

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            if (event is DebtEvent.ShowSnackbar) {
                snackbarHostState.currentSnackbarData?.dismiss()

                val translatedMessage = context.getString(event.messageResId)

                snackbarHostState.showSnackbar(
                    visuals = MySnackbarVisuals(
                        message = translatedMessage,
                        isError = event.isError,
                        duration = SnackbarDuration.Short
                    )
                )
            }
        }
    }
    val activeCurrency by CurrencyManager.currentCurrency

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                AddTopBar(
                    navController = navController,
                    canSave = personName.isNotBlank() && amountText.isNotBlank() && !uiState.isLoading,
                    title = if (initialDebt == null) stringResource(R.string.title_add_debt)
                    else stringResource(R.string.title_edit_debt),
                    onSave = {
                        viewModel.validateAndSaveDebt(
                            personName = personName,
                            amountText = amountText,
                            debtType = selectedType,
                            selectedAccount = uiState.selectedAccount,
                            initialDebt = initialDebt,
                            onSuccess = {
                                navController.popBackStack()
                            },
                            description = "",
                            inputCurrency = activeCurrency
                        )
                    }
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                DebtTypeSelector(
                    selectedType = selectedType,
                    onTypeSelect = { selectedType = it }
                )

                Surface(
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.5f),
                    shape = RoundedCornerShape(24.dp),
                ) {
                    DebtInputFields(
                        personName = personName,
                        onNameChange = { personName = it },
                        amountText = amountText,
                        onAmountChange = { amountText = it },
                        accentColor = accentColor
                    )
                }
                DebtDetailsSection(
                    accounts = uiState.accounts,
                    selectedAccount = uiState.selectedAccount,
                    onAccountSelect = viewModel::onAccountSelect,
                    dueDate = uiState.dueDate,
                    onDueDateSelect = viewModel::onDueDateChange,
                    accentColor = accentColor,
                    startDate = uiState.startDate,
                    onStartDateSelect = viewModel::onStartDateChange
                )

                if (uiState.isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = accentColor
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 4.dp)
                .zIndex(1f),
            snackbar = { data ->
                ModernSnackbar(snackbarData = data)
            }
        )
    }
}