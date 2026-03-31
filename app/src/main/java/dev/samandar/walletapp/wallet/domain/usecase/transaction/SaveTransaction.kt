package dev.samandar.walletapp.wallet.domain.usecase.transaction

import dev.samandar.walletapp.wallet.data.currencyManagerApi.repository.CurrencyRepository
import dev.samandar.walletapp.wallet.domain.model.Transaction
import dev.samandar.walletapp.wallet.domain.model.TransactionType
import dev.samandar.walletapp.wallet.domain.model.account.Account
import dev.samandar.walletapp.wallet.domain.repository.TransactionRepository
import dev.samandar.walletapp.wallet.domain.repository.account.AccountRepository
import java.util.UUID
import javax.inject.Inject

class SaveTransaction @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val currencyRepository: CurrencyRepository
) {
    suspend operator fun invoke(transaction: Transaction): Result<Unit> {
        return runCatching {
            if (transaction.originalAmount <= 0) {
                throw IllegalArgumentException("Summa musbat bo'lishi kerak.")
            }

            val account = transaction.account
            val rates = currencyRepository.getLatestRatesOnce()

            val accountRate = if (account.currencyCode == "UZS") 1.0
            else rates.find { it.code == account.currencyCode }?.rate ?: 1.0
            println("DEBUG_SAVE: Account Rate for ${account.currencyCode} is $accountRate")

            val diffInAccountCurrency = transaction.amount / accountRate

            val currentBalanceInUZS = account.amountCurrencyKonverter * accountRate

            // a) New Simple Balance (UZSda)
            // Endi bu yerda hech qanday "shishgan" son yo'q, hammasi toza UZSda
            val newSimpleBalance = if (transaction.type == TransactionType.EXPENSE) {
                currentBalanceInUZS - transaction.amount
            } else {
                currentBalanceInUZS + transaction.amount
            }

            val newKonverterBalance = if (transaction.type == TransactionType.EXPENSE) {
                account.amountCurrencyKonverter - diffInAccountCurrency
            } else {
                account.amountCurrencyKonverter + diffInAccountCurrency
            }

            val updatedAccount = when (account) {
                is Account.Card -> account.copy(
                    balance = newSimpleBalance,
                    amountCurrencyKonverter = newKonverterBalance
                )
                is Account.Cash -> account.copy(
                    balance = newSimpleBalance,
                    amountCurrencyKonverter = newKonverterBalance
                )
            }

            val transactionId = transaction.id.ifBlank { UUID.randomUUID().toString() }
            val transactionToSave = transaction.copy(
                id = transactionId,
                account = updatedAccount
            )

            accountRepository.updateAccountBalances(
                id = account.id,
                newBalance = newSimpleBalance,
                newKonverter = newKonverterBalance
            )

            transactionRepository.saveTransaction(transactionToSave).getOrThrow()
        }
    }
}