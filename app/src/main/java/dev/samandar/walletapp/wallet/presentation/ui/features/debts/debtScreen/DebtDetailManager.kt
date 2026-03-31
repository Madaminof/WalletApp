package dev.samandar.walletapp.wallet.presentation.ui.features.debts.debtScreen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import dev.samandar.walletapp.navigation.Screen
import dev.samandar.walletapp.wallet.domain.model.debt.Debt
import dev.samandar.walletapp.wallet.presentation.ui.features.debts.DebtsViewModel
import dev.samandar.walletapp.wallet.presentation.ui.features.debts.detail.DebtDetailBottomSheet
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.changeUpdateAmount.CurrencyEvaluator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtDetailManager(
    selectedDebtForDetails: Debt?,
    viewModel: DebtsViewModel,
    sheetState: SheetState,
    navController: NavController,
    onDismiss: () -> Unit,
    onAddPayment: (Debt) -> Unit,
) {
    if (selectedDebtForDetails == null) return

    val uiState by viewModel.state.collectAsState()

    val debtWithTransactions by viewModel.getDebtWithTransactions(selectedDebtForDetails.id)
        .collectAsState(initial = null)

    // 1. Original debtni olamiz
    val rawDebt = debtWithTransactions?.debt ?: selectedDebtForDetails

    // 2. UI uchun konvertatsiya qilingan yangi Debt obyektini yasab olamiz
    // Bu yerda CurrencyEvaluator ishlatiladi, shunda BottomSheet tayyor raqamni oladi
    val convertedDebtForUi = remember(rawDebt, uiState.currentCurrency, uiState.ratesList) {
        rawDebt.copy(
            totalAmount = CurrencyEvaluator.convert(rawDebt.totalAmount, uiState.currentCurrency, uiState.ratesList),
            remainingAmount = CurrencyEvaluator.convert(rawDebt.remainingAmount, uiState.currentCurrency, uiState.ratesList)
        )
    }

    DebtDetailBottomSheet(
        debt = convertedDebtForUi,
        transactions = debtWithTransactions?.transactions ?: emptyList(),
        sheetState = sheetState,
        onDismiss = onDismiss,
        onAddPayment = onAddPayment,
        onEdit = { debt ->
            onDismiss()
            navController.navigate(
                Screen.AddEditDebt.route + "?debtId=${debt.id}&debtType=${debt.type.name}"
            )
        },
        onDelete = {
            onDismiss()
            viewModel.deleteDebt(it.id)
        },
        viewModel = viewModel
    )
}