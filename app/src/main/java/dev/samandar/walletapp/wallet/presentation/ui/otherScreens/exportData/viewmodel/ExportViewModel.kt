package dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.samandar.walletapp.wallet.domain.model.TransactionType
import dev.samandar.walletapp.wallet.domain.repository.account.AccountRepository
import dev.samandar.walletapp.wallet.domain.repository.CategoryRepository
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.data.ExportConfig
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.useCase.ExportDataUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ExportViewModel @Inject constructor(
    private val exportDataUseCase: ExportDataUseCase,
    private val categoryRepository: CategoryRepository,
    private val accountRepository: AccountRepository,
) : ViewModel() {

    // 1. Export jarayoni holati
    private val _exportState = MutableStateFlow<ExportResult>(ExportResult.Idle)
    val exportState = _exportState.asStateFlow()

    // 2. Tanlangan tranzaksiya turi (Income/Expense/All)
    private val _selectedType = MutableStateFlow<TransactionType?>(null)
    val selectedType = _selectedType.asStateFlow()

    // 3. UI State: Sanalarni ViewModel'da saqlash (Orientation change uchun)
    var startDateMillis by mutableLongStateOf(
        Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1) }.timeInMillis
    )
    var endDateMillis by mutableLongStateOf(System.currentTimeMillis())

    // 4. Kategoriyalar oqimi (Logic o'zgarmadi, lekin UseCase buni ham yengillashtirishi mumkin edi)
    @OptIn(ExperimentalCoroutinesApi::class)
    val categories = _selectedType.flatMapLatest { type ->
        if (type == null) {
            combine(
                categoryRepository.getCategories(TransactionType.EXPENSE),
                categoryRepository.getCategories(TransactionType.INCOME)
            ) { expense, income -> expense + income }
        } else {
            categoryRepository.getCategories(type)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // 5. Hisoblar (Accounts)
    val accounts = accountRepository.getAllAccounts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setTransactionType(type: TransactionType?) {
        _selectedType.value = type
    }

    // 6. Eksportni boshlash (Asosiy funksiya)
    fun startExport(config: ExportConfig) {
        if (_exportState.value is ExportResult.Loading) return

        viewModelScope.launch {
            _exportState.value = ExportResult.Loading

            // UseCase chaqiriladi va Result natijasi bilan ishlanadi
            exportDataUseCase.execute(config)
                .onSuccess { file ->
                    _exportState.value = ExportResult.Success(file)
                }
                .onFailure { exception ->
                    _exportState.value = ExportResult.Error(
                        exception.localizedMessage ?: "Eksport jarayonida xatolik yuz berdi"
                    )
                }
        }
    }

    fun resetExportState() {
        _exportState.value = ExportResult.Idle
    }
}

// Sealed interface (UI State uchun)
sealed interface ExportResult {
    data object Idle : ExportResult
    data object Loading : ExportResult
    data class Success(val file: File) : ExportResult
    data class Error(val message: String) : ExportResult
}