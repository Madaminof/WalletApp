package dev.samandar.walletapp.wallet.presentation.ui.budjets.addBudget

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.domain.model.BudgetPeriod
import dev.samandar.walletapp.wallet.domain.model.Category
import dev.samandar.walletapp.wallet.presentation.ui.budjets.BudgetViewModel
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.snackbar.ModernSnackbar

@Composable
fun AddBudgetScreen(
    navController: NavController,
    viewModel: BudgetViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val expenseCategories by viewModel.expenseCategories.collectAsState()

    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var maxAmountInput by remember { mutableStateOf("") }
    var selectedPeriod by remember { mutableStateOf(BudgetPeriod.MONTHLY) }
    var startDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var endDateMillis by remember { mutableStateOf<Long?>(null) }

    var showCategorySheet by remember { mutableStateOf(false) }

    val colorFalse = MaterialTheme.colorScheme.primaryContainer

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

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                AddBudgetTopBar(
                    navController = navController,
                    selectedCategory = selectedCategory,
                    maxAmountInput = maxAmountInput,
                    selectedPeriod = selectedPeriod,
                    endDateMillis = endDateMillis,
                    startDateMillis = startDateMillis,
                    viewModel = viewModel,
                    snackbarHostState = snackbarHostState,
                    scope = scope
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(modifier = Modifier.height(0.dp))

                BudgetPreviewCard(
                    categoryName = selectedCategory?.name,
                    maxAmount = maxAmountInput.toDoubleOrNull() ?: 0.0,
                    period = selectedPeriod,
                    color = selectedCategory?.let { Color(it.colorArgb) } ?: colorFalse
                )

                InputFieldsSection(
                    selectedCategory = selectedCategory,
                    onCategoryClick = { showCategorySheet = true },
                    maxAmountInput = maxAmountInput,
                    onAmountChange = { newValue ->
                        maxAmountInput = newValue.filter { char ->
                            char.isDigit() || (char == '.' && !maxAmountInput.contains('.'))
                        }
                    }
                )

                PeriodSelector(
                    selectedPeriod = selectedPeriod,
                    onPeriodSelected = { period ->
                        selectedPeriod = period
                        if (period != BudgetPeriod.RANGE) {
                            endDateMillis = null
                        }
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

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding(),

            contentAlignment = Alignment.TopCenter
        ) {
            SnackbarHost(hostState = snackbarHostState) { data ->
                AnimatedContent(
                    targetState = data,
                    transitionSpec = {
                        (slideInVertically(initialOffsetY = { -it }) + fadeIn())
                            .togetherWith(slideOutVertically(targetOffsetY = { -it }) + fadeOut())
                    },
                    label = "ModernSnackbarAnim"
                ) { targetData ->
                    ModernSnackbar(targetData)
                }
            }
        }


    }
}


@StringRes
fun BudgetPeriod.getStringResId(): Int {
    return when (this) {
        BudgetPeriod.MONTHLY -> R.string.budget_period_monthly
        BudgetPeriod.WEEKLY -> R.string.budget_period_weekly
        BudgetPeriod.RANGE -> R.string.budget_period_range
    }
}

@Composable
fun BudgetPeriod.toLocalizedString(): String {
    return stringResource(id = this.getStringResId())
}