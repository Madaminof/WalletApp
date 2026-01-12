package dev.samandar.walletapp.wallet.data.local.entity


import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import dev.samandar.walletapp.wallet.domain.model.BudgetPeriod

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey
    val id: String,
    val categoryId: String,
    val maxAmount: Double,
    val period: BudgetPeriod,
    val startDate: Long,
    val endDate: Long? = null,
    val isActive: Boolean = true,
    val createdAt: Long,
)
data class BudgetWithCategory(
    @Embedded
    val budget: BudgetEntity,

    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: CategoryEntity?
)