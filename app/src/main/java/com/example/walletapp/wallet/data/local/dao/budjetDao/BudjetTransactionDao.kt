package com.example.walletapp.wallet.data.local.dao.budjetDao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BudjetTransactionDao{

    @Query("""
        SELECT SUM(amount) FROM `transactions` 
        WHERE categoryId = :categoryId 
        AND type = 'EXPENSE' 
        AND date BETWEEN :startDate AND :endDate
    """)
    fun getSumOfExpensesForBudget(
        categoryId: String,
        startDate: Long,
        endDate: Long
    ): Flow<Double?>
}
