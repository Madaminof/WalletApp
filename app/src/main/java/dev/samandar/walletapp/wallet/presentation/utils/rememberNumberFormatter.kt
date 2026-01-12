package dev.samandar.walletapp.wallet.presentation.utils

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencyManager
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.numFormat.NumberFormatManager
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Currency
import java.util.Date
import java.util.Locale
import kotlin.math.absoluteValue

// UZS -> so'm
/*@Composable
fun getCurrencySymbol(currencyCode: String): String {
    return remember(currencyCode) {
        try {
            val locale = if (currencyCode == "UZS") Locale("uz", "UZ") else Locale.getDefault()
            Currency.getInstance(currencyCode).getSymbol(locale)
        } catch (e: Exception) {
            currencyCode
        }
    }
}*/
@Composable
fun getCurrencySymbol(currencyCode: String): String {
    return remember(currencyCode) {
        when (currencyCode) {
            "UZS" -> "so'm" // Mahalliy belgi
            "USD" -> "$"    // Eng keng tarqalgan belgi
            "RUB" -> "₽"    // Rasmiy Rubl belgisi
            "EUR" -> "€"    // Yevro uchun
            else -> {
                try {
                    Currency.getInstance(currencyCode).getSymbol(Locale.US)
                } catch (e: Exception) {
                    currencyCode
                }
            }
        }
    }
}

@Composable
fun rememberNumberFormatter(
    maximumFractionDigits: Int
): (Double) -> String {

    val numberLocale = NumberFormatManager.getCurrentLocale()

    return remember(numberLocale, maximumFractionDigits) {

        val formatter = NumberFormat.getNumberInstance(numberLocale).apply {
            this.maximumFractionDigits = maximumFractionDigits
            this.minimumFractionDigits = maximumFractionDigits
            this.isGroupingUsed = true
        }
        return@remember { amount ->
            formatter.format(amount).trim()
        }
    }
}


@Composable
fun formatAmountWithCurrency(
    amount: Double,
    includeFraction: Boolean = false
): String {

    val maxFraction = if (includeFraction) 2 else 0

    val formatNumber = rememberNumberFormatter(maximumFractionDigits = maxFraction)
    val activeCurrencyCode by CurrencyManager.currentCurrency
    val activeCurrencySymbol = getCurrencySymbol(activeCurrencyCode)

    val formattedAmount = formatNumber(amount.absoluteValue)
    val sign = if (amount < 0) "-" else ""

    // Natija: 1 000 000.00 so'm ko'rinishi
    return "$sign$formattedAmount $activeCurrencySymbol"
}

@Composable
fun NumFormat(
    amount: Double,
    includeFraction: Boolean = false
): String {
    val maxFraction = if (includeFraction) 2 else 0

    val formatNumber = rememberNumberFormatter(maximumFractionDigits = maxFraction)
    val formattedAmount = formatNumber(amount.absoluteValue)

    return formattedAmount
}

@Composable
fun FormatAmount(
    amount: Double
): String {
    return formatAmountWithCurrency(
        amount = amount,
        includeFraction = false
    )
}

fun FormatDate(timestamp: Long): String {
    val baseLocale = NumberFormatManager.getCurrentLocale()
    val displayLocale = when (baseLocale.language) {
        "en" -> Locale.ENGLISH
        "fr" -> Locale.FRENCH
        "de" -> Locale.GERMAN
        else -> Locale.getDefault()
    }
    val uzbekLocale = Locale("uz", "UZ")
    val russianLocale = Locale("ru", "RU")
    val formatterLocale = Locale.getDefault()
    val pattern = "dd MMMM, yyyy"

    val formatter = SimpleDateFormat(pattern, formatterLocale)

    val date = Date(timestamp)
    return formatter.format(date)
}