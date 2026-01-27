package dev.samandar.walletapp.wallet.presentation.ui.budjets.editBudget

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.domain.model.Budget
import dev.samandar.walletapp.wallet.domain.model.BudgetPeriod
import dev.samandar.walletapp.wallet.domain.model.Category
import dev.samandar.walletapp.wallet.presentation.ui.budjets.BudgetDateUtils
import dev.samandar.walletapp.wallet.presentation.ui.budjets.BudgetViewModel
import dev.samandar.walletapp.wallet.presentation.ui.budjets.addBudget.BudgetPreviewCard
import dev.samandar.walletapp.wallet.presentation.ui.budjets.addBudget.CategorySelectionBottomSheet
import dev.samandar.walletapp.wallet.presentation.ui.budjets.addBudget.DateRangeSelector
import dev.samandar.walletapp.wallet.presentation.ui.budjets.addBudget.InputFieldsSection
import dev.samandar.walletapp.wallet.presentation.ui.budjets.addBudget.PeriodSelector
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.categories.getTranslatedName

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBudgetSheet(
    budgetToEdit: Budget,
    onDismiss: () -> Unit,
    viewModel: BudgetViewModel = hiltViewModel(),
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
        tonalElevation = 16.dp,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
            )
        }
    ) {
        val expenseCategories by viewModel.expenseCategories.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        val scrollState = rememberScrollState()
        var selectedCategory by remember(budgetToEdit.category) { mutableStateOf<Category?>(budgetToEdit.category) }
        var maxAmountInput by remember(budgetToEdit.maxAmount) { mutableStateOf(budgetToEdit.maxAmount.toString()) }
        var selectedPeriod by remember(budgetToEdit.period) { mutableStateOf(budgetToEdit.period) }
        var startDateMillis by remember(budgetToEdit.startDate) { mutableStateOf(budgetToEdit.startDate) }
        var endDateMillis by remember(budgetToEdit.endDate) { mutableStateOf(budgetToEdit.endDate) }
        var showCategorySheet by remember { mutableStateOf(false) }

        if (showCategorySheet) {
            CategorySelectionBottomSheet(
                expenseCategories = expenseCategories,
                selectedCategory = selectedCategory,
                onCategorySelected = { category ->
                    selectedCategory = category
                    showCategorySheet = false
                },
                onDismiss = { showCategorySheet = false }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
                .navigationBarsPadding(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.edit_budget_title),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.9f),
                    fontSize = 18.sp
                )
                EditSaveButton(
                    budgetId = budgetToEdit.id,
                    selectedCategory = selectedCategory,
                    maxAmountInput = maxAmountInput,
                    selectedPeriod = selectedPeriod,
                    endDateMillis = endDateMillis,
                    startDateMillis = startDateMillis,
                    viewModel = viewModel,
                    onSuccess = onDismiss,
                    snackbarHostState = snackbarHostState,
                    scope = scope,
                    createdAt = budgetToEdit.createdAt,
                )
            }

            BudgetPreviewCard(
                categoryName = selectedCategory?.name,
                maxAmount = maxAmountInput.toDoubleOrNull() ?: 0.0,
                period = selectedPeriod,
                selectedColor = selectedCategory?.let { Color(it.colorArgb) } ?: MaterialTheme.colorScheme.primaryContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            InputFieldsSection(
                selectedCategory = selectedCategory,
                onCategoryClick = { showCategorySheet = true },
                maxAmountInput = maxAmountInput,
                onAmountChange = { newValue ->
                    if (newValue.length <= 12) {
                        maxAmountInput = newValue.filter { char -> char.isDigit() || (char == '.' && !maxAmountInput.contains('.')) }
                    }
                }
            )

            PeriodSelector(
                selectedPeriod = selectedPeriod,
                onPeriodSelected = { newPeriod ->
                    if (newPeriod != selectedPeriod) {
                        when (newPeriod) {
                            BudgetPeriod.WEEKLY -> {
                                startDateMillis = BudgetDateUtils.getStartOfCurrentWeek()
                                endDateMillis = BudgetDateUtils.getEndOfCurrentWeek()
                            }
                            BudgetPeriod.MONTHLY -> {
                                startDateMillis = BudgetDateUtils.getStartOfMonth()
                                endDateMillis = BudgetDateUtils.getEndOfMonth()
                            }
                            BudgetPeriod.RANGE -> {
                                if (budgetToEdit.period != BudgetPeriod.RANGE) {
                                    startDateMillis = System.currentTimeMillis()
                                    endDateMillis = System.currentTimeMillis() + (24 * 60 * 60 * 1000 * 7)
                                }
                            }
                        }
                    }
                    selectedPeriod = newPeriod
                }
            )

            if (selectedPeriod == BudgetPeriod.RANGE) {
                DateRangeSelector(
                    startDateMillis = startDateMillis,
                    endDateMillis = endDateMillis,
                    onStartDateClick = { startDateMillis = it },
                    onEndDateClick = { endDateMillis = it }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

