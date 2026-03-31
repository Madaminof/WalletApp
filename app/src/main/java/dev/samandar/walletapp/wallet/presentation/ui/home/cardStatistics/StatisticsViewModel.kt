package dev.samandar.walletapp.wallet.presentation.ui.home.cardStatistics

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.samandar.walletapp.utils.FilterKeys
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.data.currencyManagerApi.repository.CurrencyRepository
import dev.samandar.walletapp.wallet.domain.model.Category
import dev.samandar.walletapp.wallet.domain.model.Transaction
import dev.samandar.walletapp.wallet.domain.model.TransactionType
import dev.samandar.walletapp.wallet.domain.usecase.category.GetCategoriesByType
import dev.samandar.walletapp.wallet.domain.usecase.transaction.GetAllTransactions
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencyManager
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.changeUpdateAmount.CurrencyEvaluator
import dev.samandar.walletapp.wallet.presentation.viewmodel.chartViewmodel.CategoryData
import dev.samandar.walletapp.wallet.presentation.viewmodel.chartViewmodel.getCategoryColor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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
    private val currencyRepository: CurrencyRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val unknownLabel: String = context.getString(Strings.unknown)

    // 1. Reaktiv oqimlar: Kurslar va Tanlangan Valyuta
    private val ratesFlow = currencyRepository.allRates
    private val currentCurrencyFlow = CurrencyManager.getCurrencyFlow()

    private val _selectedPeriod = MutableStateFlow<TimePeriod>(TimePeriod.Monthly)
    val selectedPeriod: StateFlow<TimePeriod> = _selectedPeriod.asStateFlow()

    // 2. Valyuta o'zgarganda avtomatik hisoblanadigan tranzaksiyalar
    @RequiresApi(Build.VERSION_CODES.O)
    val filteredTransactions: StateFlow<List<Transaction>> = combine(
        getAllTransactions(type = null),
        _selectedPeriod,
        ratesFlow,
        currentCurrencyFlow
    ) { array: Array<Any> ->
        val transactions = array[0] as List<Transaction>
        val period = array[1] as TimePeriod
        val rates = array[2] as List<dev.samandar.walletapp.wallet.data.currencyManagerApi.entities.CurrencyRateEntity>
        val currentCurrency = array[3] as String

        // Valyutani konvertatsiya qilish
        val converted = transactions.map { transaction ->
            val displayAmount = CurrencyEvaluator.convert(
                amount = transaction.amount,
                currentCurrency = currentCurrency,
                rates = rates
            )
            transaction.copy(amount = displayAmount)
        }

        // Davr bo'yicha filterlash
        filterTransactionsByPeriod(converted, period)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // 3. Kategoriyalar oqimi
    val expenseCategories = getCategoriesByType(TransactionType.EXPENSE)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // 4. Yakuniy Statistika (Eski kodingdagi calculateExpenseStatistics mantiqi bilan)
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

    // 5. Jami summa (Valyuta o'zgarsa bu ham o'zgaradi)
    @RequiresApi(Build.VERSION_CODES.O)
    val totalExpense: StateFlow<Double> = expenseStatistics
        .map { it.sumOf { data -> data.amount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // 6. Tanlangan period labeli uchun
    val selectedPeriodLabelResId: StateFlow<Int> = selectedPeriod
        .map { it.labelResId }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TimePeriod.Monthly.labelResId)

    // --- FUNKSIYALAR (O'zgarishsiz qoldi) ---

    fun changePeriodByKey(key: String) {
        TimePeriod.fromKey(key)?.let { _selectedPeriod.value = it }
    }

    private fun calculateExpenseStatistics(
        transactions: List<Transaction>,
        categories: List<Category>,
    ): List<CategoryData> {
        val expenseTransactions = transactions.filter { it.type == TransactionType.EXPENSE }
        val categoryMap = expenseTransactions
            .groupBy { it.category?.id }
            .mapValues { (_, txs) -> txs.sumOf { it.amount } }

        val categoryLookup = categories.associateBy { it.id }
        val result = mutableListOf<CategoryData>()

        categoryMap.forEach { (categoryId, totalAmount) ->
            if (totalAmount > 0) {
                val category = categoryLookup[categoryId]
                val categoryName = category?.name ?: unknownLabel
                result.add(
                    CategoryData(
                        categoryName = categoryName,
                        amount = totalAmount,
                        color = getCategoryColor(categoryName)
                    )
                )
            }
        }
        return result.sortedByDescending { it.amount }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun filterTransactionsByPeriod(transactions: List<Transaction>, period: TimePeriod): List<Transaction> {
        val today = LocalDate.now()
        return when (period) {
            TimePeriod.AllTime -> transactions
            TimePeriod.Daily -> transactions.filter { it.date.toLocalDate().isEqual(today) }
            TimePeriod.Weekly -> {
                val weekFields = WeekFields.of(Locale.getDefault())
                val currentWeek = today.get(weekFields.weekOfWeekBasedYear())
                transactions.filter {
                    val date = it.date.toLocalDate()
                    date.year == today.year && date.get(weekFields.weekOfWeekBasedYear()) == currentWeek
                }
            }
            TimePeriod.Monthly -> transactions.filter {
                val date = it.date.toLocalDate()
                date.month == today.month && date.year == today.year
            }
            TimePeriod.Year -> transactions.filter { it.date.toLocalDate().year == today.year }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun Long.toLocalDate(): LocalDate = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()
}