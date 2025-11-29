package com.example.walletapp.wallet.presentation.ui.home.cardStatistics

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletapp.wallet.domain.model.Category
import com.example.walletapp.wallet.domain.model.Transaction
import com.example.walletapp.wallet.domain.model.TransactionType
import com.example.walletapp.wallet.domain.usecase.category.GetCategoriesByType
import com.example.walletapp.wallet.domain.usecase.transaction.GetAllTransactions
import com.example.walletapp.wallet.presentation.viewmodel.CategoryData
import com.example.walletapp.wallet.presentation.viewmodel.getCategoryColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.WeekFields
import java.util.Locale
import javax.inject.Inject

sealed class TimePeriod(val name: String) {
    object Daily : TimePeriod("Daily")
    object Weekly : TimePeriod("Weekly")
    object Monthly : TimePeriod("Monthly")
    object AllTime : TimePeriod("AllTime")
}

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getAllTransactions: GetAllTransactions,
    private val getCategoriesByType: GetCategoriesByType,
) : ViewModel() {
    private val allTransactionsFlow = getAllTransactions(type = null)

    private val _selectedPeriod = MutableStateFlow<TimePeriod>(TimePeriod.Monthly)
    val selectedPeriod: StateFlow<TimePeriod> = _selectedPeriod.asStateFlow()

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

            else -> {transactions}
        }
    }

    private fun calculateExpenseStatistics(
        transactions: List<Transaction>,
        categories: List<Category>
    ): List<CategoryData> {
        val expenseTransactions = transactions.filter { it.type == TransactionType.EXPENSE }

        val categoryMap = expenseTransactions
            .groupBy { it.category.id }
            .mapValues { (_, transactions) -> transactions.sumOf { it.amount } }

        val categoryLookup = categories.associateBy { it.id }

        val result = mutableListOf<CategoryData>()

        categoryMap.forEach { (categoryId, totalAmount) ->
            if (totalAmount > 0) {
                val category = categoryLookup[categoryId]
                val color = getCategoryColor(category?.name ?: "Noma'lum")
                result.add(
                    CategoryData(
                        categoryName = category?.name ?: "Noma'lum",
                        amount = totalAmount,
                        color = color
                    )
                )
            }
        }
        return result.sortedByDescending { it.amount }
    }

    fun changePeriod(period: TimePeriod) {
        _selectedPeriod.value = period
    }
}