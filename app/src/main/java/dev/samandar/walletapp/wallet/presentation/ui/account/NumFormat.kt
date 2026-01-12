package dev.samandar.walletapp.wallet.presentation.ui.account

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.text.NumberFormat
import java.util.Locale

@Composable
fun NumFormatter(
    amountText: String,
    includeFraction: Boolean = true
): String {
    val amount = amountText.toDoubleOrNull() ?: 0.0

    val actualMinFraction = if (amountText.contains('.')) 2 else 0
    val maxFraction = if (includeFraction) 2 else 0

    val numberFormatter = remember {
        NumberFormat.getNumberInstance(Locale("uz", "UZ")).apply {
            minimumFractionDigits = actualMinFraction
            maximumFractionDigits = maxFraction
        }
    }

    return numberFormatter.format(amount)
}