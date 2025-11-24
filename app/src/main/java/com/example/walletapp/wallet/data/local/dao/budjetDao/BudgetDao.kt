package com.example.walletapp.wallet.data.local.dao.budjetDao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.walletapp.wallet.data.local.entity.BudgetEntity
import com.example.walletapp.wallet.data.local.entity.BudgetWithCategory
import kotlinx.coroutines.flow.Flow


@Dao
interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity)

    @Query("SELECT * FROM budgets WHERE isActive = :isActive")
    fun getBudgetsByActiveStatus(isActive: Boolean): Flow<List<BudgetWithCategory>>
    @Query("DELETE FROM budgets WHERE id = :budgetId")
    suspend fun deleteBudget(budgetId: String)

    @Query("UPDATE budgets SET isActive = 0 WHERE id = :budgetId")
    suspend fun deactivateBudget(budgetId: String)
}