package dev.samandar.walletapp.wallet.presentation.ui.budjets.addBudget

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.domain.model.Budget
import dev.samandar.walletapp.wallet.domain.model.BudgetPeriod
import dev.samandar.walletapp.wallet.domain.model.Category
import dev.samandar.walletapp.wallet.presentation.ui.budjets.BudgetDateUtils
import dev.samandar.walletapp.wallet.presentation.ui.budjets.BudgetViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun SaveButton(
    selectedCategory: Category?,
    maxAmountInput: String,
    selectedPeriod: BudgetPeriod,
    endDateMillis: Long?,
    startDateMillis: Long,
    viewModel: BudgetViewModel,
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
) {
    val selectCategory = stringResource(Strings.snackbar_error_select_category)
    val maxSum = stringResource(Strings.snackbar_error_max_amount_zero)
    val selectTime = stringResource(Strings.snackbar_error_select_end_date)
    val selectTime2 = stringResource(Strings.snackbar_error_end_date_invalid)
    val successMessage = stringResource(Strings.snackbar_success_budget_added)

    fun showTemporarySnackbar(message: String) {
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            val snackJob = launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Indefinite
                )
            }
            kotlinx.coroutines.delay(1000L)
            snackJob.cancel()
            snackbarHostState.currentSnackbarData?.dismiss()
        }
    }

    TextButton(
        onClick = {
            val amount = maxAmountInput.toDoubleOrNull()

            if (selectedCategory == null) {
                showTemporarySnackbar(selectCategory)
                return@TextButton
            }
            if (amount == null || amount <= 0) {
                showTemporarySnackbar(maxSum)
                return@TextButton
            }
            if (selectedPeriod == BudgetPeriod.RANGE && endDateMillis == null) {
                showTemporarySnackbar(selectTime)
                return@TextButton
            }
            if (selectedPeriod == BudgetPeriod.RANGE && endDateMillis != null && endDateMillis <= startDateMillis) {
                showTemporarySnackbar(selectTime2)
                return@TextButton
            }

            val finalStartDate: Long
            val finalEndDate: Long?

            when (selectedPeriod) {
                BudgetPeriod.MONTHLY -> {
                    finalStartDate = BudgetDateUtils.getStartOfMonth()
                    finalEndDate = BudgetDateUtils.getEndOfMonth()
                }

                BudgetPeriod.WEEKLY -> {
                    finalStartDate = BudgetDateUtils.getStartOfCurrentWeek()
                    finalEndDate = BudgetDateUtils.getEndOfCurrentWeek()
                }

                BudgetPeriod.RANGE -> {
                    finalStartDate = startDateMillis
                    finalEndDate = endDateMillis
                }
            }

            val newBudget = Budget(
                id = UUID.randomUUID().toString(),
                category = selectedCategory,
                maxAmount = amount,
                period = selectedPeriod,
                startDate = finalStartDate,
                endDate = finalEndDate,
                isActive = true,
                createdAt = System.currentTimeMillis()
            )

            viewModel.saveBudget(newBudget)
            navController.previousBackStackEntry?.savedStateHandle?.set("success_key", successMessage)
            navController.popBackStack()
        },
        modifier = Modifier.padding(end = 8.dp),
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(
            text = stringResource(Strings.action_save),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}