package dev.samandar.walletapp.wallet.presentation.utils

import dev.samandar.walletapp.wallet.presentation.ui.home.activeCurrency
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.numFormat.NumberFormatManager
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs


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


fun cleanAmountInput(input: String): String {
    val decimalSeparator = DecimalFormatSymbols(NumberFormatManager.getCurrentLocale()).decimalSeparator

    val cleaned = input.filter { it.isDigit() || it == decimalSeparator }

    val parts = cleaned.split(decimalSeparator)

    return when {
        parts.size <= 1 -> cleaned
        else -> parts.first() + decimalSeparator + parts.subList(1, parts.size).joinToString("")
    }
}


fun AmountFormat(
    amount: Double,
    includeFraction: Boolean = false
): String {
    val currentLocale = NumberFormatManager.getCurrentLocale()
    val symbols = DecimalFormatSymbols(currentLocale)

    val maxFraction = if (includeFraction) 2 else 0
    val pattern = if (includeFraction) {
        "#,##0.00"
    } else {
        "#,##0"
    }

    val formatter = DecimalFormat(pattern, symbols).apply {
        maximumFractionDigits = maxFraction
        minimumFractionDigits = maxFraction
        isGroupingUsed = true
    }
    return formatter.format(abs(amount))
}