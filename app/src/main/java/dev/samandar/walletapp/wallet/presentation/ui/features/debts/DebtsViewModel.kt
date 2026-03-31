package dev.samandar.walletapp.wallet.presentation.ui.features.debts

import android.graphics.Color
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.samandar.walletapp.R
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.data.currencyManagerApi.entities.CurrencyRateEntity
import dev.samandar.walletapp.wallet.data.currencyManagerApi.repository.CurrencyRepository
import dev.samandar.walletapp.wallet.domain.model.account.Account
import dev.samandar.walletapp.wallet.domain.model.debt.Debt
import dev.samandar.walletapp.wallet.domain.model.debt.DebtType
import dev.samandar.walletapp.wallet.domain.repository.account.AccountRepository
import dev.samandar.walletapp.wallet.domain.usecase.debtsUsecase.DebtsUseCases
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.currency.AddTransactionCurrencyManager
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencyManager
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.changeUpdateAmount.CurrencyEvaluator
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class DebtsUiState(
    val debts: List<Debt> = emptyList(),
    val totalLent: Double = 0.0,
    val totalBorrowed: Double = 0.0,
    val accounts: List<Account> = emptyList(),
    val selectedAccount: Account? = null,
    val startDate: Long = System.currentTimeMillis(),
    val dueDate: Long? = null,
    val isLoading: Boolean = false,
    val currentCurrency: String = "",
    val ratesList: List<CurrencyRateEntity> = emptyList() // <--- SHU QO'SHILDI
)

sealed interface DebtEvent {
    data class ShowSnackbar(val messageResId: Int, val isError: Boolean = false) : DebtEvent
}

object DebtArgs {
    const val DEBT_TYPE = "debtType"
}

@HiltViewModel
class DebtsViewModel @Inject constructor(
    private val debtsUseCases: DebtsUseCases,
    private val accountRepository: AccountRepository,
    private val currencyRepository: CurrencyRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _selectedAccount = MutableStateFlow<Account?>(null)
    private val _startDate = MutableStateFlow(System.currentTimeMillis())
    private val _dueDate = MutableStateFlow<Long?>(null)
    private val _isLoading = MutableStateFlow(false)

    // Oqimlar (Flows)
    private val ratesFlow = currencyRepository.allRates
    private val currentCurrencyFlow = CurrencyManager.getCurrencyFlow()

    private val initialTypeStr: String? = savedStateHandle[DebtArgs.DEBT_TYPE]
    var type by mutableStateOf(
        initialTypeStr?.let { runCatching { DebtType.valueOf(it) }.getOrNull() } ?: DebtType.LENT
    )
        private set

    // 🔥 ASOSIY REAKTIV STATE
    val state: StateFlow<DebtsUiState> = combine(
        debtsUseCases.getAllDebts(),      // [0]
        accountRepository.getAllAccounts(), // [1]
        _selectedAccount,                 // [2]
        _startDate,                       // [3]
        _dueDate,                         // [4]
        _isLoading,                       // [5]
        currentCurrencyFlow,              // [6]
        ratesFlow                         // [7]
    ) { flows ->
        val allDebts = flows[0] as List<Debt>
        val accounts = flows[1] as List<Account>
        val selAccount = flows[2] as? Account
        val startDate = flows[3] as Long
        val dueDate = flows[4] as? Long
        val loading = flows[5] as Boolean
        val currency = flows[6] as String
        // ⚠️ MUHIM: Bu yerda List ko'rinishida qoldiramiz
        val ratesList = flows[7] as List<CurrencyRateEntity>

        // Qarzlar summasini foydalanuvchi tanlagan valyutaga o'giramiz
        val convertedDebts = allDebts.map { debt ->
            debt.copy(
                totalAmount = CurrencyEvaluator.convert(debt.totalAmount, currency, ratesList),
                remainingAmount = CurrencyEvaluator.convert(debt.remainingAmount, currency, ratesList)
            )
        }

        val activeDebts = convertedDebts.filter { !it.isSettled }

        DebtsUiState(
            debts = convertedDebts.sortedByDescending { it.startDate },
            totalLent = activeDebts.filter { it.type == DebtType.LENT }.sumOf { it.remainingAmount },
            totalBorrowed = activeDebts.filter { it.type == DebtType.BORROWED }.sumOf { it.remainingAmount },
            accounts = accounts,
            selectedAccount = selAccount ?: accounts.firstOrNull(),
            startDate = startDate,
            dueDate = dueDate,
            isLoading = loading,
            currentCurrency = currency,
            ratesList = ratesList
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DebtsUiState(isLoading = true)
    )

    private val _eventFlow = MutableSharedFlow<DebtEvent>(
        replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val eventFlow = _eventFlow.asSharedFlow()

    // --- LOGIKA ---

    fun onStartDateChange(millis: Long) { _startDate.value = millis }
    fun onDueDateChange(millis: Long?) { _dueDate.value = millis }
    fun onAccountSelect(account: Account) { _selectedAccount.value = account }

    fun validateAndSaveDebt(
        personName: String,
        amountText: String,
        description: String,
        debtType: DebtType,
        selectedAccount:Account?,
        inputCurrency: String,
        initialDebt: Debt?,
        onSuccess: () -> Unit
    ) {
        val amount = amountText.toDoubleOrNull() ?: 0.0

        if (personName.isBlank() || amount <= 0 || selectedAccount == null) {
            val errorRes = if (personName.isBlank()) R.string.snackbar_error_empty_name else R.string.snackbar_error_max_amount_zero
            sendEvent(DebtEvent.ShowSnackbar(errorRes, isError = true))
            return
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true

                // 🔴 YANGILANDI: Global emas, LOKAL valyutani olamiz
                val localCurrency = AddTransactionCurrencyManager.localCurrency.value
                val ratesList = ratesFlow.first()

                // Kursni aniqlaymiz
                val rate = if (inputCurrency == "UZS") 1.0
                else ratesList.find { it.code == inputCurrency }?.rate ?: 1.0
                // Bazaga saqlash uchun UZS qiymati
                val baseAmount = amount * rate

                val alreadyPaidBase = if (initialDebt != null) {
                    initialDebt.totalAmount - initialDebt.remainingAmount
                } else 0.0

                val finalDebt = Debt(
                    id = initialDebt?.id ?: UUID.randomUUID().toString(),
                    personName = personName.trim(),
                    totalAmount = baseAmount,
                    remainingAmount = (baseAmount - alreadyPaidBase).coerceAtLeast(0.0),
                    type = debtType,
                    startDate = _startDate.value,
                    dueDate = _dueDate.value,
                    accountId = selectedAccount.id,
                    colorArgb = Color.parseColor(selectedAccount.colorHex ?: "#808080"),
                    isSettled = (baseAmount - alreadyPaidBase) <= 0.01,
                    description = description.trim()
                )

                // 🔴 YANGILANDI: UseCase-ga barcha valyuta detallarini uzatamiz
                debtsUseCases.addUpdateDebt(
                    debt = finalDebt,
                    account = selectedAccount,
                    date = finalDebt.startDate,
                    amountInBase = baseAmount,
                    originalAmount = amount,
                    originalCurrency = inputCurrency,
                    exchangeRate = rate
                )

                sendEvent(DebtEvent.ShowSnackbar(Strings.snackbar_transaction_saved_success))
                onSuccess()
            } catch (e: Exception) {
                sendEvent(DebtEvent.ShowSnackbar(Strings.snackbar_error_unknown, isError = true))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addPayment(debt: Debt, amount: Double, note: String?) = viewModelScope.launch {
        val account = _selectedAccount.value ?: state.value.selectedAccount ?: return@launch
        try {
            _isLoading.value = true

            // 🔴 YANGILANDI: Lokal valyuta va kurs
            val localCurrency = AddTransactionCurrencyManager.localCurrency.value
            val ratesList = ratesFlow.first()

            val rate = if (localCurrency == "UZS") 1.0
            else ratesList.find { it.code == localCurrency }?.rate ?: 1.0

            val basePaymentAmount = amount * rate

            // UseCase-ga barcha yangi fieldlarni beramiz
            debtsUseCases.addDebtPayment(
                debt = debt,
                amountInBase = basePaymentAmount,
                originalAmount = amount,
                originalCurrency = localCurrency,
                exchangeRate = rate,
                account = account,
                note = note
            )

            sendEvent(DebtEvent.ShowSnackbar(Strings.snackbar_payment_added_success))
        } catch (e: Exception) {
            sendEvent(DebtEvent.ShowSnackbar(Strings.snackbar_error_unknown, true))
        } finally {
            _isLoading.value = false
        }
    }

    fun deleteDebt(debtId: String) = viewModelScope.launch {
        try {
            debtsUseCases.deleteDebt(debtId)
            sendEvent(DebtEvent.ShowSnackbar(Strings.snackbar_debt_deleted_success))
        } catch (e: Exception) {
            sendEvent(DebtEvent.ShowSnackbar(Strings.snackbar_error_unknown, true))
        }
    }

    private fun sendEvent(event: DebtEvent) {
        viewModelScope.launch { _eventFlow.emit(event) }
    }

    fun getDebtWithTransactions(debtId: String) = debtsUseCases.getDebtWithTransactions(debtId)
}