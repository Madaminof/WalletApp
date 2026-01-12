package dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.calculator


sealed class CalculatorAction {
    data class Number(val number: String) : CalculatorAction()
    data class Operation(val operation: String) : CalculatorAction()
    object Calculate : CalculatorAction()
    object Delete : CalculatorAction()
    object Clear : CalculatorAction()
    object Decimal : CalculatorAction()
    object Save : CalculatorAction()
}