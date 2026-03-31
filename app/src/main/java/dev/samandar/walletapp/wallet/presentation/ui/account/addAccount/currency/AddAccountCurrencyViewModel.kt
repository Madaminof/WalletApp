package dev.samandar.walletapp.wallet.presentation.ui.account.addAccount.currency

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.samandar.walletapp.wallet.data.currencyManagerApi.entities.CurrencyRateEntity
import dev.samandar.walletapp.wallet.data.currencyManagerApi.repository.CurrencyRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddAccountCurrencyViewModel @Inject constructor(
    private val repository: CurrencyRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // 1. UI State - barcha holatlar bitta obyektdi
    data class AddAccountCurrencyUiState(
        val isSyncing: Boolean = false,
        val isOffline: Boolean = false,
        val lastSyncTime: Long = 0L
    )

    private val _uiState = MutableStateFlow(AddAccountCurrencyUiState())
    val uiState = _uiState.asStateFlow()

    // 2. Bazadan kurslar oqimi
    val rates: StateFlow<List<CurrencyRateEntity>> = repository.allRates
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 3. Bir martalik xabarlar (Snackbar uchun)
    private val _errorEvent = MutableSharedFlow<String>()
    val errorEvent = _errorEvent.asSharedFlow()

    init {
        // Managerlarni va bazani tayyorlash
        viewModelScope.launch {
            AddAccountCurrencyManager.initialize(context)
            // Baza bo'sh bo'lsa default kurslarni yozib qo'yamiz
            repository.prePopulateDatabase()
            // Ilovaga kirganda avtomatik yangilashga harakat qilamiz
            refreshRates()
        }
    }

    /**
     * Kurslarni serverdan yangilash
     */
    fun refreshRates() {
        if (!isNetworkAvailable()) {
            _uiState.update { it.copy(isOffline = true) }
            viewModelScope.launch {
                _errorEvent.emit("Internet yo'q. Oxirgi saqlangan kurslar ko'rsatilmoqda.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, isOffline = false) }

            repository.syncRates()
                .onSuccess {
                    _uiState.update {
                        it.copy(isSyncing = false, lastSyncTime = System.currentTimeMillis())
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isSyncing = false) }
                    val message = when(e) {
                        is java.net.UnknownHostException -> "Server bilan aloqa yo'q"
                        else -> e.message ?: "Kutilmagan xatolik"
                    }
                    _errorEvent.emit("Yangilashda xato: $message")
                }
        }
    }

    /**
     * Yangi hisob uchun tanlangan valyutani saqlash
     */
    fun changeLocalCurrency(newCurrency: String) {
        viewModelScope.launch {
            // SharedPreferences operatsiyalari IO thread'da bo'lgani ma'qul
            AddAccountCurrencyManager.saveLocalCurrency(context, newCurrency)
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        return capabilities != null && (
                capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)
                )
    }
}