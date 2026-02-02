package dev.samandar.walletapp.wallet.presentation.ui.home.cardStatistics

import android.content.Context // 1. Context uchun import
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext // 2. Contextni in'yeksiya qilish uchun
import dev.samandar.walletapp.utils.FilterKeys
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.domain.model.Category
import dev.samandar.walletapp.wallet.domain.model.Transaction
import dev.samandar.walletapp.wallet.domain.model.TransactionType
import dev.samandar.walletapp.wallet.domain.usecase.category.GetCategoriesByType
import dev.samandar.walletapp.wallet.domain.usecase.transaction.GetAllTransactions
import dev.samandar.walletapp.wallet.presentation.viewmodel.chartViewmodel.CategoryData
import dev.samandar.walletapp.wallet.presentation.viewmodel.chartViewmodel.getCategoryColor
import kotlinx.coroutines.flow.*
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.WeekFields
import java.util.Locale
import javax.inject.Inject

sealed class TimePeriod(val key: String, @StringRes val labelResId: Int) {
    object Daily : TimePeriod(FilterKeys.DAY, Strings.filter_day)
    object Weekly : TimePeriod(FilterKeys.WEEK, Strings.filter_week)
    object Monthly : TimePeriod(FilterKeys.MONTH, Strings.filter_month)
    object Year : TimePeriod(FilterKeys.YEAR, Strings.filter_year)
    object AllTime : TimePeriod(FilterKeys.ALL, Strings.filter_all)

    companion object {

        fun fromKey(key: String): TimePeriod? {
            return ALL_PERIODS.firstOrNull { it.key == key }
        }
    }
}


@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getAllTransactions: GetAllTransactions,
    private val getCategoriesByType: GetCategoriesByType,
    @ApplicationContext private val context: Context // ✅ Context in'yeksiyasi
) : ViewModel() {

    // --- Lokalizatsiya qilingan statik stringlarni yuklash ---
    // Faraz qilingan Strings.unknown ID si orqali "Noma'lum" stringini lokalizatsiya qilamiz
    private val unknownLabel: String = context.getString(Strings.unknown)

    private val allTransactionsFlow = getAllTransactions(type = null)

    private val _selectedPeriod = MutableStateFlow<TimePeriod>(TimePeriod.Monthly)
    val selectedPeriod: StateFlow<TimePeriod> = _selectedPeriod.asStateFlow()

    // ✅ Tanlangan Periodning LOKALIZATSIYA qilingan nomini UI ga taqdim etish
    val selectedPeriodLabel: StateFlow<String> = selectedPeriod
        .map { period -> context.getString(period.labelResId) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = context.getString(TimePeriod.Monthly.labelResId)
        )

    @RequiresApi(Build.VERSION_CODES.O)
    val filteredTransactions: StateFlow<List<Transaction>> = combine(
        allTransactionsFlow,
        _selectedPeriod
    ) { transactions, period ->
        filterTransactionsByPeriod(transactions, period)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val expenseCategories = getCategoriesByType(TransactionType.EXPENSE)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    @RequiresApi(Build.VERSION_CODES.O)
    val expenseStatistics: StateFlow<List<CategoryData>> = combine(
        filteredTransactions,
        expenseCategories
    ) { transactions, categories ->
        calculateExpenseStatistics(transactions, categories)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    @RequiresApi(Build.VERSION_CODES.O)
    val totalExpense: StateFlow<Double> = expenseStatistics
        .map { categoryDataList ->
            categoryDataList.sumOf { it.amount }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun Long.toLocalDate(): LocalDate {
        return Instant.ofEpochMilli(this)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun filterTransactionsByPeriod(
        transactions: List<Transaction>,
        period: TimePeriod
    ): List<Transaction> {
        val today = LocalDate.now()

        return when (period) {
            TimePeriod.AllTime -> transactions
            TimePeriod.Daily -> transactions.filter { it.date.toLocalDate().isEqual(today) }
            TimePeriod.Weekly -> {
                val weekFields = WeekFields.of(Locale.getDefault())
                val currentWeek = today.get(weekFields.weekOfWeekBasedYear())
                val currentYear = today.year

                transactions.filter { transaction ->
                    val transactionDate = transaction.date.toLocalDate()
                    val transactionWeek = transactionDate.get(weekFields.weekOfWeekBasedYear())
                    transactionDate.year == currentYear && transactionWeek == currentWeek
                }
            }
            TimePeriod.Monthly -> transactions.filter { transaction ->
                val transactionDate = transaction.date.toLocalDate()
                transactionDate.month == today.month && transactionDate.year == today.year
            }
            TimePeriod.Year -> transactions.filter { transaction -> // ✅ Yangi holat qo'shildi
                val transactionDate = transaction.date.toLocalDate()
                transactionDate.year == today.year
            }
            else -> transactions
        }
    }

    private fun calculateExpenseStatistics(
        transactions: List<Transaction>,
        categories: List<Category>
    ): List<CategoryData> {
        val expenseTransactions = transactions.filter { it.type == TransactionType.EXPENSE }

        val categoryMap = expenseTransactions
            .groupBy { it.category?.id }
            .mapValues { (_, transactions) -> transactions.sumOf { it.amount } }

        val categoryLookup = categories.associateBy { it.id }

        val result = mutableListOf<CategoryData>()

        categoryMap.forEach { (categoryId, totalAmount) ->
            if (totalAmount > 0) {
                val category = categoryLookup[categoryId]

                // ✅ Endi "Noma'lum" o'rniga lokalizatsiya qilingan unknownLabel ishlatiladi
                val categoryName = category?.name ?: unknownLabel
                val color = getCategoryColor(categoryName)

                result.add(
                    CategoryData(
                        categoryName = categoryName,
                        amount = totalAmount,
                        color = color
                    )
                )
            }
        }
        return result.sortedByDescending { it.amount }
    }
    val selectedPeriodLabelResId: StateFlow<Int> = selectedPeriod
        .map { it.labelResId }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TimePeriod.Monthly.labelResId
        )

    fun changePeriodByKey(key: String) {
        TimePeriod.fromKey(key)?.let { period ->
            _selectedPeriod.value = period
        }
    }



    // ViewModel ichida
    @RequiresApi(Build.VERSION_CODES.O)
    val topExpenseStatistics: StateFlow<List<CategoryData>> = combine(
        filteredTransactions,
        expenseCategories
    ) { transactions, categories ->
        val allStats = calculateExpenseStatistics(transactions, categories)
        // 1. Amount bo'yicha kamayish tartibida saralaymiz
        // 2. Birinchi 4 tasini olamiz (take(4))
        allStats.sortedByDescending { it.amount }.take(4)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
}