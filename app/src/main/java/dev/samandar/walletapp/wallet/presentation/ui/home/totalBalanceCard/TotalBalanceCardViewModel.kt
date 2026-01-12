package dev.samandar.walletapp.wallet.presentation.ui.home.totalBalanceCard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.samandar.walletapp.wallet.domain.model.Account
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
    val periodLabel: String = "",
    val barChartData: List<BarChartItem> = emptyList(),
    val selectedFilter: String = FilterKeys.MONTH,
    val isLoading: Boolean = true,
    val accounts: List<Account> = emptyList(),
    val selectedAccountIds: Set<String> = emptySet(),
    val isFilterDialogOpen: Boolean = false,
)


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
            transactionsFlow,
            _selectedFilter,
            _currentPeriodStart,
            _accountFilterState,
            _isFilterDialogOpen
        ) { transactions, filter, periodStart, accountPair, isDialogOpen ->
            processTransactionsForPeriod(transactions, filter, periodStart, accountPair, isDialogOpen)
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
        isDialogOpen: Boolean
    ): TotalBalanceUiState {
        val (allAccounts, selectedAccountIds) = accountPair
        val (startTime, endTime, label) = calculatePeriodBounds(periodStart, filter)

        val accountFilteredTransactions = allTransactions.filter {
            selectedAccountIds.contains(it.account.id)
        }

        val netBalance = accountFilteredTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount } -
                accountFilteredTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

        val filteredTransactionsByPeriodAndAccount = accountFilteredTransactions.filter {
            it.date >= startTime && it.date <= endTime
        }
        val periodBalance = filteredTransactionsByPeriodAndAccount.filter { it.type == TransactionType.INCOME }.sumOf { it.amount } -
                filteredTransactionsByPeriodAndAccount.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

        val barData = createBarChartData(filteredTransactionsByPeriodAndAccount, filter, startTime, endTime)

        return TotalBalanceUiState(
            netBalance = netBalance,
            periodBalance = periodBalance,
            periodLabel = label,
            barChartData = barData,
            selectedFilter = filter,
            isLoading = false,
            accounts = allAccounts,
            selectedAccountIds = selectedAccountIds,
            isFilterDialogOpen = isDialogOpen
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
        endTime: Long
    ): List<BarChartItem> {

        if (transactions.isEmpty()) return emptyList()

        val cal = Calendar.getInstance()
        val expenseTransactions = transactions.filter { it.type == TransactionType.EXPENSE }

        if (expenseTransactions.isEmpty()) return emptyList()


        val (groupingUnit, _) = when (filter) {
            FilterKeys.DAY -> Pair(Calendar.HOUR_OF_DAY, SimpleDateFormat("HH:00", Locale.getDefault()))

            FilterKeys.WEEK -> Pair(Calendar.DAY_OF_WEEK, SimpleDateFormat("EE", Locale.getDefault()))

            FilterKeys.MONTH -> Pair(Calendar.DAY_OF_MONTH, SimpleDateFormat("dd", Locale.getDefault()))

            FilterKeys.YEAR, FilterKeys.ALL -> Pair(Calendar.MONTH, SimpleDateFormat("MMM", Locale.getDefault()))
            else -> return emptyList()
        }

        val dataMap = expenseTransactions.groupBy {
            cal.timeInMillis = it.date
            cal.get(groupingUnit)
        }.mapValues { (_, txs) -> txs.sumOf { it.amount } }

        val barData = mutableListOf<BarChartItem>()
        val tempCal = Calendar.getInstance().apply { timeInMillis = startTime }

        if (filter == FilterKeys.ALL) {
            val minDate = expenseTransactions.minOf { it.date }
            tempCal.timeInMillis = minDate
            tempCal.set(Calendar.MONTH, Calendar.JANUARY)
            tempCal.set(Calendar.DAY_OF_MONTH, 1)
            tempCal.set(Calendar.HOUR_OF_DAY, 0)
            tempCal.set(Calendar.MINUTE, 0)
            tempCal.set(Calendar.SECOND, 0)
            tempCal.set(Calendar.MILLISECOND, 0)
        }

        val maxIterations = when(filter) {
            FilterKeys.DAY -> 24
            FilterKeys.WEEK -> 7
            FilterKeys.MONTH -> tempCal.getActualMaximum(Calendar.DAY_OF_MONTH)
            FilterKeys.YEAR -> 12
            FilterKeys.ALL -> 60
            else -> 7
        }
        var count = 0
        val maxEndTime = if (filter == FilterKeys.ALL) Long.MAX_VALUE else endTime

        while (tempCal.timeInMillis <= maxEndTime && count < maxIterations) {

            val unitKey = tempCal.get(groupingUnit)

            val value = dataMap[unitKey]?.div(1000000.0) ?: 0.0

            val label = when (filter) {
                FilterKeys.DAY -> SimpleDateFormat("HH:00", Locale.getDefault()).format(tempCal.time)
                FilterKeys.WEEK -> SimpleDateFormat("EE", Locale.getDefault()).format(tempCal.time) // Dushanba/Yakshanba
                FilterKeys.MONTH -> SimpleDateFormat("dd", Locale.getDefault()).format(tempCal.time) // 01, 02, ...
                FilterKeys.YEAR -> SimpleDateFormat("MMM", Locale.getDefault()).format(tempCal.time) // Yan, Fev, ...
                FilterKeys.ALL -> SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(tempCal.time) // Yan 2024, Fev 2024, ...
                else -> SimpleDateFormat("HH:00", Locale.getDefault()).format(tempCal.time)
            }

            barData.add(BarChartItem(label, value.absoluteValue))
            count++
            when (filter) {
                FilterKeys.DAY -> tempCal.add(Calendar.HOUR_OF_DAY, 1)

                FilterKeys.WEEK, FilterKeys.MONTH -> tempCal.add(Calendar.DAY_OF_YEAR, 1)

                FilterKeys.YEAR, FilterKeys.ALL -> tempCal.add(Calendar.MONTH, 1)
            }

            if (filter == FilterKeys.ALL) {
                val currentCal = Calendar.getInstance()
                if (tempCal.after(currentCal)) break
            }
        }

        return barData
    }
}