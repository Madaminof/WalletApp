package dev.samandar.walletapp.wallet.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.samandar.walletapp.wallet.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    fun getTransactionsByType(type: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionEntityById(id: String): TransactionEntity?

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: String)

    @Query("""
        SELECT COUNT(id) FROM transactions 
        WHERE date >= :startDateMillis 
        AND date < :endDateMillis
    """)
    suspend fun countTransactionsByDateRange(
        startDateMillis: Long,
        endDateMillis: Long
    ): Int



    @Query("""
        SELECT * FROM transactions 
        WHERE (:catId IS NULL OR categoryId = :catId)
        AND (:accId IS NULL OR accountId = :accId)
        AND (date BETWEEN :start AND :end)
        ORDER BY date DESC
    """)
    suspend fun getFilteredTransactions(
        start: Long,
        end: Long,
        catId: String?,
        accId: String?
    ): List<TransactionEntity>


    @Query("SELECT * FROM transactions")
    suspend fun getAllTransactionsOnce(): List<TransactionEntity>

    @Update
    suspend fun updateTransactions(transactions: List<TransactionEntity>)


}