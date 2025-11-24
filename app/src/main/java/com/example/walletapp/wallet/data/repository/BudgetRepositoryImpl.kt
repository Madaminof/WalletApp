package com.example.walletapp.wallet.data.repository

import com.example.walletapp.wallet.data.local.dao.budjetDao.BudjetTransactionDao
import com.example.walletapp.wallet.data.local.dao.budjetDao.BudgetDao
import com.example.walletapp.wallet.data.mapper.toDomain
import com.example.walletapp.wallet.data.mapper.toEntity
import com.example.walletapp.wallet.domain.model.Budget
import com.example.walletapp.wallet.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BudgetRepositoryImpl @Inject constructor(
    private val budgetDao: BudgetDao,
    private val budjetTransactionDao: BudjetTransactionDao
) : BudgetRepository {

    override suspend fun saveBudget(budget: Budget) {
        budgetDao.insertBudget(budget.toEntity())
    }

    override suspend fun deleteBudjet(budget: Budget) {
        budgetDao.deleteBudget(budgetId = budget.id)
    }

    override fun getActiveBudgets(): Flow<List<Budget>> {
        return budgetDao.getBudgetsByActiveStatus(true)
            .map { budgetWithCategories ->
                budgetWithCategories.mapNotNull { it.toDomain() }
            }
    }
    override fun getTotalSpentForBudget(
        categoryId: String,
        startDate: Long,
        endDate: Long
    ): Flow<Double> {
        return budjetTransactionDao.getSumOfExpensesForBudget(categoryId, startDate, endDate)
            .map { sum -> sum ?: 0.0 }
    }
}