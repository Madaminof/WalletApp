package dev.samandar.walletapp.wallet.domain.usecase.debtsUsecase

import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.data.currencyManagerApi.repository.CurrencyRepository
import dev.samandar.walletapp.wallet.domain.model.account.Account
import dev.samandar.walletapp.wallet.domain.model.Category
import dev.samandar.walletapp.wallet.domain.model.Transaction
import dev.samandar.walletapp.wallet.domain.model.TransactionType
import dev.samandar.walletapp.wallet.domain.model.debt.Debt
import dev.samandar.walletapp.wallet.domain.model.debt.DebtTransaction
import dev.samandar.walletapp.wallet.domain.model.debt.DebtType
import dev.samandar.walletapp.wallet.domain.repository.CategoryRepository
import dev.samandar.walletapp.wallet.domain.repository.debtRepository.DebtsRepository
import dev.samandar.walletapp.wallet.domain.usecase.transaction.SaveTransaction
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID
import javax.inject.Inject


class AddDebtPayment @Inject constructor(
    private val repository: DebtsRepository,
    private val saveTransaction: SaveTransaction,
    private val categoryRepository: CategoryRepository,
    private val currencyRepository: CurrencyRepository // Kurslarni olish uchun
) {
    suspend operator fun invoke(
        debt: Debt,
        amountInBase: Double,      // Har doim UZS (Statistika va Qarzni kamaytirish uchun)
        originalAmount: Double,    // Foydalanuvchi kiritgan summa (masalan 10$)
        originalCurrency: String,  // Kiritilgan valyuta (USD)
        exchangeRate: Double,      // Kiritilgan valyuta kursi
        account: Account,
        note: String? = null
    ) {
        // 1. Qarz tranzaksiyasini saqlash (Qarzlar asosan asosiy valyutada (UZS) yuritiladi)
        val debtPayment = DebtTransaction(
            id = UUID.randomUUID().toString(),
            debtId = debt.id,
            amount = amountInBase,
            date = System.currentTimeMillis(),
            note = note,
            accountId = account.id
        )
        repository.addDebtPayment(debtPayment)

        // 2. Qarzni qolgan qismini yangilash
        val newRemaining = (debt.remainingAmount - amountInBase).coerceAtLeast(0.0)
        val updatedDebt = debt.copy(
            remainingAmount = newRemaining,
            isSettled = newRemaining <= 0
        )
        repository.updateDebt(updatedDebt)

        // --- 3. Account Balansi uchun konvertatsiya (MUHIM QISM) ---
        // Account valyutasining kursini aniqlaymiz
        val allRates = currencyRepository.getLatestRatesOnce()
        val accountRate = if (account.currencyCode == "UZS") 1.0
        else allRates.find { it.code == account.currencyCode }?.rate ?: 1.0

        // Balansdan ayiriladigan summa Account valyutasida bo'lishi shart!
        // Formula: UZS qiymati / Account kursi
        val amountForAccountBalance = amountInBase * accountRate

        // 4. Tranzaksiya turini aniqlash
        val type = if (debt.type == DebtType.LENT) TransactionType.INCOME else TransactionType.EXPENSE
        val categoryName = DebtConstants.CAT_PAYMENT_NAME
        val category = getOrCreateDebtCategory(categoryName, type)

        // 5. Umumiy tranzaksiyalar tarixiga saqlash
        saveTransaction(
            Transaction(
                id = UUID.randomUUID().toString(),
                amount = amountInBase, // Account balansidan to'g'ri ayirish uchun (USD bo'lsa USD qiymati)
                amountInBase = amountInBase,     // Statistika uchun (Har doim UZS)
                originalAmount = originalAmount, // Foydalanuvchi ko'rishi uchun (Asl kiritgan summasi)
                originalCurrency = originalCurrency,
                exchangeRate = exchangeRate,
                type = type,
                category = category,
                account = account,
                note = "Qarz to'lovi: ${debt.personName}${if (!note.isNullOrBlank()) " ($note)" else ""}",
                date = System.currentTimeMillis()
            )
        )
    }

    private suspend fun getOrCreateDebtCategory(name: String, type: TransactionType): Category {
        return categoryRepository.getCategoryByNameAndType(name, type).firstOrNull()
            ?: Category(
                id = UUID.randomUUID().toString(),
                name = name,
                type = type,
                iconResId = R.drawable.ic_debt,
                colorArgb = 0xFF4CAF50
            ).also { categoryRepository.addCategory(it) }
    }
}