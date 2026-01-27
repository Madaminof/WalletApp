package dev.samandar.walletapp.wallet.presentation.ui.budjets.addBudget

import androidx.compose.animation.core.Spring
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import dev.samandar.walletapp.R


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

    val amount = maxAmountInput.toDoubleOrNull() ?: 0.0
    val isFormValid = selectedCategory != null && amount > 0.0

    fun showTemporarySnackbar(message: String) {
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            val snackJob = launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Indefinite
                )
            }
            kotlinx.coroutines.delay(1200L)
            snackJob.cancel()
            snackbarHostState.currentSnackbarData?.dismiss()
        }
    }

    PremiumIconButton(
        icon = R.drawable.check_icon,
        enabled = true,
        color = if (isFormValid) {
            MaterialTheme.colorScheme.primary.copy(0.9f)
        } else {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        },
        modifier = Modifier.padding(end = 16.dp),
        onClick = {
            val finalAmount = maxAmountInput.toDoubleOrNull()

            if (selectedCategory == null) {
                showTemporarySnackbar(selectCategory)
                return@PremiumIconButton
            }
            if (finalAmount == null || finalAmount <= 0) {
                showTemporarySnackbar(maxSum)
                return@PremiumIconButton
            }
            if (selectedPeriod == BudgetPeriod.RANGE && endDateMillis == null) {
                showTemporarySnackbar(selectTime)
                return@PremiumIconButton
            }
            if (selectedPeriod == BudgetPeriod.RANGE && endDateMillis != null && endDateMillis <= startDateMillis) {
                showTemporarySnackbar(selectTime2)
                return@PremiumIconButton
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
                maxAmount = finalAmount,
                period = selectedPeriod,
                startDate = finalStartDate,
                endDate = finalEndDate,
                isActive = true,
                createdAt = System.currentTimeMillis()
            )

            viewModel.saveBudget(newBudget)
            navController.previousBackStackEntry?.savedStateHandle?.set("success_key", successMessage)
            navController.popBackStack()
        }
    )
}

@Composable
fun PremiumIconButton(
    icon: Int,
    onClick: () -> Unit,
    color: Color,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.4f,
        label = "alpha"
    )

    Icon(
        painter = painterResource(id = icon),
        contentDescription = null,
        tint = color,
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .size(32.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { if (enabled) onClick() }
            )
    )
}
