package dev.samandar.walletapp.wallet.data.local.dao.budjetDao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.samandar.walletapp.wallet.data.local.entity.BudgetEntity
import dev.samandar.walletapp.wallet.data.local.entity.BudgetWithCategory
import kotlinx.coroutines.flow.Flow


@Dao
interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity)

    @Query("SELECT * FROM budgets WHERE isActive = :isActive")
    fun getBudgetsByActiveStatus(isActive: Boolean): Flow<List<BudgetWithCategory>>

    @Query("DELETE FROM budgets WHERE id = :budgetId")
    suspend fun deleteBudget(budgetId: String)

    @Update
    suspend fun updateBudget(budget: BudgetEntity)

    @Query("UPDATE budgets SET isActive = 0 WHERE id = :budgetId")
    suspend fun deactivateBudget(budgetId: String)

    @Query("SELECT * FROM budgets WHERE id = :budgetId")
    fun getBudgetById(budgetId: String): Flow<BudgetWithCategory?>
}