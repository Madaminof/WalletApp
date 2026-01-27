package dev.samandar.walletapp.wallet.presentation.ui.charts.categoryStatistic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.samandar.walletapp.wallet.domain.model.TransactionType
import dev.samandar.walletapp.wallet.domain.usecase.transaction.GetAllTransactions
import dev.samandar.walletapp.wallet.presentation.viewmodel.chartViewmodel.CategoryData
import dev.samandar.walletapp.wallet.presentation.viewmodel.chartViewmodel.DateFilterUtils
import dev.samandar.walletapp.wallet.presentation.viewmodel.chartViewmodel.TimeFilter
import dev.samandar.walletapp.wallet.presentation.viewmodel.chartViewmodel.getCategoryColor
import dev.samandar.walletapp.wallet.presentation.viewmodel.chartViewmodel.getCategoryIcon
import kotlinx.coroutines.flow.*
import javax.inject.Inject


data class StatisticsUiState(
    val categoryData: List<CategoryData> = emptyList(),
    val totalAmount: Double = 0.0,
    val isLoading: Boolean = true
)

@HiltViewModel
class CategoryStatisticsViewModel @Inject constructor(
    private val getAllTransactions: GetAllTransactions
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow(TimeFilter.MONTHLY)
    val selectedFilter: StateFlow<TimeFilter> = _selectedFilter.asStateFlow()

    private val _selectedTab = MutableStateFlow(TransactionType.EXPENSE)
    val selectedTab: StateFlow<TransactionType> = _selectedTab.asStateFlow()

    val statisticsUiState: StateFlow<StatisticsUiState> = combine(
        getAllTransactions(type = null),
        _selectedFilter,
        _selectedTab
    ) { transactions, filter, tabType ->
        val filteredByTime = DateFilterUtils.filterByTime(transactions, filter)
        val filteredByType = filteredByTime.filter { it.type == tabType }

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

        StatisticsUiState(categoryData = grouped, totalAmount = total, isLoading = false)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatisticsUiState()
    )

    fun onFilterChanged(filter: TimeFilter) { _selectedFilter.value = filter }
    fun onTabChanged(type: TransactionType) { _selectedTab.value = type }
}