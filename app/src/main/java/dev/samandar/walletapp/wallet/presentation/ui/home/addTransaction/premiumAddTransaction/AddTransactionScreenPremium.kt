package dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.domain.model.TransactionType
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.account.AccountSelectionDialog
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.calculator.CalculatorPadPremiumUI
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.categories.CategoryListSectionPremium
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.dateTime.PremiumDateTimePickerDialog
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.helperFunctions.ZoomDialog
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.note.NoteDialogContent
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.note.NoteSelectionButton
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.snackbar.ModernSnackbar
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.snackbar.MySnackbarVisuals
import dev.samandar.walletapp.wallet.presentation.viewmodel.AddTransactionViewModel
import dev.samandar.walletapp.wallet.presentation.viewmodel.TransactionEvent

@Composable
fun AddTransactionScreenPremium(
    viewModel: AddTransactionViewModel = hiltViewModel(),
    onSuccess: (String) -> Unit,
    onBack: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val state = viewModel.uiState

    var showNoteDialog by remember { mutableStateOf(false) }
    var showDateTimePicker by remember { mutableStateOf(false) }
    var showAccountDialog by remember { mutableStateOf(false) }
    var currentDisplayString by remember { mutableStateOf("0") }

    val successMessageSave = stringResource(Strings.snackbar_transaction_saved_success)
    val errorCategory = stringResource(Strings.snackbar_error_select_category)
    val errorAmount = stringResource(Strings.snackbar_error_max_amount_zero)

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is TransactionEvent.ShowSnackbar -> {

                    val displayMessage = when (event.message) {
                        Strings.snackbar_error_select_category.toString() -> errorCategory
                        Strings.snackbar_error_max_amount_zero.toString() -> errorAmount
                        else -> event.message
                    }

                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(
                        visuals = MySnackbarVisuals(
                            message = displayMessage,
                            isError = true
                        )
                    )
                }

                is TransactionEvent.Success -> {
                    onSuccess(successMessageSave)
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = { AddTransactionHeaderPremium(onClose = onBack) },
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding())
            ) {
                TransactionTypeTabRowPremium(
                    selected = state.selectedType,
                    onSelect = viewModel::onTypeChange
                )

                Box(modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()) {
                    CategoryListSectionPremium(
                        categories = if (state.selectedType == TransactionType.EXPENSE)
                            state.expenseCategories else state.incomeCategories,
                        selected = state.selectedCategory,
                        onSelect = viewModel::onCategorySelect,
                        playCustomSound = { SoundManager.playClick() }
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 0.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        NoteSelectionButton(
                            note = state.note,
                            onClick = {
                                SoundManager.playClick()
                                showNoteDialog = true
                            }
                        )
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    shadowElevation = 24.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                    ) {
                        CalculatorPadPremiumUI(
                            onDisplayChange = { currentDisplayString = it },
                            onSaveConfirmed = { finalAmount ->
                                viewModel.saveTransaction(finalAmount)
                            },
                            onDateClick = {
                                SoundManager.playClick()
                                showDateTimePicker = true
                            },
                            selectedDate = state.selectedDate,
                            currentValue = currentDisplayString,
                            selectedAccount = state.selectedAccount,
                            onClick = {
                                SoundManager.playClick()
                                showAccountDialog = true
                            }
                        )
                    }
                }
            }

            if (showNoteDialog) {
                ZoomDialog(onDismiss = { showNoteDialog = false }) { animateOut ->
                    NoteDialogContent(viewModel = viewModel, onDismiss = animateOut)
                }
            }

            if (showDateTimePicker) {
                PremiumDateTimePickerDialog(
                    initialDateTime = state.selectedDate,
                    maxDate = System.currentTimeMillis(), // FAQAT SHU YERDA CHEKLOV QO'YDIK
                    onConfirm = { newDate ->
                        viewModel.onDateChange(newDate)
                        showDateTimePicker = false
                    },
                    onDismiss = { showDateTimePicker = false }
                )
            }

            if (showAccountDialog) {
                AccountSelectionDialog(
                    accounts = state.accounts,
                    selectedAccountId = state.selectedAccount?.id,
                    onAccountSelect = { account ->
                        viewModel.onAccountSelect(account)
                        showAccountDialog = false
                    },
                    onDismiss = { showAccountDialog = false }
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 8.dp)
                .zIndex(1f),
            snackbar = { data ->
                AnimatedContent(
                    targetState = data,
                    transitionSpec = {
                        (slideInVertically(initialOffsetY = { -it }) + fadeIn() + scaleIn(
                            initialScale = 0.8f
                        ))
                            .togetherWith(slideOutVertically(targetOffsetY = { -it }) + fadeOut())
                            .using(SizeTransform(clip = false))
                    },
                    label = "PremiumSnackbarAnimation"
                ) { targetData ->
                    ModernSnackbar(snackbarData = targetData)
                }
            }
        )
    }
}