package com.example.walletapp.wallet.domain.repository

import com.example.walletapp.wallet.domain.model.Transaction
import com.example.walletapp.wallet.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    suspend fun saveTransaction(transaction: Transaction): Result<Unit>
    suspend fun getTransactionById(id: String): Result<Transaction>
    fun getAllTransactions(type: TransactionType? = null): Flow<List<Transaction>>
    fun getTransactionsByDateRange(startDate: Long, endDate: Long): Flow<List<Transaction>>
    suspend fun deleteTransaction(transactionId: String): Result<Unit>

    suspend fun countTransactionsByDateRange(startDateMillis: Long, endDateMillis: Long): Int
}
