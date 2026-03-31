/*
package dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.changeUpdateAmount

import dev.samandar.walletapp.wallet.data.currencyManagerApi.repository.CurrencyRepository
import dev.samandar.walletapp.wallet.domain.repository.AccountRepository
import dev.samandar.walletapp.wallet.domain.repository.BudgetRepository
import dev.samandar.walletapp.wallet.domain.repository.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.round

class ConvertCurrencyUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val budgetRepository: BudgetRepository,
    private val currencyRepository: CurrencyRepository
) {
    suspend fun execute(fromCurrency: String, toCurrency: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                if (fromCurrency == toCurrency) return@runCatching

                // 1. Ma'lumotlarni parallel olish (Performance boost)
                val ratesDeferred = async { currencyRepository.getLatestRatesOnce() }
                val transactionsDeferred = async { transactionRepository.getAllTransactionsOnce() }
                val accountsDeferred = async { accountRepository.getAllAccountsOnce() }
                val budgetsDeferred = async { budgetRepository.getAllBudgetsOnce() } // Budjetlarni olish

                val rates = ratesDeferred.await()
                val allTransactions = transactionsDeferred.await()
                val allAccounts = accountsDeferred.await()
                val allBudgets = budgetsDeferred.await()

                // 2. Kurslarni aniq Double tipida olish
                val fromRate = if (fromCurrency == "UZS") 1.0
                else rates.find { it.code == fromCurrency }?.rate ?: 1.0

                val toRate = if (toCurrency == "UZS") 1.0
                else rates.find { it.code == toCurrency }?.rate ?: 1.0

                // 3. Koeffitsientni hisoblash (Double aniqligida)
                val factor = fromRate / toRate

                // 4. Tranzaksiyalarni o'giring (Rounding muammosini hal qilish)
                if (allTransactions.isNotEmpty()) {
                    val updatedTransactions = allTransactions.map { transaction ->
                        // Har bir amalni alohida Double'da hisoblaymiz
                        val convertedAmount = transaction.amount * factor

                        // Professional usul: 4 xonagacha aniqlikda yaxlitlash (Tiyinlar uchun)
                        // Agar 9.61219... chiqsa, u 9.6122 bo'ladi, 10 bo'lib ketmaydi
                        val precisionAmount = round(convertedAmount * 10000) / 10000.0

                        transaction.copy(amount = precisionAmount)
                    }
                    transactionRepository.updateTransactions(updatedTransactions).getOrThrow()
                }

                // 5. Hisoblarni o'giring
                if (allAccounts.isNotEmpty()) {
                    val updatedAccounts = allAccounts.map { account ->
                        val convertedBalance = account.initialBalance * factor
                        val precisionBalance = round(convertedBalance * 10000) / 10000.0

                        account.copy(initialBalance = precisionBalance)
                    }
                    accountRepository.updateAccounts(updatedAccounts).getOrThrow()
                }

                if (allBudgets.isNotEmpty()) {
                    val updatedBudgets = allBudgets.map { budget ->
                        val precisionLimit = round((budget.maxAmount * factor) * 10000) / 10000.0
                        budget.copy(maxAmount = precisionLimit)
                    }
                    budgetRepository.updateBudgets(updatedBudgets).getOrThrow()
                }

                println("CurrencyUpdate Success: factor=$factor")
            }
        }
}*/
