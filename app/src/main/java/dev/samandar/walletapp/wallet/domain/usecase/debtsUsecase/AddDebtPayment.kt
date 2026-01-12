package dev.samandar.walletapp.wallet.domain.usecase.debtsUsecase

import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.domain.model.Account
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
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(debt: Debt, amount: Double, account: Account, note: String? = null) {
        val debtPayment = DebtTransaction(
            id = UUID.randomUUID().toString(),
            debtId = debt.id,
            amount = amount,
            date = System.currentTimeMillis(),
            note = note,
            accountId = account.id
        )
        repository.addDebtPayment(debtPayment)

        val newRemaining = (debt.remainingAmount - amount).coerceAtLeast(0.0)
        val updatedDebt = debt.copy(
            remainingAmount = newRemaining,
            isSettled = newRemaining <= 0
        )
        repository.updateDebt(updatedDebt)

        val type = if (debt.type == DebtType.LENT) TransactionType.INCOME else TransactionType.EXPENSE
        val categoryName = DebtConstants.CAT_PAYMENT_NAME
        val category = getOrCreateDebtCategory(categoryName, type)

        saveTransaction(
            Transaction(
                id = UUID.randomUUID().toString(),
                amount = amount,
                type = type,
                category = category,
                account = account,
                note = debt.personName,
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