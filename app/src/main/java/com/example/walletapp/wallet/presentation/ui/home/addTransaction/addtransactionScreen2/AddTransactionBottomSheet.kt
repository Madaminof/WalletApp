package com.example.walletapp.wallet.presentation.ui.home.addTransaction.addtransactionScreen2

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
// ... (boshqa importlar)
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
// ... (boshqa importlar)
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.walletapp.wallet.domain.model.Transaction
import com.example.walletapp.wallet.domain.model.TransactionType
import com.example.walletapp.wallet.presentation.viewmodel.AddTransactionViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionBottomSheet(
    viewModel: AddTransactionViewModel = hiltViewModel(),
    transactionToEdit: Transaction? = null,

    onClose: () -> Unit
) {
    val state = viewModel.uiState
    val currentType = state.selectedType

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { sheetValue ->
            sheetValue != SheetValue.Hidden
        }
    )

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val maxHeight = screenHeight * 0.9f

    var currentDisplayValue by remember { mutableStateOf("0") }

    LaunchedEffect(transactionToEdit) {
        transactionToEdit?.let { t ->
            viewModel.onTypeChange(t.type)
            viewModel.onAccountSelect(t.account)
            viewModel.onCategorySelect(t.category)
            viewModel.setAmount(t.amount)
            viewModel.onNoteChange(t.note ?: "")
            viewModel.onDateChange(t.date)
        }
    }


    ModalBottomSheet(
        onDismissRequest = onClose,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
        dragHandle = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(4.dp))
                Divider(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .background(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .heightIn(max = maxHeight)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
            ) {
                TransactionTypeTabRow(
                    selected = currentType,
                    onSelect = viewModel::onTypeChange
                )
                DateTimeRow(viewModel)

                Spacer(modifier = Modifier.height(16.dp))

                CalculatorDisplay(
                    displayValue = currentDisplayValue,
                    transactionType = currentType
                )
                Spacer(modifier = Modifier.height(12.dp))

                AccountRow(
                    accounts = state.accounts,
                    selected = state.selectedAccount,
                    onSelect = viewModel::onAccountSelect
                )
                Spacer(modifier = Modifier.height(8.dp))

                NoteFeatureController(viewModel = viewModel)

                Spacer(modifier = Modifier.height(8.dp))


                CategoryListSection(
                    categories = if (currentType == TransactionType.EXPENSE) state.expenseCategories else state.incomeCategories,
                    selected = state.selectedCategory,
                    onSelect = viewModel::onCategorySelect
                )
                Spacer(modifier = Modifier.height(8.dp))


            }


            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 8.dp)
            )
            CalculatorPad(
                onDisplayChange = { newDisplay ->
                    currentDisplayValue = newDisplay
                },
                onSaveConfirmed = { finalAmount ->
                    viewModel.setAmount(finalAmount)
                    val isAmountValid = finalAmount > 0.0
                    val isCategorySelected = state.selectedCategory != null
                    if (isAmountValid && isCategorySelected) {
                        viewModel.saveTransaction()
                        onClose()
                    } else {
                        val message = when {
                            !isAmountValid -> "Miqdorni (Amount) kiriting."
                            !isCategorySelected -> "Kategoriyani tanlang."
                            else -> "Saqlash uchun ma'lumotlar to'liq emas."
                        }

                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = message,
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                },
            )
        }
    }
}
