package dev.samandar.walletapp.wallet.domain.usecase.transaction

import dev.samandar.walletapp.wallet.data.currencyManagerApi.repository.CurrencyRepository
import dev.samandar.walletapp.wallet.domain.model.Transaction
import dev.samandar.walletapp.wallet.domain.model.TransactionType
import dev.samandar.walletapp.wallet.domain.repository.TransactionRepository
import dev.samandar.walletapp.wallet.domain.repository.account.AccountRepository
import javax.inject.Inject

class DeleteTransaction @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val currencyRepository: CurrencyRepository
) {
    suspend operator fun invoke(transaction: Transaction): Result<Unit> {
        return runCatching {
            val account = transaction.account

            // 1. Kursni olish (SaveTransaction-dagi mantiq bilan bir xil)
            val rates = currencyRepository.getLatestRatesOnce()
            val accountRate = if (account.currencyCode == "UZS") 1.0
            else rates.find { it.code == account.currencyCode }?.rate ?: 1.0

            // 2. Account valyutasidagi farqni topish
            val diffInAccountCurrency = if (transaction.originalCurrency == account.currencyCode) {
                transaction.originalAmount
            } else {
                transaction.amount / accountRate
            }

            // 3. AMALNI TESKARI QILISH
            // Agar chiqim o'chsa - balansga pul qaytadi (+)
            // Agar kirim o'chsa - balansdan pul ayriladi (-)
            val isExpense = transaction.type == TransactionType.EXPENSE

            // Hozirgi balansni bazadan olish (eng yangi holati uchun)
            val currentSimpleBalance = account.balance
            val currentKonverterBalance = account.amountCurrencyKonverter

            val newSimpleBalance = if (isExpense) currentSimpleBalance + transaction.amount
            else currentSimpleBalance - transaction.amount

            val newKonverterBalance = if (isExpense) currentKonverterBalance + diffInAccountCurrency
            else currentKonverterBalance - diffInAccountCurrency

            // 4. BAZADA YANGILASH
            // Birinchi balansni to'g'irlaymiz
            accountRepository.updateAccountBalances(
                id = account.id,
                newBalance = newSimpleBalance,
                newKonverter = newKonverterBalance
            )

            // Keyin tranzaksiyani o'chiramiz
            transactionRepository.deleteTransaction(transaction.id)
        }
    }
}
