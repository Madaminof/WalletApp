package dev.samandar.walletapp.wallet.presentation.ui.charts.categoryStatistic.CategoryDetail.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.samandar.walletapp.wallet.domain.model.Transaction
import dev.samandar.walletapp.wallet.domain.model.TransactionType
import dev.samandar.walletapp.wallet.domain.usecase.transaction.GetAllTransactions
import dev.samandar.walletapp.wallet.presentation.viewmodel.chartViewmodel.DateFilterUtils
import dev.samandar.walletapp.wallet.presentation.viewmodel.chartViewmodel.TimeFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class CategoryDetailViewModel @Inject constructor(
    private val getAllTransactions: GetAllTransactions,
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow(TimeFilter.MONTHLY)
    val selectedFilter: StateFlow<TimeFilter> = _selectedFilter.asStateFlow()

    private val _categoryParams = MutableStateFlow<Pair<String, TransactionType>?>(null)


    fun setCategoryParams(name: String, type: TransactionType) {
        val decodedName = java.net.URLDecoder.decode(name, "UTF-8")

        if (_categoryParams.value?.first != decodedName) {
            _categoryParams.value = decodedName to type
        }
    }

    val detailUiState: StateFlow<CategoryDetailUiState> = combine(
        getAllTransactions(type = null),
        _selectedFilter,
        _categoryParams
    ) { transactions, filter, params ->
        if (params == null) return@combine CategoryDetailUiState(isLoading = true)

        val (name, type) = params
        val timeFiltered = DateFilterUtils.filterByTime(transactions, filter)

        val filteredList = timeFiltered.filter {
            it.category?.name == name && it.type == type
        }.sortedByDescending { it.date }

        val total = filteredList.sumOf { it.amount }
        val avg = if (filteredList.isNotEmpty()) total / filteredList.size else 0.0
        val max = filteredList.maxOfOrNull { it.amount } ?: 0.0

        CategoryDetailUiState(
            transactions = filteredList,
            totalAmount = total,
            avgAmount = avg,
            maxAmount = max,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CategoryDetailUiState(isLoading = true)
    )

    fun onFilterChanged(filter: TimeFilter) {
        _selectedFilter.value = filter
    }
}

// Detail uchun maxsus State
data class CategoryDetailUiState(
    val transactions: List<Transaction> = emptyList(),
    val totalAmount: Double = 0.0,
    val avgAmount: Double = 0.0,
    val maxAmount: Double = 0.0,
    val isLoading: Boolean = false,
)