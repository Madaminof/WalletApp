package dev.samandar.walletapp.wallet.data.mapper

import dev.samandar.walletapp.wallet.data.local.entity.BudgetEntity
import dev.samandar.walletapp.wallet.data.local.entity.BudgetWithCategory
import dev.samandar.walletapp.wallet.data.local.entity.CategoryEntity
import dev.samandar.walletapp.wallet.domain.model.Budget
import dev.samandar.walletapp.wallet.domain.model.Category
import dev.samandar.walletapp.wallet.domain.model.TransactionType

fun Budget.toEntity(): BudgetEntity {
    return BudgetEntity(
        id = this.id,
        categoryId = this.category.id,
        maxAmount = this.maxAmount,
        period = this.period,
        startDate = this.startDate,
        endDate = this.endDate,
        isActive = this.isActive,
        createdAt = this.createdAt,
    )
}

fun BudgetWithCategory.toDomain(): Budget? {
    return this.category?.let {
        Budget(
            id = this.budget.id,
            category = it.toDomain(),
            maxAmount = this.budget.maxAmount,
            period = this.budget.period,
            startDate = this.budget.startDate,
            endDate = this.budget.endDate,
            isActive = this.budget.isActive,
            createdAt = this.budget.createdAt
        )
    }
}
fun CategoryEntity.toDomain(): Category {
    return Category(
        id = this.id,
        name = this.name,
        type = TransactionType.valueOf(this.type),
        iconResId = this.iconResId,
        colorArgb = this.colorArgb
    )
}
