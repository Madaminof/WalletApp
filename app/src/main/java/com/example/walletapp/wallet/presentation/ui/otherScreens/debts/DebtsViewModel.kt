package com.example.walletapp.wallet.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletapp.wallet.domain.model.Account
import com.example.walletapp.wallet.domain.model.Debt
import com.example.walletapp.wallet.domain.repository.AccountRepository
import com.example.walletapp.wallet.domain.usecase.debtsUsecase.DebtsUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class DebtsUiState(
    val debts: List<Debt> = emptyList(),
    val totalLent: Double = 0.0,
    val totalOwed: Double = 0.0,
    val isLoading: Boolean = true,
    val accounts: List<Account> = emptyList(),
    val selectedAccount: Account? = null,
    val selectedDate: Long = getStartOfDay(Date().time),
    val errorMessage: String? = null
)

@HiltViewModel
class DebtsViewModel @Inject constructor(
    private val debtsUseCases: DebtsUseCases,
    private val accountRepository: AccountRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DebtsUiState())
    val state: StateFlow<DebtsUiState> = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            combine(
                debtsUseCases.getAllDebts(),
                accountRepository.getAllAccounts()
            ) { allDebts, accountsList ->
                Pair(allDebts, accountsList)
            }.collectLatest { (allDebts, accountsList) ->

                val activeDebts = allDebts.filter { !it.isSettled }
                val totalLent = activeDebts.filter { it.isLent }.sumOf { it.amount }
                val totalOwed = activeDebts.filter { !it.isLent }.sumOf { it.amount }

                _state.update { currentState ->
                    val defaultAccount = accountsList.firstOrNull()
                    val currentSelectedAccount = currentState.selectedAccount

                    val newSelectedAccount = when {
                        currentSelectedAccount == null -> defaultAccount
                        !accountsList.contains(currentSelectedAccount) -> defaultAccount
                        else -> currentSelectedAccount
                    }

                    currentState.copy(
                        debts = allDebts.sortedByDescending { debt -> debt.date },
                        totalLent = totalLent,
                        totalOwed = totalOwed,
                        isLoading = false,
                        accounts = accountsList,
                        selectedAccount = newSelectedAccount
                    )
                }
            }
        }
    }

    fun onAccountSelect(account: Account) {
        _state.update { it.copy(selectedAccount = account) }
    }

    fun onDateSelect(date: Long) {
        _state.update { it.copy(selectedDate = getStartOfDay(date)) }
    }

    fun addUpdateDebt(debt: Debt) = viewModelScope.launch {
        val account = _state.value.selectedAccount ?: run {
            _state.update { it.copy(errorMessage = "Qarz amalini bajarish uchun hisob tanlanishi shart.") }
            return@launch
        }
        val date = _state.value.selectedDate

        try {
            debtsUseCases.addUpdateDebt(
                debt = debt,
                account = account,
                transactionDate = date
            )
            _state.update { it.copy(errorMessage = null) }
        } catch (e: Exception) {
            _state.update { it.copy(errorMessage = "Qarzni saqlashda xato yuz berdi: ${e.message}") }
        }
    }

    fun deleteDebt(debtId: String) = viewModelScope.launch {
        try {
            debtsUseCases.deleteDebt(debtId)
        } catch (e: Exception) {
            _state.update { it.copy(errorMessage = "Qarzni o'chirishda xato yuz berdi: ${e.message}") }
        }
    }

    fun toggleSettled(debt: Debt) = viewModelScope.launch {
        try {
            debtsUseCases.toggleSettled(debt)
        } catch (e: Exception) {
            _state.update { it.copy(errorMessage = "To'lov holatini o'zgartirishda xato yuz berdi: ${e.message}") }
        }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }
}

private fun getStartOfDay(timeMillis: Long): Long {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = timeMillis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return calendar.timeInMillis
}