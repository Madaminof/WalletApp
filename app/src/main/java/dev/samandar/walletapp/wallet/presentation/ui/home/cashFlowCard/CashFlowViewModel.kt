package dev.samandar.walletapp.wallet.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.samandar.walletapp.wallet.domain.model.Transaction
import dev.samandar.walletapp.wallet.domain.model.TransactionType
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
import android.content.Context
import dev.samandar.walletapp.wallet.data.currencyManagerApi.repository.CurrencyRepository
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencyManager
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.changeUpdateAmount.CurrencyEvaluator

data class CashFlowUiState(
    val periodLabel: String,
    val income: Double = 0.0,
    val expenses: Double = 0.0,
    val total: Double = 0.0,
    val isLoading: Boolean = true,
    val selectedFilter: String,
    val isFilterDialogOpen: Boolean = false,
)


@HiltViewModel
class CashFlowViewModel @Inject constructor(
    private val getAllTransactions: GetAllTransactions,
    private val currencyRepository: CurrencyRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val ratesFlow = currencyRepository.allRates
    private val currentCurrencyFlow = CurrencyManager.getCurrencyFlow()

    private val transactionsFlow: Flow<List<Transaction>> = getAllTransactions(type = null)

    private val defaultFilter = FilterKeys.MONTH

    private val filterAllTimeLabel: String
        get() = context.getString(R.string.filter_all)
    private val initialPeriodStart = calculatePeriodBounds(Calendar.getInstance(), defaultFilter).first
    private val _currentPeriodStart = MutableStateFlow(initialPeriodStart)
    private val _selectedFilter = MutableStateFlow(defaultFilter)
    private val _isFilterDialogOpen = MutableStateFlow(false)


    val cardState: StateFlow<CashFlowUiState> = combine(
        getAllTransactions(type = null),
        _currentPeriodStart,
        _selectedFilter,
        _isFilterDialogOpen,
        ratesFlow,
        currentCurrencyFlow
    ) { array: Array<Any> ->  // Massiv ko'rinishida qabul qilamiz
        val transactions = array[0] as List<Transaction>
        val periodStart = array[1] as Long
        val filter = array[2] as String
        val isDialogOpen = array[3] as Boolean
        val rates = array[4] as List<dev.samandar.walletapp.wallet.data.currencyManagerApi.entities.CurrencyRateEntity>
        val currentCurrency = array[5] as String

        // 1. Valyuta konvertatsiyasi
        val convertedTransactions = transactions.map { transaction ->
            val displayAmount = CurrencyEvaluator.convert(
                amount = transaction.amount,
                currentCurrency = currentCurrency,
                rates = rates
            )
            transaction.copy(amount = displayAmount)
        }

        // 2. Hisob-kitob
        processCashFlow(convertedTransactions, periodStart, filter, isDialogOpen)
            .copy(isLoading = false)

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CashFlowUiState(
            isLoading = true,
            periodLabel = calculatePeriodBounds(Calendar.getInstance(), defaultFilter).third,
            selectedFilter = defaultFilter
        )
    )


/*
    fun onFilterClick() { _isFilterDialogOpen.value = true }
    fun onFilterDismiss() { _isFilterDialogOpen.value = false }

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
    }*/



    fun onFilterChange(newFilterKey: String) {
        _selectedFilter.value = newFilterKey
        _currentPeriodStart.value = calculatePeriodBounds(Calendar.getInstance(), newFilterKey).first
    }

    private fun processCashFlow(
        allTransactions: List<Transaction>,
        periodStart: Long,
        filter: String,
        isDialogOpen: Boolean
    ): CashFlowUiState {
        val (startTime, endTime, label) = calculatePeriodBounds(periodStart, filter)
        val filteredTransactions = allTransactions.filter { it.date in startTime..endTime }

        val income = filteredTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val expenses = filteredTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

        return CashFlowUiState(
            periodLabel = label,
            income = income,
            expenses = expenses,
            total = income - expenses,
            isFilterDialogOpen = isDialogOpen,
            selectedFilter = filter
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
                label = "${format.format(startTime)} - ${format.format(endTime)}, ${SimpleDateFormat("yyyy", locale).format(calendar.time)}"
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

            FilterKeys.MONTH -> {
                tempCal.set(Calendar.DAY_OF_MONTH, 1)
                startTime = tempCal.timeInMillis
                val endOfMonth = tempCal.clone() as Calendar
                endOfMonth.add(Calendar.MONTH, 1); endOfMonth.add(Calendar.MILLISECOND, -1)
                endTime = endOfMonth.timeInMillis
                label = SimpleDateFormat("MMMM yyyy", locale).format(calendar.time)
            }
            else -> return calculatePeriodBounds(calendar, FilterKeys.MONTH)
        }
        return Triple(startTime, endTime, label)
    }

    private fun calculatePeriodBounds(periodStart: Long, filter: String): Triple<Long, Long, String> {
        val calendar = Calendar.getInstance().apply { timeInMillis = periodStart }
        return calculatePeriodBounds(calendar, filter)
    }
}