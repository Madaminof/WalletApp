package dev.samandar.walletapp.wallet.domain.usecase.debtsUsecase

import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.domain.model.Account
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
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(debt: Debt, account: Account, date: Long) {
        val existingDebt = repository.getDebtById(debt.id)

        if (existingDebt == null) {
            val newDebt = debt.copy(
                createdAt = date,
                remainingAmount = debt.totalAmount,
                isSettled = false
            )
            repository.insertDebt(newDebt)

            val type = if (debt.type == DebtType.LENT) TransactionType.EXPENSE else TransactionType.INCOME
            val categoryName = if (debt.type == DebtType.LENT) DebtConstants.CAT_LENT_NAME else DebtConstants.CAT_BORROWED_NAME
            val category = getOrCreateDebtCategory(categoryName, type)

            saveTransaction(
                Transaction(
                    id = UUID.randomUUID().toString(),
                    amount = debt.totalAmount,
                    type = type,
                    category = category,
                    account = account,
                    note = debt.personName,
                    date = date
                )
            )
        } else {
            repository.updateDebt(debt)
        }
    }

    private suspend fun getOrCreateDebtCategory(name: String, type: TransactionType): Category {
        return categoryRepository.getCategoryByNameAndType(name, type).firstOrNull()
            ?: Category(
                id = UUID.randomUUID().toString(),
                name = name,
                type = type,
                iconResId = R.drawable.debt_icon,
                colorArgb = 0xFFFFA500
            ).also { categoryRepository.addCategory(it) }
    }
}