package dev.samandar.walletapp.wallet.presentation.ui.charts.categoryStatistic.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.samandar.walletapp.wallet.data.currencyManagerApi.repository.CurrencyRepository
import dev.samandar.walletapp.wallet.domain.model.TransactionType
import dev.samandar.walletapp.wallet.domain.usecase.transaction.GetAllTransactions
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencyManager
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.changeUpdateAmount.CurrencyEvaluator
import dev.samandar.walletapp.wallet.presentation.viewmodel.chartViewmodel.CategoryData
import dev.samandar.walletapp.wallet.presentation.viewmodel.chartViewmodel.DateFilterUtils
import dev.samandar.walletapp.wallet.presentation.viewmodel.chartViewmodel.TimeFilter
import dev.samandar.walletapp.wallet.presentation.viewmodel.chartViewmodel.getCategoryColor
import dev.samandar.walletapp.wallet.presentation.viewmodel.chartViewmodel.getCategoryIcon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class StatisticsUiState(
    val categoryData: List<CategoryData> = emptyList(),
    val totalAmount: Double = 0.0,
    val isLoading: Boolean = true,
)

@HiltViewModel
class CategoryStatisticsViewModel @Inject constructor(
    private val getAllTransactions: GetAllTransactions,
    private val currencyRepository: CurrencyRepository,
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow(TimeFilter.MONTHLY)
    val selectedFilter: StateFlow<TimeFilter> = _selectedFilter.asStateFlow()

    private val _selectedTab = MutableStateFlow(TransactionType.EXPENSE)
    val selectedTab: StateFlow<TransactionType> = _selectedTab.asStateFlow()

    // Valyuta kurslari va tanlangan valyuta oqimi
    private val ratesFlow = currencyRepository.allRates
    private val currentCurrencyFlow = CurrencyManager.getCurrencyFlow()

    // ASOSIY REAKTIV OQIM
    @RequiresApi(Build.VERSION_CODES.O)
    val statisticsUiState: StateFlow<StatisticsUiState> = combine(
        getAllTransactions(type = null),
        _selectedFilter,
        _selectedTab,
        ratesFlow,
        currentCurrencyFlow
    ) { array: Array<Any> ->
        val transactions = array[0] as List<dev.samandar.walletapp.wallet.domain.model.Transaction>
        val filter = array[1] as TimeFilter
        val tabType = array[2] as TransactionType
        val rates =
            array[3] as List<dev.samandar.walletapp.wallet.data.currencyManagerApi.entities.CurrencyRateEntity>
        val currentCurrency = array[4] as String

        // 1. Vaqt bo'yicha filterlash
        val filteredByTime = DateFilterUtils.filterByTime(transactions, filter)

        // 2. Valyuta konvertatsiyasi
        val convertedTransactions = filteredByTime.map { transaction ->
            val displayAmount = CurrencyEvaluator.convert(
                amount = transaction.amount,
                currentCurrency = currentCurrency,
                rates = rates
            )
            transaction.copy(amount = displayAmount)
        }

        // 3. Tip bo'yicha filterlash (Income/Expense)
        val filteredByType = convertedTransactions.filter { it.type == tabType }

        // 4. Guruhlash va Statistika
        val total = filteredByType.sumOf { it.amount }
        val grouped = filteredByType
            .groupBy { it.category?.name ?: "Boshqa" }
            .map { (name, list) ->
                CategoryData(
                    categoryName = name,
                    amount = list.sumOf { it.amount },
                    color = getCategoryColor(name),
                    iconResId = getCategoryIcon(name)
                )
            }
            .filter { it.amount > 0.0 }
            .sortedByDescending { it.amount }

        StatisticsUiState(
            categoryData = grouped,
            totalAmount = total,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatisticsUiState()
    )

    fun onFilterChanged(filter: TimeFilter) {
        _selectedFilter.value = filter
    }

    fun onTabChanged(type: TransactionType) {
        _selectedTab.value = type
    }
}