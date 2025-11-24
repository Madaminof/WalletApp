package com.example.walletapp.wallet.domain.repository

import com.example.walletapp.wallet.domain.model.Budget
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {

    suspend fun saveBudget(budget: Budget)
    suspend fun deleteBudjet(budget: Budget)
    fun getActiveBudgets(): Flow<List<Budget>>
    fun getTotalSpentForBudget(
        categoryId: String,
        startDate: Long,
        endDate: Long
    ): Flow<Double>
}