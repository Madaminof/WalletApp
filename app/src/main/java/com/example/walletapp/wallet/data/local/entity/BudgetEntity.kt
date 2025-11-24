package com.example.walletapp.wallet.data.local.entity


import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.walletapp.wallet.domain.model.BudgetPeriod

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey
    val id: String,                 // Noyob ID (UUID ishlatish tavsiya etiladi)
    val categoryId: String,         // Kategoriya ID si (Transaction jadvali bilan bog'lash uchun)
    val maxAmount: Double,          // Belgilangan maksimal summa
    val period: BudgetPeriod,       // Enum: MONTHLY, WEEKLY, CUSTOM
    val startDate: Long,            // Budjet boshlangan sana (Millisekund)
    val endDate: Long? = null,      // Agar CUSTOM bo'lsa tugash sanasi
    val isActive: Boolean = true
)

/**
 * Budget va unga bog'liq Category ma'lumotlarini o'z ichiga oladi.
 * Budjetlarni domen ob'ektiga aylantirish uchun Category ma'lumotlari Room tomonidan avtomatik yuklanadi.
 */
data class BudgetWithCategory(
    @Embedded
    val budget: BudgetEntity,

    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id" // CategoryEntity ning ID si
    )
    val category: CategoryEntity?
)