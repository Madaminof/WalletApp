package dev.samandar.walletapp.wallet.presentation.ui.home.totalBalanceCard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.samandar.walletapp.R
import dev.samandar.walletapp.utils.FilterKeys
import dev.samandar.walletapp.wallet.domain.model.Account
import dev.samandar.walletapp.wallet.domain.model.Transaction
import dev.samandar.walletapp.wallet.domain.model.TransactionType
import dev.samandar.walletapp.wallet.domain.usecase.account.GetAllAccounts
import dev.samandar.walletapp.wallet.domain.usecase.transaction.GetAllTransactions
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

// 1. Ma'lumot modellari
data class ChartPoint(
    val label: String,
    val value: Double
)

data class TotalBalanceUiStatePremium(
    val netBalance: Double = 0.0,
    val periodBalance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val periodLabel: String = "",
    val chartPoints: List<ChartPoint> = emptyList(), // BarChartItem emas, ChartPoint bo'lishi kerak
    val selectedFilter: String = FilterKeys.MONTH,
    val isLoading: Boolean = true,
    val accounts: List<Account> = emptyList(),
    val selectedAccountIds: Set<String> = emptySet(),
    val isFilterDialogOpen: Boolean = false
)

@HiltViewModel
class TotalBalanceViewModel @Inject constructor(
    private val getAllTransactions: GetAllTransactions,
    private val getAllAccounts: GetAllAccounts,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val filterAllTimeLabel by lazy { context.getString(R.string.filter_all) }

    // StateFlows
    private val _selectedFilter = MutableStateFlow(FilterKeys.MONTH)
    private val _currentPeriodStart = MutableStateFlow(calculatePeriodBounds(Calendar.getInstance(), FilterKeys.MONTH).first)
    private val _accountFilterState = MutableStateFlow(Pair(emptyList<Account>(), emptySet<String>()))
    private val _isFilterDialogOpen = MutableStateFlow(false)

    private val _cardState = MutableStateFlow(TotalBalanceUiStatePremium())
    val cardState: StateFlow<TotalBalanceUiStatePremium> = _cardState.asStateFlow()

    init {
        // 1. Hisoblarni yuklash va boshlang'ich tanlovni o'rnatish
        viewModelScope.launch {
            getAllAccounts().collectLatest { accounts ->
                val initialSelection = accounts.map { it.id }.toSet()
                _accountFilterState.value = Pair(accounts, initialSelection)
            }
        }

        // 2. Barcha oqimlarni birlashtirib ma'lumotni qayta ishlash
        combine(
            getAllTransactions(null), // null - hamma turdagi tranzaksiyalar
            _selectedFilter,
            _currentPeriodStart,
            _accountFilterState,
            _isFilterDialogOpen
        ) { transactions, filter, periodStart, accountPair, isDialogOpen ->

            processBalanceData(transactions, filter, periodStart, accountPair, isDialogOpen)

        }.onEach { newState ->
            _cardState.value = newState.copy(isLoading = false)
        }.launchIn(viewModelScope)
    }

    private fun processBalanceData(
        allTransactions: List<Transaction>,
        filter: String,
        periodStart: Long,
        accountPair: Pair<List<Account>, Set<String>>,
        isDialogOpen: Boolean
    ): TotalBalanceUiStatePremium {
        val (allAccounts, selectedAccountIds) = accountPair
        val (startTime, endTime, label) = calculatePeriodBounds(periodStart, filter)

        // Hisoblar bo'yicha filter
        val accountFilteredTransactions = allTransactions.filter {
            selectedAccountIds.contains(it.account.id)
        }

        // Umumiy statistika (Hamma vaqt uchun)
        val totalInc = accountFilteredTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val totalExp = accountFilteredTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        val netBalance = totalInc - totalExp

        // Tanlangan davrdagi tranzaksiyalar
        val periodTransactions = accountFilteredTransactions.filter { it.date in startTime..endTime }
        val pIncome = periodTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val pExpense = periodTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

        // Professional Grafik: Cumulative Balance (To'planib boruvchi balans)
        val chartPoints = createCumulativeChartData(accountFilteredTransactions, filter, startTime, endTime)

        return TotalBalanceUiStatePremium(
            netBalance = netBalance,
            periodBalance = pIncome - pExpense,
            totalIncome = totalInc,
            totalExpense = totalExp,
            periodLabel = label,
            chartPoints = chartPoints,
            selectedFilter = filter,
            accounts = allAccounts,
            selectedAccountIds = selectedAccountIds,
            isFilterDialogOpen = isDialogOpen
        )
    }


    private fun createCumulativeChartData(
        allTransactions: List<Transaction>,
        filter: String,
        startTime: Long,
        endTime: Long
    ): List<ChartPoint> {

        // 1. ALL uchun startTime'ni birinchi tranzaksiya sanasiga tenglashtiramiz
        val realStartTime = if (filter == FilterKeys.ALL) {
            allTransactions.minOfOrNull { it.date } ?: System.currentTimeMillis()
        } else {
            startTime
        }

        // 2. Davr boshlanishidan oldingi balans (ALL bo'lsa bu 0 bo'ladi, chunki undan oldin tarix yo'q)
        var runningBalance = if (filter == FilterKeys.ALL) 0.0 else {
            allTransactions.filter { it.date < realStartTime }
                .sumOf { if (it.type == TransactionType.INCOME) it.amount else -it.amount }
        }

        val periodTxs = allTransactions.filter { it.date in realStartTime..endTime }.sortedBy { it.date }
        val tempCal = Calendar.getInstance().apply { timeInMillis = realStartTime }

        // 3. Qadamlarni ALL uchun dinamik hisoblaymiz (masalan, 12 ta nuqta)
        val steps = when (filter) {
            FilterKeys.DAY -> 24
            FilterKeys.WEEK -> 7
            FilterKeys.MONTH -> tempCal.getActualMaximum(Calendar.DAY_OF_MONTH)
            FilterKeys.YEAR -> 12
            FilterKeys.ALL -> 12 // Barcha vaqtni 12 ta bo'lakka bo'lib ko'rsatish optimal
            else -> 7
        }

        // ALL uchun har bir qadam qancha vaqtni o'z ichiga olishini hisoblaymiz
        val stepDuration = if (filter == FilterKeys.ALL) {
            (endTime - realStartTime) / steps
        } else 0L

        return List(steps) { index ->
            val stepStart = if (filter == FilterKeys.ALL) {
                realStartTime + (stepDuration * index)
            } else {
                tempCal.timeInMillis
            }

            val stepEnd = if (filter == FilterKeys.ALL) {
                realStartTime + (stepDuration * (index + 1))
            } else {
                // Eskicha mantiq (Kun, Hafta, Oy uchun)
                when (filter) {
                    FilterKeys.DAY -> tempCal.add(Calendar.HOUR_OF_DAY, 1)
                    FilterKeys.WEEK, FilterKeys.MONTH -> tempCal.add(Calendar.DAY_OF_YEAR, 1)
                    FilterKeys.YEAR -> tempCal.add(Calendar.MONTH, 1)
                }
                tempCal.timeInMillis
            }

            // Shu vaqt oralig'idagi o'zgarish
            val netChange = periodTxs.filter { it.date in stepStart until stepEnd }
                .sumOf { if (it.type == TransactionType.INCOME) it.amount else -it.amount }

            runningBalance += netChange

            val label = when (filter) {
                FilterKeys.DAY -> {
                    // "08:00" formatida
                    SimpleDateFormat("HH:00", Locale.getDefault()).format(Date(stepStart))
                }
                FilterKeys.WEEK -> {
                    // "Mon, 7 Jan" - Hafta kuni, kun va oy nomi
                    SimpleDateFormat("EEE, d MMM", Locale.getDefault()).format(Date(stepStart))
                }
                FilterKeys.MONTH -> {
                    // "7 Jan" - Kun va oy nomi (07 emas, 7 bo'lib chiqadi)
                    SimpleDateFormat("d MMM", Locale.getDefault()).format(Date(stepStart))
                }
                FilterKeys.YEAR -> {
                    // "Jan 2024" - Oy nomi va yil
                    SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(Date(stepStart))
                }
                FilterKeys.ALL -> {
                    // "Jan '24" yoki "2024" - Barcha vaqt uchun qisqaroq format
                    SimpleDateFormat("MMM yy", Locale.getDefault()).format(Date(stepStart))
                }
                else -> ""
            }
            ChartPoint(label, runningBalance)
        }
    }


    // --- UI EVENTS ---
    fun onPeriodNavigate(forward: Boolean) {
        val filter = _selectedFilter.value
        if (filter == FilterKeys.ALL) return
        val newCal = Calendar.getInstance().apply { timeInMillis = _currentPeriodStart.value }
        val amount = if (forward) 1 else -1

        when (filter) {
            FilterKeys.DAY -> newCal.add(Calendar.DAY_OF_YEAR, amount)
            FilterKeys.WEEK -> newCal.add(Calendar.WEEK_OF_YEAR, amount)
            FilterKeys.MONTH -> newCal.add(Calendar.MONTH, amount)
            FilterKeys.YEAR -> newCal.add(Calendar.YEAR, amount)
        }
        _currentPeriodStart.value = calculatePeriodBounds(newCal, filter).first
    }

    fun onFilterChange(newFilterKey: String) {
        _selectedFilter.value = newFilterKey
        _currentPeriodStart.value = calculatePeriodBounds(Calendar.getInstance(), newFilterKey).first
    }

    fun onAccountSelectionChange(accountId: String, isSelected: Boolean) {
        _accountFilterState.update { (accounts, currentSelection) ->
            val newSelection = if (isSelected) currentSelection + accountId else currentSelection - accountId
            Pair(accounts, newSelection)
        }
    }

    // --- HELPER FUNCTIONS ---
    private fun calculatePeriodBounds(calendar: Calendar, filter: String): Triple<Long, Long, String> {
        val tempCal = calendar.clone() as Calendar
        tempCal.set(Calendar.HOUR_OF_DAY, 0); tempCal.set(Calendar.MINUTE, 0); tempCal.set(Calendar.SECOND, 0); tempCal.set(Calendar.MILLISECOND, 0)

        val startTime: Long
        val endTime: Long
        val label: String
        val locale = Locale.getDefault()

        when (filter) {
            FilterKeys.DAY -> {
                startTime = tempCal.timeInMillis
                tempCal.add(Calendar.DAY_OF_YEAR, 1); tempCal.add(Calendar.MILLISECOND, -1)
                endTime = tempCal.timeInMillis
                label = SimpleDateFormat("dd MMMM, yyyy", locale).format(calendar.time)
            }
            FilterKeys.WEEK -> {
                tempCal.set(Calendar.DAY_OF_WEEK, tempCal.firstDayOfWeek)
                startTime = tempCal.timeInMillis
                tempCal.add(Calendar.WEEK_OF_YEAR, 1); tempCal.add(Calendar.MILLISECOND, -1)
                endTime = tempCal.timeInMillis
                val format = SimpleDateFormat("dd MMM", locale)
                label = "${format.format(startTime)} - ${format.format(endTime)}"
            }
            FilterKeys.YEAR -> {
                tempCal.set(Calendar.MONTH, Calendar.JANUARY); tempCal.set(Calendar.DAY_OF_MONTH, 1)
                startTime = tempCal.timeInMillis
                tempCal.add(Calendar.YEAR, 1); tempCal.add(Calendar.MILLISECOND, -1)
                endTime = tempCal.timeInMillis
                label = SimpleDateFormat("yyyy", locale).format(calendar.time)
            }
            FilterKeys.ALL -> return Triple(0L, Long.MAX_VALUE, filterAllTimeLabel)
            else -> { // MONTH
                tempCal.set(Calendar.DAY_OF_MONTH, 1)
                startTime = tempCal.timeInMillis
                tempCal.add(Calendar.MONTH, 1); tempCal.add(Calendar.MILLISECOND, -1)
                endTime = tempCal.timeInMillis
                label = SimpleDateFormat("MMMM yyyy", locale).format(calendar.time)
            }
        }
        return Triple(startTime, endTime, label)
    }

    private fun calculatePeriodBounds(periodStart: Long, filter: String): Triple<Long, Long, String> {
        val calendar = Calendar.getInstance().apply { timeInMillis = periodStart }
        return calculatePeriodBounds(calendar, filter)
    }
}