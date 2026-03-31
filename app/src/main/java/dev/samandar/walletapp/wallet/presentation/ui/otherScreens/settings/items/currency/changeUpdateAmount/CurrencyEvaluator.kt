package dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.changeUpdateAmount

import dev.samandar.walletapp.wallet.data.currencyManagerApi.entities.CurrencyRateEntity

object CurrencyEvaluator {

    fun convert(
        amount: Double,
        currentCurrency: String,
        rates: List<CurrencyRateEntity>
    ): Double {
        if (currentCurrency == "UZS") return amount

        val rate = rates.find { it.code == currentCurrency }?.rate ?: return amount

        return amount / rate
    }

    fun convertToBase(
        amount: Double,
        currentCurrency: String,
        rates: List<CurrencyRateEntity>
    ): Double {
        if (currentCurrency == "UZS") return amount
        val rate = rates.find { it.code == currentCurrency }?.rate ?: return amount

        // 10 $ * 12800 = 128,000 UZS
        return amount * rate
    }
}