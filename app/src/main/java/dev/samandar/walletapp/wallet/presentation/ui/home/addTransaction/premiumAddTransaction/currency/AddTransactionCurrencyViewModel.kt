package dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.currency

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.samandar.walletapp.wallet.data.currencyManagerApi.entities.CurrencyRateEntity
import dev.samandar.walletapp.wallet.data.currencyManagerApi.repository.CurrencyRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTransactionCurrencyViewModel @Inject constructor(
    private val repository: CurrencyRepository,
    @ApplicationContext private val context: Context,
    // Agar alohida NetworkMonitor bo'lsa, bu yerga Inject qilinadi
) : ViewModel() {

    // 1. UI State uchun maxsus klas (Soddalik uchun shu yerda)
    data class CurrencyUiState(
        val isSyncing: Boolean = false,
        val lastUpdated: Long = 0L,
        val isOffline: Boolean = false
    )

    private val _uiState = MutableStateFlow(CurrencyUiState())
    val uiState = _uiState.asStateFlow()

    // 2. Room'dan kelayotgan oqim
    val rates: StateFlow<List<CurrencyRateEntity>> = repository.allRates
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _errorEvent = MutableSharedFlow<String>()
    val errorEvent = _errorEvent.asSharedFlow()

    init {
        // Managerlarni xavfsiz ishga tushirish
        viewModelScope.launch {
            AddTransactionCurrencyManager.initialize(context)
            // Birinchi marta bazani default kurslar bilan to'ldirish (Offline bo'lsa ham nol ko'rinmasligi uchun)
            repository.prePopulateDatabase()
            refreshRates()
        }
    }

    /**
     * Kurslarni yangilash - Professional Error Handling bilan
     */
    fun refreshRates() {
        if (!isNetworkAvailable()) {
            _uiState.update { it.copy(isOffline = true) }
            viewModelScope.launch {
                _errorEvent.emit("Internet aloqasi yo'q. Bazadagi ma'lumotlar ko'rsatilmoqda.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, isOffline = false) }

            repository.syncRates()
                .onSuccess {
                    _uiState.update { it.copy(isSyncing = false, lastUpdated = System.currentTimeMillis()) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isSyncing = false) }
                    // Xatolik xabarini tahlil qilish (Productionda juda muhim)
                    val errorMessage = when (e) {
                        is java.net.UnknownHostException -> "Serverga ulanib bo'lmadi"
                        is java.net.SocketTimeoutException -> "Server juda sekin javob beryapti"
                        else -> e.message ?: "Noma'lum xatolik yuz berdi"
                    }
                    _errorEvent.emit("Kurslarni yangilab bo'lmadi: $errorMessage")
                }
        }
    }

    /**
     * Valyutani o'zgartirish
     */
    fun changeLocalCurrency(newCurrency: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true) }
            runCatching {
                AddTransactionCurrencyManager.saveLocalCurrency(context, newCurrency)
            }.onFailure {
                _errorEvent.emit("Valyutani saqlashda xatolik yuz berdi")
            }
            _uiState.update { it.copy(isSyncing = false) }
        }
    }

    // Yordamchi funksiya: Internetni tekshirish
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities != null && (
                capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)
                )
    }
}