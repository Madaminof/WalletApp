package com.example.walletapp.wallet.presentation.ui.charts.tabRowComponents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletapp.wallet.domain.model.Account
import com.example.walletapp.wallet.domain.model.Transaction
import com.example.walletapp.wallet.domain.model.TransactionType
import com.example.walletapp.wallet.domain.usecase.account.GetAllAccounts
import com.example.walletapp.wallet.domain.usecase.transaction.GetAllTransactions
import com.example.walletapp.wallet.presentation.ui.home.cardStatistics.TimePeriod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.abs
import java.text.SimpleDateFormat
import java.util.Locale

data class BalanceReportState(
    val accounts: List<Account> = emptyList(),
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedFilter: String = "Week",
    val periodLabel: String = "Oxirgi 7 kun",
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
    private val getAllTransactionsUseCase: GetAllTransactions
) : ViewModel() {

    private val _balanceState = MutableStateFlow(BalanceReportState(isLoading = true))
    val balanceState: StateFlow<BalanceReportState> = _balanceState.asStateFlow()

    private val _selectedPeriod = MutableStateFlow<TimePeriod>(TimePeriod.Monthly)
    val selectedPeriod: StateFlow<TimePeriod> = _selectedPeriod.asStateFlow()


    private val trendCalendar = Calendar.getInstance()

    init {
        // Dastlabki yuklanish (Week)
        resetTrendCalendarToStartOfDay()
        trendCalendar.add(Calendar.DAY_OF_YEAR, -7)
        collectData()
    }

    private fun resetTrendCalendarToStartOfDay() {
        trendCalendar.time = Date()
        trendCalendar.set(Calendar.HOUR_OF_DAY, 0)
        trendCalendar.set(Calendar.MINUTE, 0)
        trendCalendar.set(Calendar.SECOND, 0)
        trendCalendar.set(Calendar.MILLISECOND, 0)
    }

    private fun collectData() {
        viewModelScope.launch {
            combine(
                getAllAccountsUseCase(),
                getAllTransactionsUseCase()
            ) { accounts, transactions ->

                _balanceState.update { currentState ->

                    val (periodLabel, trendData) = calculateTrendDataForCurrentPeriod(
                        currentState.selectedFilter,
                        accounts,
                        transactions
                    )

                    // TUZATISH: totalBalance endi trendData ning eng oxirgi nuqtasidan olinadi.
                    val currentTotalBalance = trendData.lastOrNull()?.amount ?: 0.0
                    val trendPercentage = calculateTrendPercentage(trendData)

                    currentState.copy(
                        accounts = accounts,
                        transactions = transactions,
                        isLoading = false,
                        totalBalance = currentTotalBalance,
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
                            error = exception.localizedMessage ?: "Noma'lum xato"
                        )
                    }
                }
                .collect { }
        }
    }

    private fun calculateTrendDataForCurrentPeriod(
        filter: String,
        accounts: List<Account>,
        transactions: List<Transaction>
    ): Pair<String, List<BalancePoint>> {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val periodLabel: String
        val periodStart: Long
        val periodEnd: Long

        if (filter == "All") {
            periodStart = transactions.minOfOrNull { it.date } ?: Date().time
            periodEnd = Date().time
            periodLabel = "Barcha davr"
        } else {
            val periodStartCalendar = trendCalendar.clone() as Calendar

            // Period tugash sanasini aniqlash. Agar navigatsiya ishlatilmagan bo'lsa (onFilterChange),
            // bu yerda trendCalendar 7 kun orqada turadi (Week uchun).
            // periodEnd har doim periodStart + filtr uzunligi bo'lishi kerak.
            val periodEndCalendar = periodStartCalendar.clone() as Calendar

            when (filter) {
                // 'Day' filtri trendCalendar 00:00:00 da turganini anglatadi. Tugash sanasi 1 kun keyin bo'ladi (ertasi kuni 00:00:00).
                "Day" -> periodEndCalendar.add(Calendar.DAY_OF_YEAR, 1)
                "Week" -> periodEndCalendar.add(Calendar.DAY_OF_YEAR, 7)
                "Month" -> periodEndCalendar.add(Calendar.MONTH, 1)
                "Year" -> periodEndCalendar.add(Calendar.YEAR, 1)
            }

            periodStart = periodStartCalendar.time.time
            // periodEnd: Agar Day bo'lsa, ertasi kun 00:00 ga o'tamiz. Boshqa holatlarda ham period oxiri.
            periodEnd = periodEndCalendar.time.time

            val startDateString = dateFormat.format(periodStartCalendar.time)

            // Joriy kunni to'g'ri ko'rsatish uchun Day filtri uchun faqat bitta sana
            periodLabel = if (filter == "Day" && periodEndCalendar.time.after(Date())) {
                dateFormat.format(Date()) // Agar Day filtri hozirgi davrni qamrab olsa, bugungi sanani ko'rsatish
            } else {
                "${startDateString} - ${dateFormat.format(periodEndCalendar.time)}"
            }
        }

        // Asosiy Balans Trendini hisoblash
        var trendData = generateBalanceTrendByPeriod(
            accounts,
            transactions,
            periodStart = periodStart,
            periodEnd = periodEnd
        )

        // !!! GRAFIK XATOSINI TUZATISH MANTIQI !!!
        val maxAmount = trendData.maxOfOrNull { it.amount } ?: 0.0
        val minAmount = trendData.minOfOrNull { it.amount } ?: 0.0

        trendData = when {
            // 1. Diapazon nolga teng bo'lsa
            abs(maxAmount - minAmount) < 0.0001 && trendData.size > 1 -> {
                trendData.mapIndexed { index, point ->
                    when (index) {
                        0 -> point.copy(amount = point.amount - 0.01)
                        trendData.lastIndex -> point.copy(amount = point.amount + 0.01)
                        else -> point
                    }
                }
            }
            // 2. Faqat bitta nuqta bo'lsa
            trendData.size == 1 -> {
                val point = trendData.first()
                listOf(
                    point.copy(date = point.date - TimeUnit.HOURS.toMillis(1), amount = point.amount - 0.01),
                    point.copy(date = point.date + TimeUnit.HOURS.toMillis(1), amount = point.amount + 0.01)
                )
            }
            else -> trendData
        }

        return Pair(periodLabel, trendData)
    }

    private fun generateBalanceTrendByPeriod(
        accounts: List<Account>,
        transactions: List<Transaction>,
        periodStart: Long,
        periodEnd: Long
    ): List<BalancePoint> {
        if (accounts.isEmpty()) return listOf(BalancePoint(Date().time, 0.0))

        val initialBalance = accounts.sumOf { it.initialBalance }
        val sortedTransactions = transactions.sortedBy { it.date }

        // 1. Period boshlanishidan oldingi balansni hisoblash (startBalance)
        var startBalance = initialBalance
        sortedTransactions.filter { it.date < periodStart }.forEach { tx ->
            startBalance += if (tx.type == TransactionType.INCOME) tx.amount else -tx.amount
        }

        val balanceTrend = mutableListOf<BalancePoint>()
        // Boshlanish nuqtasini qo'shish
        balanceTrend.add(BalancePoint(date = periodStart, amount = startBalance))

        // 2. Period davridagi balans o'zgarishlarini qo'shish
        var runningBalance = startBalance

        // Agar Day filtri tanlangan bo'lsa, tugash nuqtasi hozirgi vaqt bo'lishi kerak (periodEnd emas)
        val actualEnd = if (_balanceState.value.selectedFilter == "Day" && periodEnd > Date().time) {
            Date().time
        } else {
            periodEnd
        }

        val transactionsInPeriod = sortedTransactions.filter { it.date in periodStart..actualEnd }

        transactionsInPeriod.forEach { tx ->
            val amountChange = if (tx.type == TransactionType.INCOME) tx.amount else -tx.amount
            runningBalance += amountChange

            // Xuddi shu sanadagi oxirgi nuqtani yangilaymiz
            if (balanceTrend.lastOrNull()?.date == tx.date) {
                balanceTrend[balanceTrend.lastIndex] = balanceTrend.last().copy(amount = runningBalance)
            } else {
                balanceTrend.add(BalancePoint(date = tx.date, amount = runningBalance))
            }
        }

        // 3. Trendni yakuniy vaqtdagi balans bilan tugatish
        // Yakuniy nuqta actualEnd bo'lishi kerak.
        if (balanceTrend.lastOrNull()?.date?.let { it < actualEnd } == true || balanceTrend.isEmpty()) {
            val finalBalance = runningBalance
            balanceTrend.add(BalancePoint(date = actualEnd, amount = finalBalance))
        } else if (balanceTrend.size == 1) {
            // Faqat bitta nuqta bo'lsa, ikkinchi nuqtani qo'shamiz
            balanceTrend.add(BalancePoint(date = actualEnd, amount = runningBalance))
        }


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

    /**
     * Filtr o'zgarganda kalendar va trend ma'lumotlarini yangilash.
     */
    fun onFilterChange(newFilter: String) {
        _balanceState.update { currentState ->

            // 1. Kalendarni nolga (joriy kunga) qaytarish
            resetTrendCalendarToStartOfDay()

            // 2. Trend boshlanish sanasini belgilash
            when (newFilter) {
                // Day uchun, bugungi kun 00:00:00 da qoladi. trendData da hozirgi vaqtgacha hisoblanadi.
                "Week" -> trendCalendar.add(Calendar.DAY_OF_YEAR, -7)
                "Month" -> trendCalendar.add(Calendar.MONTH, -1)
                "Year" -> trendCalendar.add(Calendar.YEAR, -1)
                "All" -> {}
            }

            // 3. Yangi ma'lumotlarni hisoblash
            val (periodLabel, trendData) = calculateTrendDataForCurrentPeriod(
                newFilter,
                currentState.accounts,
                currentState.transactions
            )

            val currentTotalBalance = trendData.lastOrNull()?.amount ?: 0.0
            val trendPercentage = calculateTrendPercentage(trendData)

            currentState.copy(
                selectedFilter = newFilter,
                periodLabel = periodLabel,
                trendData = trendData,
                totalBalance = currentTotalBalance, // TUZATILDI
                trendPercentage = trendPercentage
            )
        }
    }
}