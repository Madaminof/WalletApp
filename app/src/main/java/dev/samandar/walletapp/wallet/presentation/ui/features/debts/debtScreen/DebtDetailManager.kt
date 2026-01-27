package dev.samandar.walletapp.wallet.presentation.ui.features.debts.debtScreen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import dev.samandar.walletapp.navigation.Screen
import dev.samandar.walletapp.wallet.domain.model.debt.Debt
import dev.samandar.walletapp.wallet.presentation.ui.features.debts.DebtsViewModel
import dev.samandar.walletapp.wallet.presentation.ui.features.debts.detail.DebtDetailBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtDetailManager(
    selectedDebtForDetails: Debt?,
    viewModel: DebtsViewModel,
    sheetState: SheetState,
    navController: NavController,
    onDismiss: () -> Unit,
    onAddPayment: (Debt) -> Unit
) {
    if (selectedDebtForDetails == null) return

    val debtWithTransactions by viewModel.getDebtWithTransactions(selectedDebtForDetails.id)
        .collectAsState(initial = null)

    DebtDetailBottomSheet(
        debt = debtWithTransactions?.debt ?: selectedDebtForDetails,
        transactions = debtWithTransactions?.transactions ?: emptyList(),
        sheetState = sheetState,
        onDismiss = onDismiss,
        onAddPayment = onAddPayment,
        onEdit = {debt ->
            onDismiss()
            navController.navigate(
                Screen.AddEditDebt.route + "?debtId=${debt.id}&debtType=${debt.type.name}"
            )
        },
        onDelete = {
            onDismiss()
            viewModel.deleteDebt(it.id)
        }
    )
}