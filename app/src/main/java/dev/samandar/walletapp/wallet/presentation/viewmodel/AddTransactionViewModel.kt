package dev.samandar.walletapp.wallet.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.samandar.walletapp.wallet.domain.model.*
import dev.samandar.walletapp.wallet.domain.usecase.account.GetAllAccounts
import dev.samandar.walletapp.wallet.domain.usecase.category.GetCategoriesByType
import dev.samandar.walletapp.wallet.domain.usecase.transaction.SaveTransaction
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.samandar.walletapp.utils.Strings
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject


sealed interface TransactionEvent {
    data class ShowSnackbar(val message: String, val isError: Boolean = false) : TransactionEvent
    object Success : TransactionEvent
}

data class AddTransactionUiState(
    val amountInput: String = "",
    val note: String = "",
    val selectedType: TransactionType = TransactionType.EXPENSE,
    val selectedCategory: Category? = null,
    val selectedAccount: Account? = null,
    val accounts: List<Account> = emptyList(),
    val expenseCategories: List<Category> = emptyList(),
    val incomeCategories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null,
    val selectedDate: Long = Date().time,
)

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val saveTransactionUseCase: SaveTransaction,
    private val getCategoriesByType: GetCategoriesByType,
    private val getAllAccounts: GetAllAccounts
) : ViewModel() {

    var uiState by mutableStateOf(AddTransactionUiState())
        private set

    private val _eventFlow = MutableSharedFlow<TransactionEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        observeDataFlows()
    }

    private fun observeDataFlows() {
        uiState = uiState.copy(isLoading = true)

        val categoriesFlow = combine(
            getCategoriesByType(TransactionType.EXPENSE),
            getCategoriesByType(TransactionType.INCOME)
        ) { expense, income -> expense to income }

        combine(getAllAccounts(), categoriesFlow) { accounts, (expenseCats, incomeCats) ->
            uiState.copy(
                accounts = accounts,
                expenseCategories = expenseCats,
                incomeCategories = incomeCats,
                isLoading = false,
                selectedAccount = uiState.selectedAccount ?: accounts.firstOrNull()
            )
        }.onEach { updatedState ->
            uiState = updatedState
        }.launchIn(viewModelScope)
    }

    fun saveTransaction() {
        viewModelScope.launch {
            val amount = uiState.amountInput.toDoubleOrNull() ?: 0.0

            if (uiState.selectedCategory == null) {
                updateErrorState(Strings.snackbar_error_select_category.toString())
                return@launch
            }

            if (amount <= 0.0) {
                updateErrorState(Strings.snackbar_error_max_amount_zero.toString())
                return@launch
            }

            uiState = uiState.copy(isSaving = true, errorMessage = null, saveSuccess = false)

            val transaction = Transaction(
                id = "",
                amount = amount,
                type = uiState.selectedType,
                category = uiState.selectedCategory!!,
                account = uiState.selectedAccount!!,
                note = uiState.note.trim(),
                date = uiState.selectedDate,
            )

            saveTransactionUseCase(transaction)
                .onSuccess {
                    clearState(keepTypeAndAccount = false)
                    uiState = uiState.copy(saveSuccess = true, isSaving = false)
                    sendEvent(TransactionEvent.Success)
                }
                .onFailure { error ->
                    val errorMsg = error.message ?: "Error"
                    uiState = uiState.copy(errorMessage = errorMsg, isSaving = false)
                    sendEvent(TransactionEvent.ShowSnackbar(errorMsg, isError = true))
                }
        }
    }

    fun saveTransaction(amount: Double) {
        setAmount(amount)
        saveTransaction()
    }

    private fun updateErrorState(message: String) {
        uiState = uiState.copy(errorMessage = message, isSaving = false)
        sendEvent(TransactionEvent.ShowSnackbar(message, isError = true))
    }

    private fun sendEvent(event: TransactionEvent) {
        viewModelScope.launch {
            delay(100)
            _eventFlow.emit(event)
        }
    }

    fun onAmountChange(input: String) {
        val sanitized = input.filter { it.isDigit() || (it == '.' && input.count { d -> d == '.' } <= 1) }
        uiState = uiState.copy(amountInput = sanitized)
    }

    fun setAmount(amount: Double) {
        val formatted = if (amount % 1.0 == 0.0) amount.toLong().toString()
        else String.format("%.8f", amount).trimEnd('0').trimEnd('.')
        uiState = uiState.copy(amountInput = formatted)
    }

    fun onCategorySelect(category: Category) { uiState = uiState.copy(selectedCategory = category) }
    fun onAccountSelect(account: Account) { uiState = uiState.copy(selectedAccount = account) }
    fun onNoteChange(note: String) { uiState = uiState.copy(note = note) }
    fun onDateChange(newDate: Long) { uiState = uiState.copy(selectedDate = newDate) }

    fun onTypeChange(type: TransactionType) {
        uiState = uiState.copy(selectedType = type, selectedCategory = null)
    }

    fun setupForShoppingList(totalAmount: Double, note: String) {
        clearState()
        uiState = uiState.copy(
            selectedType = TransactionType.EXPENSE,
            note = note,
            selectedCategory = uiState.expenseCategories.firstOrNull(),
            selectedAccount = uiState.accounts.firstOrNull(),
        )
        setAmount(totalAmount)
    }

    fun resetSuccessState() {
        uiState = uiState.copy(saveSuccess = false)
    }

    fun clearState(keepTypeAndAccount: Boolean = true) {
        val defaultState = AddTransactionUiState()
        uiState = uiState.copy(
            selectedCategory = null,
            amountInput = "",
            note = "",
            selectedDate = Date().time,
            isSaving = false,
            errorMessage = null,
            saveSuccess = false
        ).run {
            if (!keepTypeAndAccount) {
                copy(selectedType = TransactionType.EXPENSE,
                    selectedAccount = accounts.firstOrNull())
            } else this
        }
    }
}