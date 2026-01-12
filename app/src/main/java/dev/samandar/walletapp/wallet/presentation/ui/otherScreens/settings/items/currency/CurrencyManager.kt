package dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

object CurrencyManager {

    private const val PREFS_NAME = "wallet_app_settings"
    private const val KEY_CURRENCY = "selected_currency"
    private const val DEFAULT_CURRENCY = "UZS"

    val supportedCurrencies = listOf("UZS", "USD", "RUB", "EUR")

    private val _currentCurrency = mutableStateOf(DEFAULT_CURRENCY)
    val currentCurrency: State<String> = _currentCurrency

    fun initialize(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val savedCurrency = prefs.getString(KEY_CURRENCY, DEFAULT_CURRENCY) ?: DEFAULT_CURRENCY

        if (supportedCurrencies.contains(savedCurrency)) {
            _currentCurrency.value = savedCurrency
        } else {
            _currentCurrency.value = DEFAULT_CURRENCY
        }
    }

    fun saveCurrency(context: Context, newCurrency: String) {
        if (newCurrency !in supportedCurrencies) {
            return
        }

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_CURRENCY, newCurrency).apply()

        _currentCurrency.value = newCurrency
    }
}