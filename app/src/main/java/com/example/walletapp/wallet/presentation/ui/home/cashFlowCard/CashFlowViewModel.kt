package com.example.walletapp.wallet.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletapp.wallet.domain.model.Transaction
import com.example.walletapp.wallet.domain.model.TransactionType
import com.example.walletapp.wallet.domain.usecase.transaction.GetAllTransactions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

// --- Ma'lumotlar Modeli (CashFlowCard uchun) ---

data class CashFlowUiState(
    val periodLabel: String = "Joriy Oy",
    val income: Double = 0.0,
    val expenses: Double = 0.0,
    val total: Double = 0.0,
    val isLoading: Boolean = true,
    val isFilterDialogOpen: Boolean = false, // 🚨 Yangi: Filtr Dialog holati
)

// --- ViewModel ---

@HiltViewModel
class CashFlowViewModel @Inject constructor(
    private val getAllTransactions: GetAllTransactions
) : ViewModel() {

    private val transactionsFlow: Flow<List<Transaction>> = getAllTransactions(type = null)

    private val _currentPeriodStart = MutableStateFlow(calculatePeriodBounds(Calendar.getInstance(), "Month").first)
    private val _selectedFilter = MutableStateFlow("Month")
    private val _isFilterDialogOpen = MutableStateFlow(false) // 🚨 Yangi: Filtr Dialog holati

    private val _cardState = MutableStateFlow(CashFlowUiState(isLoading = true))
    val cardState: StateFlow<CashFlowUiState> = _cardState.asStateFlow()

    init {
        combine(
            transactionsFlow,
            _currentPeriodStart,
            _selectedFilter,
            _isFilterDialogOpen // Filtr holatini birlashtirish
        ) { transactions, periodStart, filter, isDialogOpen ->
            processCashFlow(transactions, periodStart, filter, isDialogOpen)
        }.onEach { newState ->
            _cardState.value = newState.copy(isLoading = false)
        }.launchIn(viewModelScope)
    }

    // --- Boshqaruv Funksiyalari ---

    // 🚨 Filtr Dialog Funksiyalari
    fun onFilterClick() {
        _isFilterDialogOpen.value = true
    }

    fun onFilterDismiss() {
        _isFilterDialogOpen.value = false
    }

    // Vaqt Navigatsiyasi Funksiyalari
    fun onPeriodNavigate(forward: Boolean) {
        val filter = _selectedFilter.value
        if (filter == "All") return

        val newCal = Calendar.getInstance().apply { timeInMillis = _currentPeriodStart.value }
        val amount = if (forward) 1 else -1

        when (filter) {
            "Day" -> newCal.add(Calendar.DAY_OF_YEAR, amount)
            "Week" -> newCal.add(Calendar.WEEK_OF_YEAR, amount) // WEEK_OF_YEAR ga o'zgartirildi
            "Month" -> newCal.add(Calendar.MONTH, amount)
            "Year" -> newCal.add(Calendar.YEAR, amount)
        }
        _currentPeriodStart.value = calculatePeriodBounds(newCal, filter).first
    }

    fun onFilterChange(newFilter: String) {
        _selectedFilter.value = newFilter
        _currentPeriodStart.value = calculatePeriodBounds(Calendar.getInstance(), newFilter).first
    }

    // --- Asosiy Hisoblash Mantig'i ---

    private fun processCashFlow(
        allTransactions: List<Transaction>,
        periodStart: Long,
        filter: String,
        isDialogOpen: Boolean
    ): CashFlowUiState {

        val (startTime, endTime, label) = calculatePeriodBounds(periodStart, filter)

        val filteredTransactions = allTransactions.filter {
            it.date >= startTime && it.date <= endTime
        }

        val income = filteredTransactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }

        val expenses = filteredTransactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }

        val total = income - expenses

        return CashFlowUiState(
            periodLabel = label,
            income = income,
            expenses = expenses,
            total = total,
            isFilterDialogOpen = isDialogOpen // Holatni yangilash
        )
    }

    // --- Yordamchi Funksiyalar (Vaqt) ---
    // (calculatePeriodBounds funksiyalari bu yerda o'zgarmasdan qoladi)

    private fun calculatePeriodBounds(calendar: Calendar, filter: String): Triple<Long, Long, String> {
        val tempCal = calendar.clone() as Calendar
        tempCal.set(Calendar.HOUR_OF_DAY, 0); tempCal.set(Calendar.MINUTE, 0); tempCal.set(Calendar.SECOND, 0); tempCal.set(Calendar.MILLISECOND, 0)

        val startTime: Long
        val endTime: Long
        val label: String
        val locale = Locale.getDefault()

        when (filter) {
            "Day" -> {
                startTime = tempCal.timeInMillis
                tempCal.add(Calendar.DAY_OF_YEAR, 1); tempCal.add(Calendar.MILLISECOND, -1)
                endTime = tempCal.timeInMillis
                label = SimpleDateFormat("dd MMMM, yyyy", locale).format(calendar.time)
            }
            "Week" -> {
                tempCal.set(Calendar.DAY_OF_WEEK, tempCal.firstDayOfWeek)
                startTime = tempCal.timeInMillis
                val endOfWeek = tempCal.clone() as Calendar
                endOfWeek.add(Calendar.WEEK_OF_YEAR, 1); endOfWeek.add(Calendar.MILLISECOND, -1)
                endTime = endOfWeek.timeInMillis
                val format = SimpleDateFormat("dd MMM", locale)
                label = "${format.format(startTime)} - ${format.format(endTime)}, ${SimpleDateFormat("yyyy", locale).format(calendar.time)}"
            }
            "Year" -> {
                tempCal.set(Calendar.MONTH, Calendar.JANUARY); tempCal.set(Calendar.DAY_OF_MONTH, 1)
                startTime = tempCal.timeInMillis
                val endOfYear = tempCal.clone() as Calendar
                endOfYear.add(Calendar.YEAR, 1); endOfYear.add(Calendar.MILLISECOND, -1)
                endTime = endOfYear.timeInMillis
                label = SimpleDateFormat("yyyy", locale).format(calendar.time)
            }
            "All" -> return Triple(0L, Long.MAX_VALUE, "Barcha Vaqt")
            else -> { // Default: Month
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
}