package dev.samandar.walletapp.wallet.presentation.ui.account.addAccount.currency

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

object AddAccountCurrencyManager {
    private const val PREFS_NAME = "add_account_prefs"
    private const val KEY_SELECTED_CURRENCY = "selected_account_currency"
    private const val DEFAULT_CURRENCY = "UZS"

    val supportedCurrencies = listOf("UZS", "USD", "RUB", "EUR")

    private val _localCurrency = mutableStateOf(DEFAULT_CURRENCY)
    val localCurrency: State<String> = _localCurrency

    fun initialize(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val saved = prefs.getString(KEY_SELECTED_CURRENCY, DEFAULT_CURRENCY) ?: DEFAULT_CURRENCY
        _localCurrency.value = if (saved in supportedCurrencies) saved else DEFAULT_CURRENCY
    }

    fun saveLocalCurrency(context: Context, newCurrency: String) {
        if (newCurrency !in supportedCurrencies) return
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_SELECTED_CURRENCY, newCurrency).apply()
        _localCurrency.value = newCurrency
    }
}