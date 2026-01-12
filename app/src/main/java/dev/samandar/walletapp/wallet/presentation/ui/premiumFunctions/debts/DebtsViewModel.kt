package dev.samandar.walletapp.wallet.presentation.ui.premiumFunctions.debts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.samandar.walletapp.R
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.domain.model.Account
import dev.samandar.walletapp.wallet.domain.model.debt.Debt
import dev.samandar.walletapp.wallet.domain.model.debt.DebtType
import dev.samandar.walletapp.wallet.domain.repository.AccountRepository
import dev.samandar.walletapp.wallet.domain.usecase.debtsUsecase.DebtsUseCases
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
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
    val errorMessage: String? = null,
)

object DebtArgs {
    const val DEBT_TYPE = "debtType"
}

sealed interface DebtEvent {
    data class ShowSnackbar(val messageResId: Int, val isError: Boolean = false) : DebtEvent
}

@HiltViewModel
class DebtsViewModel @Inject constructor(
    private val debtsUseCases: DebtsUseCases,
    private val accountRepository: AccountRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _selectedAccount = MutableStateFlow<Account?>(null)
    private val _startDate = MutableStateFlow(System.currentTimeMillis())
    private val _dueDate = MutableStateFlow<Long?>(null)
    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)

    private val initialTypeStr: String? = savedStateHandle[DebtArgs.DEBT_TYPE]
    var type by mutableStateOf(
        initialTypeStr?.let { runCatching { DebtType.valueOf(it) }.getOrNull() } ?: DebtType.LENT
    )
        private set

    val state: StateFlow<DebtsUiState> = combine(
        debtsUseCases.getAllDebts(),
        accountRepository.getAllAccounts(),
        _selectedAccount,
        _startDate,
        _dueDate,
        _isLoading,
        _errorMessage
    ) { args: Array<Any?> ->
        val allDebts = args[0] as List<Debt>
        val accounts = args[1] as List<Account>
        val selAccount = args[2] as? Account
        val startDate = args[3] as Long
        val dueDate = args[4] as? Long
        val loading = args[5] as Boolean
        val error = args[6] as? String

        val activeDebts = allDebts.filter { !it.isSettled }

        DebtsUiState(
            debts = allDebts.sortedByDescending { it.startDate },
            totalLent = activeDebts.filter { it.type == DebtType.LENT }.sumOf { it.remainingAmount },
            totalBorrowed = activeDebts.filter { it.type == DebtType.BORROWED }.sumOf { it.remainingAmount },
            accounts = accounts,
            selectedAccount = selAccount ?: accounts.firstOrNull(),
            startDate = startDate,
            dueDate = dueDate,
            isLoading = loading,
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DebtsUiState(isLoading = true)
    )

    private val _eventFlow = MutableSharedFlow<DebtEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val eventFlow = _eventFlow.asSharedFlow()


    fun onStartDateChange(millis: Long) { _startDate.value = millis }
    fun onDueDateChange(millis: Long?) { _dueDate.value = millis }
    fun onAccountSelect(account: Account) { _selectedAccount.value = account }


    fun validateAndSaveDebt(
        personName: String,
        amountText: String,
        description: String,
        debtType: DebtType,
        selectedAccount: Account?,
        initialDebt: Debt?,
        onSuccess: () -> Unit
    ) {
        val amount = amountText.toDoubleOrNull()
        val currentUiState = state.value

        if (personName.isBlank()) {
            sendEvent(DebtEvent.ShowSnackbar(R.string.snackbar_error_empty_name, isError = true))
            return
        }
        if (amount == null || amount <= 0) {
            sendEvent(DebtEvent.ShowSnackbar(R.string.snackbar_error_max_amount_zero, isError = true))
            return
        }
        if (selectedAccount == null) {
            sendEvent(DebtEvent.ShowSnackbar(R.string.snackbar_error_select_account, isError = true))
            return
        }
        val alreadyPaid = if (initialDebt != null) {
            initialDebt.totalAmount - initialDebt.remainingAmount
        } else 0.0

        val finalDebt = Debt(
            id = initialDebt?.id ?: UUID.randomUUID().toString(),
            personName = personName.trim(),
            totalAmount = amount,
            remainingAmount = (amount - alreadyPaid).coerceAtLeast(0.0),
            type = debtType,

            startDate = currentUiState.startDate,
            dueDate = currentUiState.dueDate,

            accountId = selectedAccount.id,
            colorArgb = android.graphics.Color.parseColor(selectedAccount.colorHex ?: "#808080"),
            isSettled = (amount - alreadyPaid) <= 0,
            description = description.trim()
        )

        addUpdateDebt(finalDebt, selectedAccount, onSuccess)
    }

    fun addUpdateDebt(debt: Debt, account: Account, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                debtsUseCases.addUpdateDebt(debt, account, debt.startDate)
                sendEvent(DebtEvent.ShowSnackbar(Strings.snackbar_payment_added_success))
                onSuccess?.invoke()
            } catch (e: Exception) {
                sendEvent(DebtEvent.ShowSnackbar(Strings.snackbar_error_unknown, isError = true))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addPayment(debt: Debt, amount: Double, note: String?) = viewModelScope.launch {
        val account = _selectedAccount.value ?: state.value.selectedAccount
        if (account == null) {
            sendEvent(DebtEvent.ShowSnackbar(Strings.snackbar_error_select_account, true))
            return@launch
        }
        try {
            debtsUseCases.addDebtPayment(debt, amount, account, note)
            sendEvent(DebtEvent.ShowSnackbar(Strings.snackbar_payment_added_success))
        } catch (e: Exception) {
            sendEvent(DebtEvent.ShowSnackbar(Strings.snackbar_error_unknown, true))
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
        viewModelScope.launch {
            delay(100)
            _eventFlow.emit(event)
        }
    }

    fun getDebtWithTransactions(debtId: String) = debtsUseCases.getDebtWithTransactions(debtId)
}