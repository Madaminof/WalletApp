package dev.samandar.walletapp.wallet.presentation.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.samandar.walletapp.wallet.data.currencyManagerApi.repository.CurrencyRepository
import dev.samandar.walletapp.wallet.domain.model.account.Account
import dev.samandar.walletapp.wallet.domain.model.Category
import dev.samandar.walletapp.wallet.domain.model.Transaction
import dev.samandar.walletapp.wallet.domain.model.TransactionType
import dev.samandar.walletapp.wallet.domain.usecase.account.GetAllAccounts
import dev.samandar.walletapp.wallet.domain.usecase.category.GetCategoriesByType
import dev.samandar.walletapp.wallet.domain.usecase.transaction.DeleteTransaction
import dev.samandar.walletapp.wallet.domain.usecase.transaction.GetAllTransactions
import dev.samandar.walletapp.wallet.domain.usecase.transaction.UpdateTransaction
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencyManager
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.changeUpdateAmount.CurrencyEvaluator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllTransactions: GetAllTransactions,
    private val getAllAccounts: GetAllAccounts,
    private val deleteTransactionUseCase: DeleteTransaction,
    private val getCategoriesByType: GetCategoriesByType,
    private val updateTransactionUsecase: UpdateTransaction,
    private val currencyRepository: CurrencyRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val ratesFlow = currencyRepository.allRates
    private val currentCurrencyFlow = CurrencyManager.getCurrencyFlow()

    val transactions: StateFlow<List<Transaction>> = combine(
        getAllTransactions(type = null),
        ratesFlow,
        currentCurrencyFlow
    ) { transactions, rates, currentCurrency ->
        transactions.map { transaction ->
            val displayAmount = CurrencyEvaluator.convert(
                amount = transaction.amount,
                currentCurrency = currentCurrency,
                rates = rates
            )
            transaction.copy(amount = displayAmount)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // 3. Hisoblar (Accounts) - Balanslar ham konvertatsiya qilinadi
    val accounts: StateFlow<List<Account>> = combine(
        getAllAccounts(),
        ratesFlow,
        currentCurrencyFlow
    ) { accountsList, rates, currentCurrency ->
        // Bu yerda List<Account> qaytayotganini aniq ko'rsatdik
        accountsList.map { account ->
            val displayBalance = CurrencyEvaluator.convert(
                amount = account.balance, // initialBalance emas, balance!
                currentCurrency = currentCurrency,
                rates = rates
            )
            // Sealed class uchun maxsus extension ishlatamiz
            account.copyAccountWithBalance(displayBalance)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )

    // Kategoriyalar qolgan qismlari o'zgarishsiz...
    val incomeCategories: StateFlow<List<Category>> = getCategoriesByType(TransactionType.INCOME)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val expenseCategories: StateFlow<List<Category>> = getCategoriesByType(TransactionType.EXPENSE)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun deleteTransaction(transaction: Transaction) { // 👈 String emas, Transaction obyektini olamiz
        viewModelScope.launch {
            try {
                // UseCase-ga butun obyektni uzatamiz
                deleteTransactionUseCase(transaction)

                // Muvaffaqiyatli bo'lsa Snackbar yoki log chiqarish mumkin
                println("Tranzaksiya muvaffaqiyatli o'chirildi va balans qaytarildi")

            } catch (e: Exception) {
                // Xatolikni UI-da ko'rsatish professionalroq bo'ladi
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

    // ⚠️ Sealed class uchun nusxa olish funksiyasi (Fayl pastiga yoki ViewModel ichiga)
    private fun Account.copyAccountWithBalance(newBalance: Double): Account {
        return when (this) {
            is Account.Cash -> this.copy(balance = newBalance)
            is Account.Card -> this.copy(balance = newBalance)
        }
    }
}