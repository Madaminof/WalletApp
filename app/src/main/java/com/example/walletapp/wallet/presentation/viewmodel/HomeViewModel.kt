package com.example.walletapp.wallet.presentation.viewmodel

import androidx.compose.material.icons.Icons
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletapp.wallet.domain.model.Transaction
import com.example.walletapp.wallet.domain.model.TransactionType
import com.example.walletapp.wallet.domain.usecase.account.GetAllAccounts
import com.example.walletapp.wallet.domain.usecase.transaction.GetAllTransactions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Savings
import androidx.compose.ui.graphics.Color
import com.example.walletapp.R
import com.example.walletapp.wallet.domain.model.Account
import com.example.walletapp.wallet.domain.model.Category
import com.example.walletapp.wallet.domain.usecase.category.GetCategoriesByType
import com.example.walletapp.wallet.domain.usecase.transaction.DeleteTransaction
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class BalanceItem(
    val iconResId: Int,
    val title: String,
    val amountDouble: Double,
    val amount: String,
)

fun formatAmount(amount: Double): String {
    return String.format("%,.0f UZS", amount)
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllTransactions: GetAllTransactions,
    private val getAllAccounts: GetAllAccounts,
    private val deleteTransactionUseCase: DeleteTransaction,
    private val getCategoriesByType: GetCategoriesByType,

    ) : ViewModel() {
    private val transactionsFlow = getAllTransactions(type = null)
    private val accountsFlow = getAllAccounts()

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

    val incomeExpenseFlow: StateFlow<Pair<Double, Double>> = stateFlow()

    private fun stateFlow() = transactionsFlow
            .map { transactions ->
                val totalIncome = transactions
                    .filter { it.type == TransactionType.INCOME }
                    .sumOf { it.amount }

                val totalExpense = transactions
                    .filter { it.type == TransactionType.EXPENSE }
                    .sumOf { it.amount }

                Pair(totalIncome, totalExpense)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = Pair(0.0, 0.0)
            )

    val balanceItemsFlow: StateFlow<List<BalanceItem>> = accountsFlow
        .map { accountsList ->

            val totalBalanceValue = accountsList.sumOf { it.initialBalance }

            // Total
            val totalBalanceItem = BalanceItem(
                iconResId = R.drawable.ic_balance,
                title = "Balance",
                amountDouble = totalBalanceValue,
                amount = formatAmount(totalBalanceValue),
            )

            val individualBalances = accountsList.map { account ->

                val iconResId = when (account.name.lowercase()) {
                    "naqd pul (cash)", "naqd pul", "cash" -> R.drawable.ic_cash
                    "karta", "bank hisob", "card" -> R.drawable.ic_card
                    else -> account.iconResId ?: R.drawable.ic_wallet
                }

                BalanceItem(
                    iconResId = iconResId,
                    title = account.name,
                    amountDouble = account.initialBalance,
                    amount = formatAmount(account.initialBalance),
                )
            }

            listOf(totalBalanceItem) + individualBalances
        }
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
}