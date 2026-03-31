package dev.samandar.walletapp.wallet.domain.usecase.debtsUsecase

import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.data.currencyManagerApi.repository.CurrencyRepository
import dev.samandar.walletapp.wallet.domain.model.account.Account
import dev.samandar.walletapp.wallet.domain.model.Category
import dev.samandar.walletapp.wallet.domain.model.Transaction
import dev.samandar.walletapp.wallet.domain.model.TransactionType
import dev.samandar.walletapp.wallet.domain.model.debt.Debt
import dev.samandar.walletapp.wallet.domain.model.debt.DebtType
import dev.samandar.walletapp.wallet.domain.repository.CategoryRepository
import dev.samandar.walletapp.wallet.domain.repository.debtRepository.DebtsRepository
import dev.samandar.walletapp.wallet.domain.usecase.transaction.SaveTransaction
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID
import javax.inject.Inject

object DebtConstants {
    const val CAT_LENT_NAME = "Lent"
    const val CAT_BORROWED_NAME = "Borrowed"
    const val CAT_PAYMENT_NAME = "Debt Payment"
}


class AddUpdateDebt @Inject constructor(
    private val repository: DebtsRepository,
    private val saveTransaction: SaveTransaction,
    private val categoryRepository: CategoryRepository,
    private val currencyRepository: CurrencyRepository
) {
    suspend operator fun invoke(
        debt: Debt,
        account: Account,
        date: Long,
        amountInBase: Double,      // UZS ga o'girilgan jami summa (Qarzlar jadvali uchun)
        originalAmount: Double,    // Asl kiritilgan summa (masalan 100$)
        originalCurrency: String,  // Valyuta kodi (USD)
        exchangeRate: Double       // Kurs (12850)
    ) {
        val existingDebt = repository.getDebtById(debt.id)

        if (existingDebt == null) {
            // 1. Yangi qarz yaratish
            val newDebt = debt.copy(
                createdAt = date,
                totalAmount = amountInBase,
                remainingAmount = amountInBase,
                isSettled = false
            )
            repository.insertDebt(newDebt)

            // 2. Tranzaksiya turini va kategoriyani aniqlash
            // Qarz BERGANDA (LENT) pul chiqib ketadi (EXPENSE)
            // Qarz OLGANDA (BORROWED) pul kirib keladi (INCOME)
            val type = if (debt.type == DebtType.LENT) TransactionType.EXPENSE else TransactionType.INCOME
            val categoryName = if (debt.type == DebtType.LENT) DebtConstants.CAT_LENT_NAME else DebtConstants.CAT_BORROWED_NAME
            val category = getOrCreateDebtCategory(categoryName, type)

            // --- 3. Balans uchun hisob-kitob (MUHIM) ---
            val allRates = currencyRepository.getLatestRatesOnce()
            val accountRate = if (account.currencyCode == "UZS") 1.0
            else allRates.find { it.code == account.currencyCode }?.rate ?: 1.0

            // Account balansini to'g'ri o'zgartirish uchun summa
            // (UZS qiymati / Account kursi)
            val amountForAccountBalance = amountInBase / accountRate

            // 4. Tranzaksiyalar tarixiga saqlash
            saveTransaction(
                Transaction(
                    id = UUID.randomUUID().toString(),
                    amount = amountInBase, // Account balansidan to'g'ri ayirish/qo'shish uchun
                    amountInBase = amountInBase,     // Statistika uchun (Har doim UZS)
                    originalAmount = originalAmount, // Foydalanuvchi kiritgan (100.0)
                    originalCurrency = originalCurrency,
                    exchangeRate = exchangeRate,
                    type = type,
                    category = category,
                    account = account,
                    note = "${if (debt.type == DebtType.LENT) "Qarz berildi" else "Qarz olindi"}: ${debt.personName}",
                    date = date
                )
            )
        } else {
            // Update holati
            repository.updateDebt(debt)
        }
    }

    private suspend fun getOrCreateDebtCategory(name: String, type: TransactionType): Category {
        return categoryRepository.getCategoryByNameAndType(name, type).firstOrNull()
            ?: Category(
                id = UUID.randomUUID().toString(),
                name = name,
                type = type,
                iconResId = R.drawable.debt_ic2,
                colorArgb = 0xFFFFA500
            ).also { categoryRepository.addCategory(it) }
    }
}