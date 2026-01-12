package dev.samandar.walletapp.wallet.domain.usecase.budjets


import android.os.Build
import androidx.annotation.RequiresApi
import dev.samandar.walletapp.wallet.domain.model.Budget
import dev.samandar.walletapp.wallet.domain.model.BudgetPeriod
import dev.samandar.walletapp.wallet.domain.model.BudgetStatus
import dev.samandar.walletapp.wallet.domain.repository.BudgetRepository
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

@RequiresApi(Build.VERSION_CODES.O)
class GetBudgetStatusUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository
) {
    operator fun invoke(budget: Budget): Flow<BudgetStatus> {
        if (!budget.isActive) {
            return flowOf(createEmptyStatus(budget))
        }

        val (currentStartDate, currentEndDate) = calculateCurrentPeriod(budget)
        val daysLeft = calculateDaysRemaining(currentEndDate)

        return budgetRepository.getTotalSpentForBudget(
            categoryId = budget.category.id,
            startDate = currentStartDate,
            endDate = currentEndDate
        ).map { spent ->
            val remaining = (budget.maxAmount - spent).coerceAtLeast(0.0)
            val percentage = if (budget.maxAmount > 0) (spent / budget.maxAmount * 100) else 0.0

            // Aqlli Kunlik Limit hisoblash
            // Agar byudjet oshib ketgan bo'lsa yoki kun tugagan bo'lsa 0.0 qaytaradi
            val dailyLimit = if (daysLeft > 0 && spent < budget.maxAmount) {
                remaining / daysLeft
            } else 0.0

            BudgetStatus(
                budget = budget,
                spentAmount = spent,
                remainingAmount = remaining,
                percentageUsed = percentage.coerceIn(0.0, 100.0),
                isOverBudget = spent > budget.maxAmount,
                daysRemaining = daysLeft,
                dailyLimit = dailyLimit // Buni Modelga qo'shishni unutmang
            )
        }
    }

    private fun calculateCurrentPeriod(budget: Budget): Pair<Long, Long> {
        val today = LocalDate.now(DEFAULT_ZONE_ID)

        val periodStart: LocalDate
        val periodEnd: LocalDate

        when (budget.period) {
            BudgetPeriod.MONTHLY -> {
                // Kalendar oyi boshidan oxirigacha
                periodStart = today.with(TemporalAdjusters.firstDayOfMonth())
                periodEnd = today.with(TemporalAdjusters.lastDayOfMonth())
            }

            BudgetPeriod.WEEKLY -> {
                // Dushanbadan Yakshanbagacha
                periodStart = today.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))
                periodEnd = periodStart.plusDays(6)
            }

            BudgetPeriod.RANGE -> {
                periodStart = Instant.ofEpochMilli(budget.startDate).atZone(DEFAULT_ZONE_ID).toLocalDate()
                periodEnd = budget.endDate?.let {
                    Instant.ofEpochMilli(it).atZone(DEFAULT_ZONE_ID).toLocalDate()
                } ?: today.plusYears(1)
            }
        }

        return Pair(
            periodStart.atStartOfDay(DEFAULT_ZONE_ID).toInstant().toEpochMilli(),
            periodEnd.atTime(23, 59, 59).atZone(DEFAULT_ZONE_ID).toInstant().toEpochMilli()
        )
    }

    private fun calculateDaysRemaining(endDateMillis: Long): Int {
        val today = LocalDate.now(DEFAULT_ZONE_ID)
        val endDate = Instant.ofEpochMilli(endDateMillis).atZone(DEFAULT_ZONE_ID).toLocalDate()

        // Bugunni ham hisobga olish uchun +1 qo'shamiz
        return if (!endDate.isBefore(today)) {
            (ChronoUnit.DAYS.between(today, endDate).toInt() + 1)
        } else 0
    }

    private fun createEmptyStatus(budget: Budget) = BudgetStatus(
        budget = budget,
        spentAmount = 0.0,
        remainingAmount = budget.maxAmount,
        percentageUsed = 0.0,
        isOverBudget = false,
        daysRemaining = 0,
        dailyLimit = 0.0
    )
}