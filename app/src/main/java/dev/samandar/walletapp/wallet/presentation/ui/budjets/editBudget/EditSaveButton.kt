package dev.samandar.walletapp.wallet.presentation.ui.budjets.editBudget

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.domain.model.Budget
import dev.samandar.walletapp.wallet.domain.model.BudgetPeriod
import dev.samandar.walletapp.wallet.domain.model.Category
import dev.samandar.walletapp.wallet.presentation.ui.budjets.BudgetViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditSaveButton(
    budgetId: String,
    selectedCategory: Category?,
    maxAmountInput: String,
    selectedPeriod: BudgetPeriod,
    endDateMillis: Long?,
    startDateMillis: Long,
    createdAt: Long,
    viewModel: BudgetViewModel,
    onSuccess: () -> Unit,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
) {
    val amount = maxAmountInput.toDoubleOrNull()
    val isReadyToSave = selectedCategory != null && amount != null && amount > 0.0 &&
            (selectedPeriod != BudgetPeriod.RANGE || (endDateMillis != null && endDateMillis > startDateMillis))

    val errorMsg = stringResource(R.string.snackbar_fill_fields_error)
    val successMsg = stringResource(R.string.snackbar_budget_update_success)


    fun showTemporarySnackbar(message: String) {
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()

            val snackJob = launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Indefinite
                )
            }
            delay(1000L)
            snackJob.cancel()
            snackbarHostState.currentSnackbarData?.dismiss()
        }
    }


    TextButton(
        onClick = {
            if (!isReadyToSave) {
                scope.launch { showTemporarySnackbar(errorMsg) }
                return@TextButton
            }

            val updatedBudget = Budget(
                id = budgetId,
                category = selectedCategory!!,
                maxAmount = amount!!,
                period = selectedPeriod,
                startDate = startDateMillis,
                endDate = endDateMillis,
                isActive = true,
                createdAt = createdAt
            )

            viewModel.updateBudget(updatedBudget)
            scope.launch { showTemporarySnackbar(successMsg) }
            onSuccess()
        },
        enabled = isReadyToSave
    ) {
        Text(
            text = stringResource(R.string.edit_budget_button_update),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 14.sp
        )
    }
}
