package dev.samandar.walletapp.wallet.presentation.ui.charts.tabRowComponents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.domain.model.Account
import dev.samandar.walletapp.wallet.domain.model.Transaction
import dev.samandar.walletapp.wallet.domain.model.TransactionType
import dev.samandar.walletapp.wallet.domain.usecase.account.GetAllAccounts
import dev.samandar.walletapp.wallet.domain.usecase.transaction.GetAllTransactions
import dev.samandar.walletapp.wallet.presentation.ui.home.cardStatistics.TimePeriod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import kotlin.math.abs
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.annotation.StringRes
import android.app.Application


data class BalanceReportState(
    val accounts: List<Account> = emptyList(),
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedFilter: TimePeriod = TimePeriod.Weekly,
    val periodLabel: String = "",
    val totalBalance: Double = 0.0,
    val trendPercentage: Double = 0.0,
    val trendData: List<BalancePoint> = emptyList()
)

data class BalancePoint(
    val date: Long,
    val amount: Double
)

@HiltViewModel
class BalanceTabViewModel @Inject constructor(
    private val getAllAccountsUseCase: GetAllAccounts,
    private val getAllTransactionsUseCase: GetAllTransactions,
    private val application: Application
) : ViewModel() {

    private val _balanceState = MutableStateFlow(BalanceReportState(isLoading = true))
    val balanceState: StateFlow<BalanceReportState> = _balanceState.asStateFlow()

    private fun getString(@StringRes resId: Int): String {
        return application.getString(resId)
    }

    init {
        collectData()
    }

    private fun resetCalendarToStartOfDay(date: Date = Date()): Calendar {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar
    }

    private fun collectData() {
        viewModelScope.launch {
            combine(
                getAllAccountsUseCase(),
                getAllTransactionsUseCase()
            ) { accounts, transactions ->

                _balanceState.update { currentState ->
                    val (periodLabel, periodStart, periodEnd) = getPeriodBoundaries(
                        currentState.selectedFilter,
                        transactions
                    )

                    val trendData = generateBalanceTrendByPeriod(
                        accounts,
                        transactions,
                        periodStart,
                        periodEnd
                    )

                    // ✅ OPTIMAL: totalBalance endi davrdagi Net o'zgarishni hisoblaydi
                    val currentTotalBalance = calculateNetBalanceForPeriod(transactions, periodStart, periodEnd)
                    val trendPercentage = calculateTrendPercentage(trendData)

                    currentState.copy(
                        accounts = accounts,
                        transactions = transactions,
                        isLoading = false,
                        totalBalance = currentTotalBalance, // Net o'zgarish
                        trendData = trendData,
                        trendPercentage = trendPercentage,
                        periodLabel = periodLabel,
                        error = null
                    )
                }
            }
                .catch { exception ->
                    _balanceState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.localizedMessage ?: getString(R.string.unknown)
                        )
                    }
                }
                .collect { }
        }
    }

    /**
     * Berilgan davrdagi tranzaksiyalarning sof balansini (daromad - xarajat) hisoblaydi.
     */
    private fun calculateNetBalanceForPeriod(
        transactions: List<Transaction>,
        periodStart: Long,
        periodEnd: Long
    ): Double {
        return transactions
            .filter { it.date in periodStart..periodEnd }
            .sumOf { tx ->
                if (tx.type == TransactionType.INCOME) tx.amount else -tx.amount
            }
    }

    /**
     * Davr chegaralarini (start/end) va labelni hisoblaydi.
     */
    private fun getPeriodBoundaries(
        filter: TimePeriod,
        transactions: List<Transaction>
    ): Triple<String, Long, Long> {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val periodEnd = Date().time

        // Bu Kalendar faqat vaqt o'lchamini orqaga surish uchun ishlatiladi, joriy kundan boshlab.
        val startCalendar = resetCalendarToStartOfDay()

        val periodStart: Long = when (filter) {
            TimePeriod.Daily -> startCalendar.timeInMillis
            TimePeriod.Weekly -> startCalendar.apply { add(Calendar.DAY_OF_YEAR, -7) }.timeInMillis
            TimePeriod.Monthly -> startCalendar.apply { add(Calendar.MONTH, -1) }.timeInMillis
            TimePeriod.Year -> startCalendar.apply { add(Calendar.YEAR, -1) }.timeInMillis
            TimePeriod.AllTime -> transactions.minOfOrNull { it.date } ?: startCalendar.timeInMillis
        }

        val periodLabel = when (filter) {
            TimePeriod.Daily -> dateFormat.format(Date(periodStart))
            TimePeriod.AllTime -> getString(R.string.filter_all)
            else -> "${dateFormat.format(Date(periodStart))} - ${dateFormat.format(Date(periodEnd))}"
        }

        return Triple(periodLabel, periodStart, periodEnd)
    }

    private fun generateBalanceTrendByPeriod(
        accounts: List<Account>,
        transactions: List<Transaction>,
        periodStart: Long,
        periodEnd: Long
    ): List<BalancePoint> {

        // Agar hisoblar bo'lmasa yoki tranzaksiya bo'lmasa, nol balans bilan kamida 2 nuqta qaytaramiz
        if (accounts.isEmpty() && transactions.isEmpty()) {
            return listOf(
                BalancePoint(date = periodStart, amount = 0.0),
                BalancePoint(date = periodEnd, amount = 0.0)
            )
        }

        val initialBalance = accounts.sumOf { it.initialBalance }
        val sortedTransactions = transactions.sortedBy { it.date }

        var startBalance = initialBalance
        // Davr boshlanishidan oldingi balansni hisoblash
        sortedTransactions.filter { it.date < periodStart }.forEach { tx ->
            startBalance += if (tx.type == TransactionType.INCOME) tx.amount else -tx.amount
        }

        val balanceTrend = mutableListOf<BalancePoint>()
        // 1. Birinchi nuqta: Davr boshlanishidagi hisoblangan balans
        balanceTrend.add(BalancePoint(date = periodStart, amount = startBalance))

        var runningBalance = startBalance

        // Davr ichidagi tranzaksiyalar
        val transactionsInPeriod = sortedTransactions.filter { it.date in periodStart..periodEnd }

        transactionsInPeriod.forEach { tx ->
            val amountChange = if (tx.type == TransactionType.INCOME) tx.amount else -tx.amount
            runningBalance += amountChange

            if (balanceTrend.lastOrNull()?.date == tx.date) {
                balanceTrend[balanceTrend.lastIndex] = balanceTrend.last().copy(amount = runningBalance)
            } else {
                balanceTrend.add(BalancePoint(date = tx.date, amount = runningBalance))
            }
        }

        // 2. Yakuniy nuqta: Agar oxirgi nuqta periodEnd dan oldin bo'lsa, uni qo'shamiz
        if (balanceTrend.lastOrNull()?.date?.let { it < periodEnd } == true || balanceTrend.size == 1) {
            balanceTrend.add(BalancePoint(date = periodEnd, amount = runningBalance))
        }

        // ✅ MUHIM TEKSHIRUV: Agar faqat 1 ta nuqta bo'lsa (kun ichida tranzaksiya yo'q bo'lsa),
        // grafik chizilishi uchun uni 2 taga ko'paytiramiz (davr oxirida ham xuddi shu balans)
        if (balanceTrend.size == 1) {
            balanceTrend.add(BalancePoint(date = periodEnd, amount = runningBalance))
        }

        // Qaytarishdan oldin takrorlanishlarni olib tashlash va tartiblash
        return balanceTrend.distinctBy { it.date }.sortedBy { it.date }
    }


    private fun calculateTrendPercentage(trendData: List<BalancePoint>): Double {
        if (trendData.size < 2) return 0.0

        val initialBalance = trendData.first().amount
        val finalBalance = trendData.last().amount

        if (abs(initialBalance) < 0.0001) {
            return if (abs(finalBalance) < 0.0001) 0.0 else 100.0
        }

        val change = finalBalance - initialBalance
        return (change / abs(initialBalance)) * 100.0
    }

    fun onFilterChange(newPeriod: TimePeriod) {
        _balanceState.update { currentState ->
            if (currentState.selectedFilter == newPeriod) return@update currentState

            // Hamma tranzaksiyalar olinadi
            val allTransactions = currentState.transactions
            val allAccounts = currentState.accounts

            // Yangi davr chegaralarini hisoblash
            val (periodLabel, periodStart, periodEnd) = getPeriodBoundaries(
                newPeriod,
                allTransactions
            )

            // Trend ma'lumotlarini qayta hisoblash
            val trendData = generateBalanceTrendByPeriod(
                allAccounts,
                allTransactions,
                periodStart,
                periodEnd
            )

            // ✅ Davrdagi Net Balansni hisoblash
            val currentTotalBalance = calculateNetBalanceForPeriod(allTransactions, periodStart, periodEnd)
            val trendPercentage = calculateTrendPercentage(trendData)

            currentState.copy(
                selectedFilter = newPeriod,
                periodLabel = periodLabel,
                trendData = trendData,
                totalBalance = currentTotalBalance, // Endi bu Net o'zgarish
                trendPercentage = trendPercentage
            )
        }
    }
}