package dev.samandar.walletapp.wallet.presentation.ui.charts.categoryStatistic.CategoryDetail.viewmodel


import android.os.Build
import androidx.annotation.RequiresApi
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
        val decodedName = try {
            java.net.URLDecoder.decode(name, "UTF-8")
        } catch (e: Exception) { name }

        if (_categoryParams.value?.first != decodedName || _categoryParams.value?.second != type) {
            _categoryParams.value = decodedName to type
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    val detailUiState: StateFlow<CategoryDetailUiState> = combine(
        getAllTransactions(type = null),
        _selectedFilter,
        _categoryParams
    ) { allTransactions, filter, params ->
        if (params == null) return@combine CategoryDetailUiState(isLoading = true)

        val (name, type) = params

        // 1. Tanlangan vaqt bo'yicha filtr (Joriy davr)
        val timeFiltered = DateFilterUtils.filterByTime(allTransactions, filter)
        val filteredList = timeFiltered.filter {
            it.category?.name == name && it.type == type
        }

        // 2. Umumiy summa
        val total = filteredList.sumOf { it.amount }

        // 3. Eng ko'p xarajat/daromad bo'lgan kunni aniqlash (Peak Insight)
        val transactionsByDate = filteredList.groupBy {
            java.time.Instant.ofEpochMilli(it.date)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate()
        }

        // Har bir kunning umumiy summasini hisoblab, eng kattasini topamiz
        val maxDayEntry = transactionsByDate.maxByOrNull { entry ->
            entry.value.sumOf { it.amount }
        }

        val peakDate = maxDayEntry?.key
        val peakAmount = maxDayEntry?.value?.sumOf { it.amount } ?: 0.0

        CategoryDetailUiState(
            transactions = filteredList.sortedByDescending { it.date },
            totalAmount = total,
            peakDate = peakDate,
            peakAmount = peakAmount,
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

/**
 * UI State: Endi foiz o'rniga eng yuqori kun ma'lumotlari
 */
data class CategoryDetailUiState(
    val transactions: List<Transaction> = emptyList(),
    val totalAmount: Double = 0.0,
    val peakAmount: Double = 0.0,
    val peakDate: java.time.LocalDate? = null,
    val isLoading: Boolean = false,
)