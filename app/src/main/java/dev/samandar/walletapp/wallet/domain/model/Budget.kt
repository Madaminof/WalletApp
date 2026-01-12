package dev.samandar.walletapp.wallet.domain.model

enum class BudgetPeriod {
    MONTHLY,
    WEEKLY,
    RANGE
}

data class Budget(
    val id: String,
    val category: Category,
    val maxAmount: Double,
    val period: BudgetPeriod,
    val startDate: Long,
    val endDate: Long? = null,
    val isActive: Boolean = true,
    val createdAt: Long
)

data class BudgetStatus(
    val budget: Budget,
    val spentAmount: Double,
    val remainingAmount: Double,
    val percentageUsed: Double,
    val isOverBudget: Boolean,
    val daysRemaining: Int,
    val dailyLimit: Double = if (daysRemaining > 0 && remainingAmount > 0) {
        remainingAmount / daysRemaining
    } else {
        0.0
    }
)