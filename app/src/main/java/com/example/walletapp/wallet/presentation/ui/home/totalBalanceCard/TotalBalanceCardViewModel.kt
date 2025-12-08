package com.example.walletapp.wallet.presentation.ui.home.totalBalanceCard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletapp.wallet.domain.model.Account
import com.example.walletapp.wallet.domain.model.Transaction
import com.example.walletapp.wallet.domain.model.TransactionType
import com.example.walletapp.wallet.domain.usecase.account.GetAllAccounts
import com.example.walletapp.wallet.domain.usecase.transaction.GetAllTransactions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import kotlin.math.absoluteValue

// --- Ma'lumotlar Modellari (Presentation Layer uchun) ---

data class BarChartItem(
    val label: String,
    val value: Double, // Millionlik shkalada (M UZS)
    val isExpense: Boolean = true
)

data class TotalBalanceUiState(
    val netBalance: Double = 0.0,
    val periodBalance: Double = 0.0,
    val periodLabel: String = "Joriy Oy",
    val barChartData: List<BarChartItem> = emptyList(),
    val selectedFilter: String = "Month",
    val isLoading: Boolean = true,
    // Filtrlash uchun qo'shilgan maydonlar
    val accounts: List<Account> = emptyList(),
    val selectedAccountIds: Set<String> = emptySet(),
    val isFilterDialogOpen: Boolean = false,
)

// --- ViewModel ---

@HiltViewModel
class TotalBalanceCardViewModel @Inject constructor(
    private val getAllTransactions: GetAllTransactions,
    private val getAllAccounts: GetAllAccounts
) : ViewModel() {

    // REAL MA'LUMOT MANBASI
    private val transactionsFlow: Flow<List<Transaction>> = getAllTransactions(type = null)
    private val accountsFlow: Flow<List<Account>> = getAllAccounts()

    // Vaqt bo'yicha filtr holatlari
    private val _selectedFilter = MutableStateFlow("Month")
    private val _currentPeriodStart = MutableStateFlow(calculatePeriodBounds(Calendar.getInstance(), "Month").first)

    // Hisob bo'yicha filtr holatlari
    private val _accountFilterState = MutableStateFlow(
        Pair(emptyList<Account>(), emptySet<String>())
    )
    private val _isFilterDialogOpen = MutableStateFlow(false)

    private val _cardState = MutableStateFlow(TotalBalanceUiState(isLoading = true))
    val cardState: StateFlow<TotalBalanceUiState> = _cardState.asStateFlow()

    init {
        // 1. Hisoblarni yuklash va boshlang'ich tanlovni o'rnatish (barchasi tanlangan)
        accountsFlow.onEach { accountsList ->
            val initialSelection = accountsList.map { it.id }.toSet()
            _accountFilterState.value = Pair(accountsList, initialSelection)
        }.launchIn(viewModelScope)

        // 2. Barcha oqimlarni birlashtirish va hisoblash
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

    // --- Boshqaruv Funksiyalari ---

    fun onPeriodNavigate(forward: Boolean) {
        val filter = _selectedFilter.value
        if (filter == "All") return
        val newCal = Calendar.getInstance().apply { timeInMillis = _currentPeriodStart.value }
        val amount = if (forward) 1 else -1

        when (filter) {
            "Day" -> newCal.add(Calendar.DAY_OF_YEAR, amount)
            "Week" -> newCal.add(Calendar.WEEK_OF_YEAR, amount)
            "Month" -> newCal.add(Calendar.MONTH, amount)
            "Year" -> newCal.add(Calendar.YEAR, amount)
        }
        _currentPeriodStart.value = calculatePeriodBounds(newCal, filter).first
    }

    fun onFilterChange(newFilter: String) {
        _selectedFilter.value = newFilter
        _currentPeriodStart.value = calculatePeriodBounds(Calendar.getInstance(), newFilter).first
    }

    // --- Filtr Dialog Funksiyalari ---

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

    // --- Asosiy Hisoblash Mantig'i ---

    private fun processTransactionsForPeriod(
        allTransactions: List<Transaction>,
        filter: String,
        periodStart: Long,
        accountPair: Pair<List<Account>, Set<String>>,
        isDialogOpen: Boolean
    ): TotalBalanceUiState {
        val (allAccounts, selectedAccountIds) = accountPair
        val (startTime, endTime, label) = calculatePeriodBounds(periodStart, filter)

        // 1. Hisob bo'yicha filtratsiya
        val accountFilteredTransactions = allTransactions.filter {
            selectedAccountIds.contains(it.account.id)
        }

        // 2. Umumiy balans (Tanlangan hisoblar bo'yicha, barcha vaqt)
        val netBalance = accountFilteredTransactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount } -
                accountFilteredTransactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

        // 3. Tanlangan davr uchun balans (Hisob + Vaqt bo'yicha filtr)
        val filteredTransactionsByPeriodAndAccount = accountFilteredTransactions.filter {
            it.date >= startTime && it.date <= endTime
        }
        val periodBalance = filteredTransactionsByPeriodAndAccount.filter { it.type == TransactionType.INCOME }.sumOf { it.amount } -
                filteredTransactionsByPeriodAndAccount.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

        // 4. Grafik ma'lumotlari (Davr + Hisob bo'yicha filtrdan o'tgan tranzaksiyalar)
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

    // --- Yordamchi Funksiyalar (Vaqt) ---

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
                label = "${format.format(startTime)} - ${format.format(endTime)}"
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

    // --- Grafik Ma'lumotlarini Tayyorlash ---

    private fun createBarChartData(transactions: List<Transaction>, filter: String, startTime: Long, endTime: Long): List<BarChartItem> {
        if (transactions.isEmpty() || filter == "All") return emptyList()

        val cal = Calendar.getInstance()
        val expenseTransactions = transactions.filter { it.type == TransactionType.EXPENSE }

        val (groupingUnit, _) = when (filter) {
            "Day" -> Pair(Calendar.HOUR_OF_DAY, SimpleDateFormat("HH:00", Locale.getDefault()))
            "Week", "Month" -> Pair(Calendar.DAY_OF_YEAR, SimpleDateFormat("dd", Locale.getDefault()))
            "Year" -> Pair(Calendar.MONTH, SimpleDateFormat("MMM", Locale.getDefault()))
            else -> return emptyList()
        }

        val dataMap = expenseTransactions.groupBy {
            cal.timeInMillis = it.date
            cal.get(groupingUnit)
        }.mapValues { (_, txs) -> txs.sumOf { it.amount } }

        val barData = mutableListOf<BarChartItem>()
        val tempCal = Calendar.getInstance().apply { timeInMillis = startTime }

        val maxBars = when(filter) { // Maksimal chiziqlar sonini cheklash
            "Day" -> 24 // Kunlik 24 soatni ko'rsatish
            "Week" -> 7
            "Month" -> 31
            "Year" -> 12
            else -> 7
        }

        while (tempCal.timeInMillis <= endTime) {
            val unitKey = tempCal.get(groupingUnit)

            // SUMMANI MILLIONGA O'TKAZISH
            val value = dataMap[unitKey]?.div(1000000.0) ?: 0.0

            val label = when(filter) {
                "Week" -> SimpleDateFormat("EE", Locale.getDefault()).format(tempCal.time)
                "Month" -> SimpleDateFormat("dd", Locale.getDefault()).format(tempCal.time)
                "Year" -> SimpleDateFormat("MMM", Locale.getDefault()).format(tempCal.time)
                else -> SimpleDateFormat("HH:00", Locale.getDefault()).format(tempCal.time)
            }

            // Barcha nuqtalarni qo'shish, ammo UI qismi ularni qisqartirib ko'rsatadi (TotalBalanceCard.kt da optimallashgan)
            barData.add(BarChartItem(label, value.absoluteValue))

            when (filter) {
                "Day" -> tempCal.add(Calendar.HOUR_OF_DAY, 1)
                "Week", "Month" -> tempCal.add(Calendar.DAY_OF_YEAR, 1)
                "Year" -> tempCal.add(Calendar.MONTH, 1)
            }
        }

        return barData
    }
}