package dev.samandar.walletapp.wallet.presentation.ui.charts.categoryStatistic.CategoryDetail.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.samandar.walletapp.wallet.data.currencyManagerApi.repository.CurrencyRepository
import dev.samandar.walletapp.wallet.domain.model.Transaction
import dev.samandar.walletapp.wallet.domain.model.TransactionType
import dev.samandar.walletapp.wallet.domain.usecase.transaction.GetAllTransactions
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencyManager
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.changeUpdateAmount.CurrencyEvaluator
import dev.samandar.walletapp.wallet.presentation.viewmodel.chartViewmodel.DateFilterUtils
import dev.samandar.walletapp.wallet.presentation.viewmodel.chartViewmodel.TimeFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class CategoryDetailUiState(
    val transactions: List<Transaction> = emptyList(),
    val totalAmount: Double = 0.0,
    val peakAmount: Double = 0.0,
    val peakDate: LocalDate? = null,
    val isLoading: Boolean = false,
    val currentCurrency: String = "",
)

@HiltViewModel
class CategoryDetailViewModel @Inject constructor(
    private val getAllTransactions: GetAllTransactions,
    private val currencyRepository: CurrencyRepository,
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow(TimeFilter.MONTHLY)
    val selectedFilter: StateFlow<TimeFilter> = _selectedFilter.asStateFlow()

    private val _categoryParams = MutableStateFlow<Pair<String, TransactionType>?>(null)

    private val ratesFlow = currencyRepository.allRates
    private val currentCurrencyFlow = CurrencyManager.getCurrencyFlow()

    fun setCategoryParams(name: String, type: TransactionType) {
        val decodedName = try {
            java.net.URLDecoder.decode(name, "UTF-8")
        } catch (e: Exception) {
            name
        }
        if (_categoryParams.value?.first != decodedName || _categoryParams.value?.second != type) {
            _categoryParams.value = decodedName to type
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    val detailUiState: StateFlow<CategoryDetailUiState> = combine(
        getAllTransactions(type = null),
        _selectedFilter,
        _categoryParams,
        ratesFlow,
        currentCurrencyFlow
    ) { allTransactions, filter, params, rates, currentCurrency -> // ✅ Parametrlarni alohida chiqardik

        if (params == null) return@combine CategoryDetailUiState(isLoading = true)
        val (name, type) = params

        // 1. Vaqt va Kategoriya bo'yicha filterlash
        val timeFiltered = DateFilterUtils.filterByTime(allTransactions, filter)
        val categoryFiltered = timeFiltered.filter { it.category?.name == name && it.type == type }

        // 2. Valyuta konvertatsiyasi
        val convertedTransactions = categoryFiltered.map { tx ->
            val displayAmount = CurrencyEvaluator.convert(
                amount = tx.amount,
                currentCurrency = currentCurrency,
                rates = rates
            )
            tx.copy(amount = displayAmount)
        }.sortedByDescending { it.date }

        // 3. Umumiy summa
        val total = convertedTransactions.sumOf { it.amount }

        // 4. Peak Insight (Eng yuqori kunni aniqlash)
        val transactionsByDate = convertedTransactions.groupBy {
            Instant.ofEpochMilli(it.date).atZone(ZoneId.systemDefault()).toLocalDate()
        }

        val maxDayEntry = transactionsByDate.maxByOrNull { entry ->
            entry.value.sumOf { it.amount }
        }

        CategoryDetailUiState(
            transactions = convertedTransactions,
            totalAmount = total,
            peakDate = maxDayEntry?.key,
            peakAmount = maxDayEntry?.value?.sumOf { it.amount } ?: 0.0,
            isLoading = false,
            currentCurrency = currentCurrency
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