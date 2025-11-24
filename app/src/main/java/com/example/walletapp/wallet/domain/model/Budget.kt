package com.example.walletapp.wallet.domain.model

enum class BudgetPeriod {
    MONTHLY,
    WEEKLY,
    CUSTOM
}

data class Budget(
    val id: String,
    val category: Category,
    val maxAmount: Double,
    val period: BudgetPeriod,
    val startDate: Long,
    val endDate: Long? = null,
    val isActive: Boolean = true
)

data class BudgetStatus(
    val budget: Budget,
    val spentAmount: Double,
    val remainingAmount: Double,
    val percentageUsed: Double,
    val isOverBudget: Boolean,
    val daysRemaining: Int         
)