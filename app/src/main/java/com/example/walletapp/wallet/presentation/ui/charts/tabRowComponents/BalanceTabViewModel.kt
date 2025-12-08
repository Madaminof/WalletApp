package com.example.walletapp.wallet.presentation.ui.charts.tabRowComponents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletapp.wallet.domain.model.Account
import com.example.walletapp.wallet.domain.model.Transaction
import com.example.walletapp.wallet.domain.usecase.account.GetAllAccounts
import com.example.walletapp.wallet.domain.usecase.transaction.GetAllTransactions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BalanceReportState(
    val accounts: List<Account> = emptyList(),
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class BalanceTabViewModel @Inject constructor(
    private val getAllAccountsUseCase: GetAllAccounts,
    private val getAllTransactionsUseCase: GetAllTransactions
) : ViewModel() {

    private val _balanceState = MutableStateFlow(BalanceReportState(isLoading = true))
    val balanceState: StateFlow<BalanceReportState> = _balanceState.asStateFlow()

    init {
        collectDataForReport()
    }

    private fun collectDataForReport() {
        viewModelScope.launch {
            combine(
                getAllAccountsUseCase(),
                getAllTransactionsUseCase()
            ) { accounts, transactions ->
                BalanceReportState(
                    accounts = accounts,
                    transactions = transactions,
                    isLoading = false
                )
            }
                .catch { exception ->
                    _balanceState.value = _balanceState.value.copy(
                        isLoading = false,
                        error = "Ma'lumotlarni yuklashda xato: ${exception.localizedMessage}"
                    )
                }
                .collect { state ->
                    _balanceState.value = state
                }
        }
    }
}