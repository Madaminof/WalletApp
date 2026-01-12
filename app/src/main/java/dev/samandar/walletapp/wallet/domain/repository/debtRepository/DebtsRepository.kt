package dev.samandar.walletapp.wallet.domain.repository.debtRepository


import dev.samandar.walletapp.wallet.domain.model.debt.Debt
import dev.samandar.walletapp.wallet.domain.model.debt.DebtTransaction
import dev.samandar.walletapp.wallet.domain.model.debt.DebtWithTransactions
import kotlinx.coroutines.flow.Flow

interface DebtsRepository {
    fun getAllDebts(): Flow<List<Debt>>
    fun getActiveDebts(): Flow<List<Debt>>

    fun getDebtWithTransactions(debtId: String): Flow<DebtWithTransactions?>

    suspend fun insertDebt(debt: Debt)
    suspend fun updateDebt(debt: Debt)
    suspend fun deleteDebtById(debtId: String)
    suspend fun getDebtById(debtId: String): Debt?
    suspend fun addDebtPayment(transaction: DebtTransaction)

    fun getTotalLentAmount(): Flow<Double>
    fun getTotalBorrowedAmount(): Flow<Double>
}