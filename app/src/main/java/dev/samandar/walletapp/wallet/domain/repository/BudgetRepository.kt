package dev.samandar.walletapp.wallet.domain.repository

import dev.samandar.walletapp.wallet.data.local.entity.BudgetWithCategory
import dev.samandar.walletapp.wallet.domain.model.Budget
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {

    suspend fun saveBudget(budget: Budget)
    suspend fun updateBudget(budget: Budget)
    suspend fun deleteBudjet(budget: Budget)
    fun getBudgetById(budgetId: String): Flow<BudgetWithCategory?>
    fun getActiveBudgets(): Flow<List<Budget>>
    fun getTotalSpentForBudget(
        categoryId: String,
        startDate: Long,
        endDate: Long
    ): Flow<Double>

    suspend fun getAllBudgetsOnce(): List<Budget>
    suspend fun updateBudgets(budgets: List<Budget>): Result<Unit>
}