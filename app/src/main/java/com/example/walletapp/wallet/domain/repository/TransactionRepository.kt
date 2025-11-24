package com.example.walletapp.wallet.domain.repository

import com.example.walletapp.wallet.domain.model.Transaction
import com.example.walletapp.wallet.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface TransactionRepository {
    suspend fun saveTransaction(transaction: Transaction): Result<Unit>
    suspend fun getTransactionById(id: String): Result<Transaction>
    fun getAllTransactions(type: TransactionType? = null): Flow<List<Transaction>>
    fun getTransactionsByDateRange(startDate: Date, endDate: Date): Flow<List<Transaction>>
    suspend fun deleteTransaction(transactionId: String): Result<Unit>
}
