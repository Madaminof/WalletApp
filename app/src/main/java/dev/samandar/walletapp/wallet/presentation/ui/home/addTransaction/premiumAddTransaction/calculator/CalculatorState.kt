package dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.calculator


data class CalculatorState(
    val display: String = "0",
    val pendingOperation: String? = null,
    val firstOperand: Double? = null,
    val isNewInput: Boolean = true
)