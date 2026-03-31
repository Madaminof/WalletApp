package dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.currency

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

object AddTransactionCurrencyManager {

    private const val PREFS_NAME = "add_transaction_settings"
    private const val KEY_LOCAL_CURRENCY = "local_currency"
    private const val DEFAULT_CURRENCY = "UZS"

    // Add Transaction ekranida ishlatiladigan barcha valyutalar
    val supportedCurrencies = listOf("UZS", "USD", "RUB", "EUR")

    private val _localCurrency = mutableStateOf(DEFAULT_CURRENCY)
    val localCurrency: State<String> = _localCurrency

    /**
     * Ilova yonganda MainActivity yoki AddTransactionViewModel initda chaqiriladi
     */
    fun initialize(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val saved = prefs.getString(KEY_LOCAL_CURRENCY, DEFAULT_CURRENCY) ?: DEFAULT_CURRENCY

        // Agar saqlangan valyuta ro'yxatda bo'lsa uni olamiz, bo'lmasa default
        _localCurrency.value = if (saved in supportedCurrencies) saved else DEFAULT_CURRENCY
    }

    /**
     * Faqat Add Transaction ekranidagi valyutani saqlash uchun
     */
    fun saveLocalCurrency(context: Context, newCurrency: String) {
        if (newCurrency !in supportedCurrencies) return

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LOCAL_CURRENCY, newCurrency).apply()

        // Bu faqat lokal state-ni yangilaydi, Global managerga tegmaydi
        _localCurrency.value = newCurrency
    }
}