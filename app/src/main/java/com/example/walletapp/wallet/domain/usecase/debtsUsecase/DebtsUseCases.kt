package com.example.walletapp.wallet.domain.usecase.debtsUsecase

import com.example.walletapp.R
import com.example.walletapp.wallet.domain.model.Account
import com.example.walletapp.wallet.domain.model.Category
import com.example.walletapp.wallet.domain.model.Debt
import com.example.walletapp.wallet.domain.model.Transaction
import com.example.walletapp.wallet.domain.model.TransactionType
import com.example.walletapp.wallet.domain.repository.AccountRepository
import com.example.walletapp.wallet.domain.repository.CategoryRepository
import com.example.walletapp.wallet.domain.repository.DebtsRepository
import com.example.walletapp.wallet.domain.usecase.transaction.SaveTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID
import javax.inject.Inject

data class DebtsUseCases @Inject constructor(
    val getAllDebts: GetAllDebts,
    val addUpdateDebt: AddUpdateDebt,
    val deleteDebt: DeleteDebt,
    val toggleSettled: ToggleSettled
)

class GetAllDebts @Inject constructor(
    private val repository: DebtsRepository
) {
    operator fun invoke(): Flow<List<Debt>> {
        return repository.getAllDebts()
    }
}

class AddUpdateDebt @Inject constructor(
    private val repository: DebtsRepository,
    private val saveTransaction: SaveTransaction,
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(
        debt: Debt,
        account: Account,
        transactionDate: Long
    ) {
        if (debt.id.isBlank()) {

            val debtId = UUID.randomUUID().toString()
            val newDebt = debt.copy(
                id = debtId,
                date = transactionDate,
                isSettled = false
            )
            repository.insertDebt(newDebt)

            val categoryName = if (debt.isLent) "Qarz berish" else "Qarz olish"
            val categoryType = if (debt.isLent) TransactionType.EXPENSE else TransactionType.INCOME
            val defaultColorArgb = 0xFFFFA500L

            var debtCategory = categoryRepository
                .getCategoryByNameAndType(categoryName, categoryType)
                .firstOrNull()

            if (debtCategory == null) {

                val newCategory = Category(
                    id = UUID.randomUUID().toString(),
                    name = categoryName,
                    type = categoryType,
                    iconResId = R.drawable.ic_debt,
                    colorArgb = defaultColorArgb,
                )
                categoryRepository.addCategory(newCategory)
                debtCategory = newCategory
            }
            if (debtCategory != null) {
                val transaction = Transaction(
                    id = UUID.randomUUID().toString(),
                    amount = debt.amount,
                    type = categoryType,
                    category = debtCategory,
                    account = account,
                    note = "Debts: ${debt.person} (${categoryName})",
                    date = transactionDate,
                )
                saveTransaction(transaction)
            }

        } else {
            repository.updateDebt(debt)
        }
    }
}


class ToggleSettled @Inject constructor(
    private val repository: DebtsRepository,
    private val saveTransaction: SaveTransaction,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(debt: Debt) {
        val updatedDebt = debt.copy(isSettled = !debt.isSettled)
        repository.updateDebt(updatedDebt)

        if (updatedDebt.isSettled) {

            val defaultAccount = accountRepository.getAllAccounts().firstOrNull()?.firstOrNull()

            val categoryType = if (debt.isLent) TransactionType.INCOME else TransactionType.EXPENSE
            val categoryName = "Qarzni to'lash"
            val defaultColorArgb = 0xFFFFA500L

            var paymentCategory = categoryRepository
                .getCategoryByNameAndType(categoryName, categoryType)
                .firstOrNull()

            if (paymentCategory == null) {
                val newCategory = Category(
                    id = UUID.randomUUID().toString(),
                    name = categoryName,
                    type = categoryType,
                    iconResId = R.drawable.ic_debt, // ✅ Siz xohlagan ikona
                    colorArgb = defaultColorArgb,
                )
                categoryRepository.addCategory(newCategory) // addCategory funksiyasi bor deb faraz qilindi
                paymentCategory = newCategory
            }

            if (defaultAccount != null && paymentCategory != null) {

                val note = "Qarz to'lovi: ${debt.person} (${if (debt.isLent) "Menga to'landi" else "Men to'ladim"})"

                val transaction = Transaction(
                    id = UUID.randomUUID().toString(),
                    amount = debt.amount,
                    type = categoryType,
                    category = paymentCategory, // ✅ Endi bu kategoriya to'g'ri ikonaga ega
                    account = defaultAccount,
                    note = note,
                    date = System.currentTimeMillis()
                )
                saveTransaction(transaction)
            }
        }
    }
}

class DeleteDebt @Inject constructor(
    private val repository: DebtsRepository
) {
    suspend operator fun invoke(debtId: String) {
        repository.deleteDebtById(debtId)
    }
}