package dev.samandar.walletapp.wallet.data.local.dao.debtDao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import dev.samandar.walletapp.wallet.data.local.entity.debt.DebtEntity
import dev.samandar.walletapp.wallet.data.local.entity.debt.DebtTransactionEntity
import dev.samandar.walletapp.wallet.data.local.entity.debt.DebtWithTransactionsRelation
import dev.samandar.walletapp.wallet.domain.model.debt.DebtWithTransactions
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebt(debt: DebtEntity)

    @Update
    suspend fun updateDebt(debt: DebtEntity)

    @Query("DELETE FROM debts WHERE id = :debtId")
    suspend fun deleteDebtById(debtId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebtTransaction(transaction: DebtTransactionEntity)

    @Transaction
    suspend fun addPaymentToDebt(transaction: DebtTransactionEntity) {
        insertDebtTransaction(transaction)

        val debt = getDebtByIdInternal(transaction.debtId)
        debt?.let {
            val newRemainingAmount = (it.remainingAmount - transaction.amount).coerceAtLeast(0.0)
            val isSettled = newRemainingAmount <= 0.0

            updateDebt(it.copy(
                remainingAmount = newRemainingAmount,
                isSettled = isSettled
            ))
        }
    }
    @Query("SELECT * FROM debts WHERE id = :debtId LIMIT 1")
    suspend fun getDebtById(debtId: String): DebtEntity?

    @Query("SELECT * FROM debts WHERE id = :debtId")
    suspend fun getDebtByIdInternal(debtId: String): DebtEntity?

    @Query("SELECT * FROM debts ORDER BY createdAt DESC")
    fun getAllDebts(): Flow<List<DebtEntity>>

    @Query("SELECT * FROM debts WHERE isSettled = 0 ORDER BY dueDate ASC")
    fun getActiveDebts(): Flow<List<DebtEntity>>

    @Transaction
    @Query("SELECT * FROM debts WHERE id = :debtId")
    fun getDebtWithTransactions(debtId: String): Flow<DebtWithTransactionsRelation>

    @Query("SELECT SUM(remainingAmount) FROM debts WHERE type = 'LENT' AND isSettled = 0")
    fun getTotalLentAmount(): Flow<Double?>

    @Query("SELECT SUM(remainingAmount) FROM debts WHERE type = 'BORROWED' AND isSettled = 0")
    fun getTotalBorrowedAmount(): Flow<Double?>
}