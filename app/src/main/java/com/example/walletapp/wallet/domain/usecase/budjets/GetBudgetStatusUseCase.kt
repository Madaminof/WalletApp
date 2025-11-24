package com.example.walletapp.wallet.domain.usecase.budjets


import android.os.Build
import androidx.annotation.RequiresApi
import com.example.walletapp.wallet.domain.model.Budget
import com.example.walletapp.wallet.domain.model.BudgetPeriod
import com.example.walletapp.wallet.domain.model.BudgetStatus
import com.example.walletapp.wallet.domain.repository.BudgetRepository
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import kotlin.math.max
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
private val DEFAULT_ZONE_ID = ZoneId.systemDefault()

class GetBudgetStatusUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository
) {
    @RequiresApi(Build.VERSION_CODES.O)
    operator fun invoke(budget: Budget): Flow<BudgetStatus> {
        if (!budget.isActive) {
            return flowOf(
                BudgetStatus(
                    budget = budget,
                    spentAmount = 0.0,
                    remainingAmount = budget.maxAmount,
                    percentageUsed = 0.0,
                    isOverBudget = false,
                    daysRemaining = 0
                )
            )
        }

        val (currentStartDate, currentEndDate) = calculateCurrentPeriod(budget)

        val daysLeft = calculateDaysRemaining(currentEndDate)

        return budgetRepository.getTotalSpentForBudget(
            categoryId = budget.category.id,
            startDate = currentStartDate,
            endDate = currentEndDate
        ).map { spent ->

            val remaining = budget.maxAmount - spent
            val percentage = if (budget.maxAmount > 0) (spent / budget.maxAmount) * 100 else 0.0
            val isOver = remaining < 0

            BudgetStatus(
                budget = budget,
                spentAmount = spent,
                remainingAmount = remaining,
                percentageUsed = percentage.coerceIn(0.0, 100.0),
                isOverBudget = isOver,
                daysRemaining = daysLeft
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateCurrentPeriod(budget: Budget): Pair<Long, Long> {
        val today = LocalDate.now(DEFAULT_ZONE_ID)
        val budgetStartDate = Instant.ofEpochMilli(budget.startDate).atZone(DEFAULT_ZONE_ID).toLocalDate()

        val periodStart: LocalDate
        val periodEnd: LocalDate

        when (budget.period) {
            BudgetPeriod.CUSTOM -> {
                periodStart = budgetStartDate
                periodEnd = budget.endDate?.let { Instant.ofEpochMilli(it).atZone(DEFAULT_ZONE_ID).toLocalDate() } ?: today.plusYears(10)
            }

            BudgetPeriod.MONTHLY -> {
                val dayOfMonth = budgetStartDate.dayOfMonth

                periodStart = if (today.dayOfMonth >= dayOfMonth) {
                    YearMonth.from(today).atDay(max(1, dayOfMonth))
                } else {
                    YearMonth.from(today.minusMonths(1)).atDay(max(1, dayOfMonth))
                }

                periodEnd = periodStart.plusMonths(1).minusDays(1)
            }

            BudgetPeriod.WEEKLY -> {
                val dayOfWeek = budgetStartDate.dayOfWeek

                periodStart = today.with(TemporalAdjusters.previousOrSame(dayOfWeek))

                periodEnd = periodStart.plusDays(6)
            }
        }

        return Pair(
            periodStart.atStartOfDay(DEFAULT_ZONE_ID).toInstant().toEpochMilli(),
            periodEnd.atStartOfDay(DEFAULT_ZONE_ID).toInstant().toEpochMilli()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateDaysRemaining(endDateMillis: Long): Int {
        val today = LocalDate.now(DEFAULT_ZONE_ID)
        val endDate = Instant.ofEpochMilli(endDateMillis).atZone(DEFAULT_ZONE_ID).toLocalDate()

        if (endDate.isBefore(today)) {
            return 0
        }

        val days = ChronoUnit.DAYS.between(today, endDate)

        return days.toInt()
    }
}