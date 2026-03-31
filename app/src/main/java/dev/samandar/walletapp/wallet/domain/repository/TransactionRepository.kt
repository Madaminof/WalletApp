package dev.samandar.walletapp.wallet.domain.repository

import dev.samandar.walletapp.wallet.data.local.entity.TransactionEntity
import dev.samandar.walletapp.wallet.domain.model.Transaction
import dev.samandar.walletapp.wallet.domain.model.TransactionType
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.data.ExportConfig
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    suspend fun saveTransaction(transaction: Transaction): Result<Unit>
    suspend fun getTransactionById(id: String): Result<Transaction>
    fun getAllTransactions(type: TransactionType? = null): Flow<List<Transaction>>
    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<Transaction>>
    suspend fun deleteTransaction(transactionId: String): Result<Unit>
    suspend fun updateTransaction(transaction: Transaction): Result<Unit>
    suspend fun countTransactionsByDateRange(startDateMillis: Long, endDateMillis: Long): Int

    suspend fun getFilteredTransactions(
        config: ExportConfig
    ): List<TransactionEntity>

    suspend fun getAllTransactionsOnce(): List<Transaction>

    // 2. Ommaviy update (Batch update)
    suspend fun updateTransactions(transactions: List<Transaction>): Result<Unit>
}
