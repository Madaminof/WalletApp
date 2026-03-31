package dev.samandar.walletapp.wallet.presentation.ui.home.totalBalanceCard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.samandar.walletapp.wallet.domain.model.account.Account
import dev.samandar.walletapp.wallet.domain.model.Transaction
import dev.samandar.walletapp.wallet.domain.model.TransactionType
import dev.samandar.walletapp.wallet.domain.usecase.account.GetAllAccounts
import dev.samandar.walletapp.wallet.domain.usecase.transaction.GetAllTransactions
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.samandar.walletapp.R
import dev.samandar.walletapp.utils.FilterKeys
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import kotlin.math.absoluteValue



data class BarChartItem(
    val label: String,
    val value: Double,
    val isExpense: Boolean = true
)

data class TotalBalanceUiState(
    val netBalance: Double = 0.0,
    val periodBalance: Double = 0.0,
    val totalIncome: Double = 0.0,      // QO'SHILDI
    val totalExpense: Double = 0.0,     // QO'SHILDI
    val periodLabel: String = "",
    val barChartData: List<BarChartItem> = emptyList(),
    val selectedFilter: String = FilterKeys.MONTH,
    val isLoading: Boolean = true,
    val accounts: List<Account> = emptyList(),
    val selectedAccountIds: Set<String> = emptySet(),
    val isFilterDialogOpen: Boolean = false,
    val isIncomeMode: Boolean = false   // Switch orqali boshqariladigan rejim
) {
    // Grafik uchun maksimal limit (133K va 17K ni solishtirish uchun)
    val globalMaxLimit: Double get() = maxOf(
        netBalance.absoluteValue,
        barChartData.maxOfOrNull { it.value } ?: 0.0
    ).coerceAtLeast(1.0)
}


@HiltViewModel
class TotalBalanceCardViewModel @Inject constructor(
    private val getAllTransactions: GetAllTransactions,
    private val getAllAccounts: GetAllAccounts,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val filterAllTimeLabel by lazy { context.getString(R.string.filter_all) }

    private val transactionsFlow: Flow<List<Transaction>> = getAllTransactions(type = null)
    private val accountsFlow: Flow<List<Account>> = getAllAccounts()

    private val _selectedFilter = MutableStateFlow(FilterKeys.MONTH)

    private val _isShowingIncome = MutableStateFlow(false)

    private val _currentPeriodStart = MutableStateFlow(calculatePeriodBounds(Calendar.getInstance(), FilterKeys.MONTH).first)

    private val _accountFilterState = MutableStateFlow(
        Pair(emptyList<Account>(), emptySet<String>())
    )
    private val _isFilterDialogOpen = MutableStateFlow(false)

    private val initialPeriodLabel = calculatePeriodBounds(Calendar.getInstance(), FilterKeys.MONTH).third
    private val _cardState = MutableStateFlow(TotalBalanceUiState(
        isLoading = true,
        periodLabel = initialPeriodLabel,
        selectedFilter = FilterKeys.MONTH
    ))
    val cardState: StateFlow<TotalBalanceUiState> = _cardState.asStateFlow()

    init {
        accountsFlow.onEach { accountsList ->
            val initialSelection = accountsList.map { it.id }.toSet()
            _accountFilterState.value = Pair(accountsList, initialSelection)
        }.launchIn(viewModelScope)

        combine(
            transactionsFlow,          // [0]
            _selectedFilter,           // [1]
            _currentPeriodStart,       // [2]
            _accountFilterState,       // [3]
            _isFilterDialogOpen,       // [4]
            _isShowingIncome           // [5]
        ) { args: Array<Any> ->
            // Massivdan elementlarni o'z tipiga cast qilib olamiz
            val transactions = args[0] as List<Transaction>
            val filter = args[1] as String
            val periodStart = args[2] as Long
            val accountPair = args[3] as Pair<List<Account>, Set<String>>
            val isDialogOpen = args[4] as Boolean
            val isShowingIncome = args[5] as Boolean

            // Endi hamma argumentlarni funksiyaga uzatamiz
            processTransactionsForPeriod(
                transactions,
                filter,
                periodStart,
                accountPair,
                isDialogOpen,
                isShowingIncome
            )
        }.onEach { newState ->
            _cardState.value = newState.copy(isLoading = false)
        }.launchIn(viewModelScope)
    }


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

    fun onFilterClick() {
        _isFilterDialogOpen.value = true
    }

    fun onFilterDismiss() {
        _isFilterDialogOpen.value = false
    }

    fun onAccountSelectionChange(accountId: String, isSelected: Boolean) {
        _accountFilterState.update { (accounts, currentSelection) ->
            val newSelection = if (isSelected) {
                currentSelection + accountId
            } else {
                currentSelection - accountId
            }
            Pair(accounts, newSelection)
        }
    }


    private fun processTransactionsForPeriod(
        allTransactions: List<Transaction>,
        filter: String,
        periodStart: Long,
        accountPair: Pair<List<Account>, Set<String>>,
        isDialogOpen: Boolean,
        manualShowIncome: Boolean
    ): TotalBalanceUiState {
        val (allAccounts, selectedAccountIds) = accountPair
        val (startTime, endTime, label) = calculatePeriodBounds(periodStart, filter)

        val accountFilteredTransactions = allTransactions.filter {
            selectedAccountIds.contains(it.account.id)
        }

        val totalInc = accountFilteredTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val totalExp = accountFilteredTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        val netBalance = totalInc - totalExp

        // 3. TANLANGAN DAVR (Period) tranzaksiyalari
        val periodTransactions = accountFilteredTransactions.filter {
            it.date >= startTime && it.date <= endTime
        }

        val periodIncome = periodTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val periodExpense = periodTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

        // 4. CHART REJIMINI ANIQLASH (SMART LOGIC)
        // Professional mantiq:
        // - Agar foydalanuvchi UI'da "Income"ni tanlasa -> Income ko'rsatiladi.
        // - Agar default holat bo'lsa:
        //      - Umumiy balans minus bo'lsa (netBalance < 0) -> Expense ko'rsatiladi (Xavf rejimi).
        //      - Shu davrda xarajat ko'p bo'lsa -> Expense ko'rsatiladi.
        val isExpensePriority = netBalance < 0 || periodExpense > periodIncome

        // Agar foydalanuvchi qo'lda tanlamagan bo'lsa (manualShowIncome - false bo'lsa),
        // avtomatik ravishda isExpensePriority'ga teskari qiymat oladi.
        val effectiveIncomeMode = if (manualShowIncome) true else !isExpensePriority

        // 5. Grafik ma'lumotlarini tayyorlash
        val barData = createBarChartData(
            transactions = periodTransactions,
            filter = filter,
            startTime = startTime,
            endTime = endTime,
            showIncome = effectiveIncomeMode
        )

        return TotalBalanceUiState(
            netBalance = netBalance, // Masalan: -342 000
            periodBalance = if (effectiveIncomeMode) periodIncome else periodExpense,
            totalIncome = totalInc,
            totalExpense = totalExp,
            periodLabel = label,
            barChartData = barData,
            selectedFilter = filter,
            isLoading = false,
            accounts = allAccounts,
            selectedAccountIds = selectedAccountIds,
            isFilterDialogOpen = isDialogOpen,
            isIncomeMode = effectiveIncomeMode // Chart rangini (yashil/qizil) belgilaydi
        )
    }

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
                val endOfWeek = tempCal.clone() as Calendar
                endOfWeek.add(Calendar.WEEK_OF_YEAR, 1); endOfWeek.add(Calendar.MILLISECOND, -1)
                endTime = endOfWeek.timeInMillis
                val format = SimpleDateFormat("dd MMM", locale)
                label = "${format.format(startTime)} - ${format.format(endTime)}"
            }
            FilterKeys.YEAR -> {
                tempCal.set(Calendar.MONTH, Calendar.JANUARY); tempCal.set(Calendar.DAY_OF_MONTH, 1)
                startTime = tempCal.timeInMillis
                val endOfYear = tempCal.clone() as Calendar
                endOfYear.add(Calendar.YEAR, 1); endOfYear.add(Calendar.MILLISECOND, -1)
                endTime = endOfYear.timeInMillis
                label = SimpleDateFormat("yyyy", locale).format(calendar.time)
            }
            FilterKeys.ALL -> return Triple(0L, Long.MAX_VALUE, filterAllTimeLabel)

            else -> {
                tempCal.set(Calendar.DAY_OF_MONTH, 1)
                startTime = tempCal.timeInMillis
                val endOfMonth = tempCal.clone() as Calendar
                endOfMonth.add(Calendar.MONTH, 1); endOfMonth.add(Calendar.MILLISECOND, -1)
                endTime = endOfMonth.timeInMillis
                label = SimpleDateFormat("MMMM yyyy", locale).format(calendar.time)
            }
        }
        return Triple(startTime, endTime, label)
    }

    private fun calculatePeriodBounds(periodStart: Long, filter: String): Triple<Long, Long, String> {
        val calendar = Calendar.getInstance().apply { timeInMillis = periodStart }
        return calculatePeriodBounds(calendar, filter)
    }


    private fun createBarChartData(
        transactions: List<Transaction>,
        filter: String,
        startTime: Long,
        endTime: Long,
        showIncome: Boolean
    ): List<BarChartItem> {
        val targetType = if (showIncome) TransactionType.INCOME else TransactionType.EXPENSE
        val filteredTxs = transactions.filter { it.type == targetType }

        // Agar ma'lumot bo'lmasa, o'qlar ko'rinishi uchun bo'sh bo'lmagan list qaytargan ma'qul
        // yoki UI darajasida "Empty" holatini ko'rsatish kerak.

        val cal = Calendar.getInstance()
        val groupingUnit = when (filter) {
            FilterKeys.DAY -> Calendar.HOUR_OF_DAY
            FilterKeys.WEEK -> Calendar.DAY_OF_WEEK
            FilterKeys.MONTH -> Calendar.DAY_OF_MONTH
            FilterKeys.YEAR, FilterKeys.ALL -> Calendar.MONTH
            else -> Calendar.DAY_OF_MONTH
        }

        val dataMap = filteredTxs.groupBy {
            cal.timeInMillis = it.date
            cal.get(groupingUnit)
        }.mapValues { (_, txs) -> txs.sumOf { it.amount }.absoluteValue }

        val barData = mutableListOf<BarChartItem>()
        val tempCal = Calendar.getInstance().apply { timeInMillis = startTime }

        // Iteratsiyalar soni va loop mantiqi (Siz yozgan kod deyarli to'g'ri)
        val maxIterations = when (filter) {
            FilterKeys.DAY -> 24
            FilterKeys.WEEK -> 7
            FilterKeys.MONTH -> tempCal.getActualMaximum(Calendar.DAY_OF_MONTH)
            FilterKeys.YEAR -> 12
            FilterKeys.ALL -> 12 // All time uchun oxirgi 12 oyni ko'rsatish optimal
            else -> 7
        }

        repeat(maxIterations) {
            val unitKey = tempCal.get(groupingUnit)
            val value = dataMap[unitKey] ?: 0.0

            val label = when (filter) {
                FilterKeys.DAY -> String.format("%02d:00", unitKey)
                FilterKeys.WEEK -> SimpleDateFormat("EEE", Locale.getDefault()).format(tempCal.time)
                FilterKeys.MONTH -> tempCal.get(Calendar.DAY_OF_MONTH).toString()
                FilterKeys.YEAR -> SimpleDateFormat("MMM", Locale.getDefault()).format(tempCal.time)
                else -> ""
            }

            barData.add(BarChartItem(label, value, !showIncome))

            // Vaqtni siljitish
            when (filter) {
                FilterKeys.DAY -> tempCal.add(Calendar.HOUR_OF_DAY, 1)
                FilterKeys.WEEK, FilterKeys.MONTH -> tempCal.add(Calendar.DAY_OF_YEAR, 1)
                FilterKeys.YEAR -> tempCal.add(Calendar.MONTH, 1)
                else -> tempCal.add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        return barData
    }
}