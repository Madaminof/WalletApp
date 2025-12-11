package com.example.walletapp.wallet.presentation.utils

import com.example.walletapp.wallet.presentation.ui.home.activeCurrency
import java.text.NumberFormat
import java.util.Locale


private fun formatUzs(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("uz", "UZ")).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 2
        currency = java.util.Currency.getInstance("$activeCurrency")
    }
    val formatted = formatter.format(amount)
    val cleanFormatted = formatted.replace("-", "").replace("(", "").replace(")", "").trim()
    return if (amount < 0) "-$cleanFormatted" else cleanFormatted
}
