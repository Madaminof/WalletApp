package dev.samandar.walletapp.wallet.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.data.currencyManagerApi.repository.CurrencyRepository
import dev.samandar.walletapp.wallet.domain.model.account.Account
import dev.samandar.walletapp.wallet.domain.model.Category
import dev.samandar.walletapp.wallet.domain.model.Transaction
import dev.samandar.walletapp.wallet.domain.model.TransactionType
import dev.samandar.walletapp.wallet.domain.usecase.account.GetAllAccounts
import dev.samandar.walletapp.wallet.domain.usecase.category.GetCategoriesByType
import dev.samandar.walletapp.wallet.domain.usecase.transaction.SaveTransaction
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.currency.AddTransactionCurrencyManager
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencyManager
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
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
    private val getAllAccounts: GetAllAccounts,
    private val currencyRepository: CurrencyRepository,
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
            // 1. UI-dan ma'lumotlarni yig'ish
            val amountInput = uiState.amountInput.toDoubleOrNull() ?: 0.0
            val selectedAccount = uiState.selectedAccount
            val selectedCategory = uiState.selectedCategory
            val selectedType = uiState.selectedType

            // 2. Validatsiya (Foydalanuvchiga xatoni ko'rsatish)
            val validationError = when {
                selectedCategory == null -> "Iltimos, kategoriyani tanlang"
                selectedAccount == null -> "Iltimos, hisobni tanlang"
                amountInput <= 0.0 -> "Summa 0 dan baland bo'lishi kerak"
                else -> null
            }

            if (validationError != null) {
                sendEvent(TransactionEvent.ShowSnackbar(validationError, isError = true))
                return@launch
            }

            // 3. Loading holatini yoqish
            uiState = uiState.copy(isSaving = true)

            try {
                // 4. Valyuta kurslari bilan ishlash
                val selectedLocalCurrency = AddTransactionCurrencyManager.localCurrency.value
                val allRates = currencyRepository.getLatestRatesOnce()

                // Kiritilgan valyuta kursini aniqlash
                val inputCurrencyRate = if (selectedLocalCurrency == "UZS") 1.0
                else allRates.find { it.code == selectedLocalCurrency }?.rate ?: 1.0

                // Tarix va statistika uchun har doim UZS (Base) qiymatini hisoblaymiz
                val amountInBaseCurrency = amountInput * inputCurrencyRate

                // 5. Tranzaksiya obyektini yaratish
                // DIQQAT: selectedAccount! bu yerda xavfsiz, chunki yuqorida validatsiya qildik
                val transaction = Transaction(
                    id = UUID.randomUUID().toString(),
                    amount = amountInBaseCurrency,           // DB: 1,280,000 UZS
                    originalAmount = amountInput,            // DB: 100.0
                    originalCurrency = selectedLocalCurrency, // DB: "USD"
                    amountInBase = amountInBaseCurrency,
                    exchangeRate = inputCurrencyRate,
                    type = selectedType,
                    category = selectedCategory!!,
                    account = selectedAccount!!,             // UseCase buni yangi balanslar bilan boyitadi
                    note = uiState.note.trim(),
                    date = uiState.selectedDate
                )

                // 6. UseCase-ni chaqirish (Hamma matematik ishlar UseCase ichida)
                saveTransactionUseCase(transaction)
                    .onSuccess {
                        clearState() // UI-ni tozalash
                        uiState = uiState.copy(saveSuccess = true, isSaving = false)
                        sendEvent(TransactionEvent.Success)
                    }
                    .onFailure { error ->
                        val errorMsg = error.message ?: "Saqlashda xatolik yuz berdi"
                        uiState = uiState.copy(errorMessage = errorMsg, isSaving = false)
                        sendEvent(TransactionEvent.ShowSnackbar(errorMsg, isError = true))
                    }

            } catch (e: Exception) {
                // Kutilmagan xatoliklar (masalan, Network error)
                uiState = uiState.copy(isSaving = false)
                sendEvent(TransactionEvent.ShowSnackbar(e.message ?: "Xatolik", isError = true))
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
        val sanitized =
            input.filter { it.isDigit() || (it == '.' && input.count { d -> d == '.' } <= 1) }
        uiState = uiState.copy(amountInput = sanitized)
    }

    fun setAmount(amount: Double) {
        val formatted = if (amount % 1.0 == 0.0) amount.toLong().toString()
        else String.format("%.8f", amount).trimEnd('0').trimEnd('.')
        uiState = uiState.copy(amountInput = formatted)
    }

    fun onCategorySelect(category: Category) {
        uiState = uiState.copy(selectedCategory = category)
    }

    fun onAccountSelect(account: Account) {
        uiState = uiState.copy(selectedAccount = account)
    }

    fun onNoteChange(note: String) {
        uiState = uiState.copy(note = note)
    }

    fun onDateChange(newDate: Long) {
        uiState = uiState.copy(selectedDate = newDate)
    }

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
                copy(
                    selectedType = TransactionType.EXPENSE,
                    selectedAccount = accounts.firstOrNull()
                )
            } else this
        }
    }
}