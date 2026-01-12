package dev.samandar.walletapp.wallet.data.repository

import dev.samandar.walletapp.utils.EMPTY
import dev.samandar.walletapp.utils.orZero
import dev.samandar.walletapp.wallet.data.local.dao.budjetDao.BudgetDao
import dev.samandar.walletapp.wallet.data.local.dao.budjetDao.BudjetTransactionDao
import dev.samandar.walletapp.wallet.data.local.entity.BudgetWithCategory
import dev.samandar.walletapp.wallet.data.mapper.toDomain
import dev.samandar.walletapp.wallet.data.mapper.toEntity
import dev.samandar.walletapp.wallet.domain.model.Budget
import dev.samandar.walletapp.wallet.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BudgetRepositoryImpl @Inject constructor(
    private val budgetDao: BudgetDao,
    private val budjetTransactionDao: BudjetTransactionDao,
) : BudgetRepository {

    override suspend fun saveBudget(budget: Budget) {
        budgetDao.insertBudget(budget.toEntity())
    }

    override suspend fun deleteBudjet(budget: Budget) {
        budgetDao.deleteBudget(budgetId = budget.id)
    }

    override suspend fun updateBudget(budget: Budget) {
        budgetDao.updateBudget(budget.toEntity())
    }

    override fun getBudgetById(budgetId: String): Flow<BudgetWithCategory?> {
        return budgetDao.getBudgetById(budgetId)
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
        endDate: Long,
    ): Flow<Double> {
        return budjetTransactionDao.getSumOfExpensesForBudget(categoryId, startDate, endDate)
            .map { sum -> sum.orZero }
    }
}