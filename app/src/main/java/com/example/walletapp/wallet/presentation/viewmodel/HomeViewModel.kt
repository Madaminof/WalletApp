package com.example.walletapp.wallet.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletapp.wallet.domain.model.Transaction
import com.example.walletapp.wallet.domain.model.TransactionType
import com.example.walletapp.wallet.domain.usecase.account.GetAllAccounts
import com.example.walletapp.wallet.domain.usecase.transaction.GetAllTransactions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import com.example.walletapp.wallet.domain.model.Account
import com.example.walletapp.wallet.domain.model.Category
import com.example.walletapp.wallet.domain.usecase.category.GetCategoriesByType
import com.example.walletapp.wallet.domain.usecase.transaction.DeleteTransaction
import com.example.walletapp.wallet.domain.usecase.transaction.UpdateTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllTransactions: GetAllTransactions,
    private val getAllAccounts: GetAllAccounts,
    private val deleteTransactionUseCase: DeleteTransaction,
    private val getCategoriesByType: GetCategoriesByType,
    private val updateTransactionUsecase: UpdateTransaction

    ) : ViewModel() {
    private val transactionsFlow = getAllTransactions(type = null)

    val incomeCategories: StateFlow<List<Category>> = getCategoriesByType(TransactionType.INCOME)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val expenseCategories: StateFlow<List<Category>> = getCategoriesByType(TransactionType.EXPENSE)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val allCategories: StateFlow<List<Category>> = combine(
        incomeCategories,
        expenseCategories
    ) { income, expense ->
        income + expense
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    val transactions: StateFlow<List<Transaction>> = transactionsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val accounts: StateFlow<List<Account>> = getAllAccounts()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    fun deleteTransaction(transactionId: String) {
        viewModelScope.launch {
            try {
                deleteTransactionUseCase(transactionId)
            } catch (e: Exception) {
                println("Delete error: ${e.message}")
            }
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    updateTransactionUsecase(transaction)
                }
            } catch (e: Exception) {
                println("Update error: ${e.message}")
            }
        }
    }

}