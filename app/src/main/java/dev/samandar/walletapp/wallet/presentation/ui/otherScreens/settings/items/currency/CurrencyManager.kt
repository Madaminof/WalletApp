package dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onStart

object CurrencyManager {

    private const val PREFS_NAME = "wallet_app_settings"
    private const val KEY_CURRENCY = "selected_currency"
    private const val DEFAULT_CURRENCY = "UZS"

    val supportedCurrencies = listOf("UZS", "USD", "RUB", "EUR")

    // replay = 1 oxirgi qiymatni eslab qoladi va yangi obunachilarga uzatadi
    private val _currencyFlow = MutableSharedFlow<String>(replay = 1)

    // UI (Compose) uchun
    private val _currentCurrency = mutableStateOf(DEFAULT_CURRENCY)
    val currentCurrency: State<String> = _currentCurrency

    /**
     * Ilova yonganda (MainActivity'da) bir marta chaqiriladi.
     */
    fun initialize(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedCurrency = prefs.getString(KEY_CURRENCY, DEFAULT_CURRENCY) ?: DEFAULT_CURRENCY

        val finalCurrency =
            if (savedCurrency in supportedCurrencies) savedCurrency else DEFAULT_CURRENCY

        _currentCurrency.value = finalCurrency
        _currencyFlow.tryEmit(finalCurrency) // Oqimni boshlang'ich qiymat bilan to'ldiramiz
    }

    /**
     * Valyutani saqlaydi va barcha ViewModel-larga "xabar" yuboradi.
     */
    fun saveCurrency(context: Context, newCurrency: String) {
        if (newCurrency !in supportedCurrencies) return

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_CURRENCY, newCurrency).apply()

        // 1. Compose State'ni yangilaymiz (UI uchun)
        _currentCurrency.value = newCurrency

        // 2. Flow'ga yangi qiymat yuboramiz (ViewModel'lar eshitishi uchun)
        // BU ENG MUHIM QISMI!
        _currencyFlow.tryEmit(newCurrency)
    }

    /**
     * ViewModel'lar uchun reaktiv oqim.
     */
    fun getCurrencyFlow(): Flow<String> {
        return _currencyFlow.asSharedFlow()
            .onStart {
                // Agar oqim bo'sh bo'lsa, joriy qiymatni berib yuboramiz
                emit(_currentCurrency.value)
            }
            .distinctUntilChanged() // Faqat qiymat haqiqatda o'zgarsa ishlashi uchun
    }
}