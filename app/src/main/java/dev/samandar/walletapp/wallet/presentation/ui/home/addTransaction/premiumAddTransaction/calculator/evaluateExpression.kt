package dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.calculator
import java.text.DecimalFormat



private fun performPendingOperation(
    currentDisplay: String,
    firstOperand: Double?,
    pendingOperation: String?
): Double? {
    if (firstOperand == null || pendingOperation == null) return currentDisplay.toDoubleOrNull()
    val secondOperand = currentDisplay.toDoubleOrNull() ?: return null
    return when (pendingOperation) {
        "+" -> firstOperand + secondOperand
        "-" -> firstOperand - secondOperand
        "×" -> firstOperand * secondOperand
        "÷" -> if (secondOperand == 0.0) Double.NaN else firstOperand / secondOperand
        else -> null
    }
}

fun evaluateExpression(state: CalculatorState, action: CalculatorAction, onSave: (Double) -> Unit): CalculatorState {
    var display = state.display
    var firstOperand = state.firstOperand
    var pendingOperation = state.pendingOperation
    var isNewInput = state.isNewInput
    val format = DecimalFormat("0.########")

    when (action) {
        is CalculatorAction.Number -> {
            if (display == "Error" || isNewInput) display = action.number
            else if (action.number == "000") display += "000"
            else if (display == "0") display = action.number
            else display += action.number
            isNewInput = false
        }
        is CalculatorAction.Operation -> {
            if (firstOperand == null) firstOperand = display.toDoubleOrNull()
            else if (!isNewInput) {
                val result = performPendingOperation(display, firstOperand, pendingOperation)
                firstOperand = result
                display = if (result?.isNaN() == true) "Error" else format.format(result)
            }
            pendingOperation = action.operation
            isNewInput = true
        }
        CalculatorAction.Decimal -> {
            if (isNewInput) { display = "0."; isNewInput = false }
            else if (!display.contains(".")) display += "."
        }
        CalculatorAction.Calculate -> {
            val result = performPendingOperation(display, firstOperand, pendingOperation)
            if (result?.isNaN() == true) display = "Error"
            else if (result != null) display = format.format(result)
            firstOperand = null
            pendingOperation = null
            isNewInput = true
        }
        CalculatorAction.Delete -> {
            if (display != "Error" && !isNewInput) {
                if (display.length > 1 && display != "0") display = display.dropLast(1)
                else { display = "0"; isNewInput = true }
            } else if (display == "Error") { display = "0"; isNewInput = true }
        }
        CalculatorAction.Clear -> return CalculatorState(display = "0")
        CalculatorAction.Save -> {
            val result = performPendingOperation(display, firstOperand, pendingOperation)
                ?: display.toDoubleOrNull() ?: 0.0
            if (result.isNaN()) display = "Error"
            else { onSave(result); display = format.format(result) }
            firstOperand = null; pendingOperation = null; isNewInput = true
        }
    }
    return state.copy(display = display, firstOperand = firstOperand, pendingOperation = pendingOperation, isNewInput = isNewInput)
}
