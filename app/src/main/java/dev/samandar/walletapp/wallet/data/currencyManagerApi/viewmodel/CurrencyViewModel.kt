package dev.samandar.walletapp.wallet.data.currencyManagerApi.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.samandar.walletapp.wallet.data.currencyManagerApi.entities.CurrencyRateEntity
import dev.samandar.walletapp.wallet.data.currencyManagerApi.repository.CurrencyRepository
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencyManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(
    private val repository: CurrencyRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val rates: StateFlow<List<CurrencyRateEntity>> = repository.allRates
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing = _isSyncing.asStateFlow()

    private val _errorEvent = MutableSharedFlow<String>()
    val errorEvent = _errorEvent.asSharedFlow()

    init {
        initializeCurrencyData()
    }

    private fun initializeCurrencyData() {
        viewModelScope.launch {
            // 1. Birinchi navbatda bazani default kurslar bilan to'ldiramiz (agar bo'sh bo'lsa)
            // Bu internet yo'q bo'lsa ham foydalanuvchi nol ko'rmasligini ta'minlaydi
            repository.prePopulateDatabase()

            // 2. Keyin serverdan yangilashga harakat qilamiz
            refreshRates()
        }
    }

    fun refreshRates() {
        // Internet bor-yo'qligini tekshirish (Utility funksiya orqali)
        if (!isNetworkAvailable(context)) {
            viewModelScope.launch {
                _errorEvent.emit("Internet aloqasi yo'q. Kurslar yangilanmadi.")
            }
            return
        }

        viewModelScope.launch {
            _isSyncing.value = true
            repository.syncRates()
                .onFailure { e ->
                    _errorEvent.emit("Server bilan bog'lanishda xatolik: ${e.message}")
                }
            _isSyncing.value = false
        }
    }

    fun changeCurrency(newCurrency: String) {
        viewModelScope.launch {
            _isSyncing.value = true
            CurrencyManager.saveCurrency(context, newCurrency)
            _isSyncing.value = false
        }
    }

    // Internetni tekshirish uchun yordamchi funksiya
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities != null && (
                capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)
                )
    }
}