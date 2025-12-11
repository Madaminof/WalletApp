package com.example.walletapp.wallet.presentation.ui.home.addTransaction.addtransactionScreen2

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardBackspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.ui.theme.expenseColor
import com.example.walletapp.wallet.domain.model.TransactionType
import java.text.DecimalFormat


sealed class CalculatorAction {
    data class Number(val number: String) : CalculatorAction()
    data class Operation(val operation: String) : CalculatorAction()
    object Calculate : CalculatorAction()
    object Delete : CalculatorAction()
    object Clear : CalculatorAction()
    object Decimal : CalculatorAction()
    object Save : CalculatorAction()
}

data class CalculatorState(
    val display: String = "0",
    val pendingOperation: String? = null,
    val firstOperand: Double? = null,
    val isNewInput: Boolean = true
)

private fun performPendingOperation(
    currentDisplay: String,
    firstOperand: Double?,
    pendingOperation: String?
): Double? {
    if (firstOperand == null || pendingOperation == null) {
        return currentDisplay.toDoubleOrNull()
    }
    val secondOperand = currentDisplay.toDoubleOrNull() ?: return null
    return when (pendingOperation) {
        "+" -> firstOperand + secondOperand
        "-" -> firstOperand - secondOperand
        "×" -> firstOperand * secondOperand
        "÷" -> {
            if (secondOperand == 0.0) Double.NaN else firstOperand / secondOperand
        }
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
                ?: display.toDoubleOrNull()
                ?: 0.0

            if (result.isNaN()) display = "Error"
            else { onSave(result); display = format.format(result) }
            firstOperand = null; pendingOperation = null; isNewInput = true
        }
    }
    return state.copy(display = display, firstOperand = firstOperand, pendingOperation = pendingOperation, isNewInput = isNewInput)
}

@Composable
fun CalculatorDisplay(
    displayValue: String,
    transactionType: TransactionType
) {
    val isExpression = displayValue.contains(Regex("[+\\-×÷]"))

    val parts = displayValue.split('.')
    val integerPart = parts[0]
    val decimalPart = if (parts.size > 1) ".${parts[1]}" else ""

    val amountColor = if (transactionType == TransactionType.EXPENSE) Color(0xFFC62828) else Color(0xFF2E7D32)

    val integerLength = integerPart.length

    val baseFontSize = when {
        integerLength > 10 -> 32.sp
        integerLength > 7 -> 40.sp
        else -> 45.sp
    }

    val unitFontSize = baseFontSize * 0.4f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        if (isExpression || displayValue == "Error") {
            Text(
                text = displayValue,
                fontSize = 48.sp,
                fontWeight = FontWeight.Light,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        } else {
            Text(
                text = integerPart + decimalPart,
                fontSize = baseFontSize,
                fontWeight = FontWeight.Light,
                color = amountColor,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "UZS",
                fontSize = unitFontSize,
                fontWeight = FontWeight.Light,
                color = amountColor.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }
    }
    Divider(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.onTertiary.copy(0.1f)), thickness = 0.5.dp
    )
}

@Composable
fun CalculatorPad(
    onSaveConfirmed: (Double) -> Unit,
    onDisplayChange: (String) -> Unit,
) {
    var state by remember { mutableStateOf(CalculatorState()) }

    val processAction: (CalculatorAction) -> Unit = { action ->
        val newState = evaluateExpression(state, action) { value ->
            onSaveConfirmed(value)
        }
        state = newState
        onDisplayChange(newState.display)
    }

    val rows = listOf(
        listOf("÷", "7", "8", "9", "⌫"),
        listOf("×", "4", "5", "6", "DeleteIcon"),
        listOf("-", "1", "2", "3", "="),
        listOf("+", "0", "000", ".", "CheckIcon"),
    )

    Column(
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        rows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { key ->
                    when (key) {
                        "⌫" -> IconCalculatorButton(
                            icon = Icons.Default.KeyboardBackspace,
                            onClick = { processAction(CalculatorAction.Delete) },
                            isDanger = true,
                        )
                        "CheckIcon" -> IconCalculatorButton(
                            icon = Icons.Default.Check,
                            onClick = { processAction(CalculatorAction.Save) },
                            isPrimary = true,
                        )
                        "DeleteIcon" -> CalculatorButton(
                            label = "C",
                            onClick = { processAction(CalculatorAction.Clear) },
                            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                            labelColor = Color(0xFFFFA000)
                        )
                        "=" -> CalculatorButton(
                            label = key,
                            onClick = { processAction(CalculatorAction.Calculate) },
                            isOperator = true,
                            backgroundColor = MaterialTheme.colorScheme.primaryContainer
                        )
                        in listOf("÷", "×", "+", "-") -> CalculatorButton(
                            label = key,
                            onClick = { processAction(CalculatorAction.Operation(key)) },
                            isOperator = true
                        )
                        "." -> CalculatorButton(label = key, onClick = { processAction(CalculatorAction.Decimal) })
                        else -> CalculatorButton(label = key, onClick = { processAction(CalculatorAction.Number(key)) })
                    }
                }
            }
        }
    }
}

@Composable
fun RowScope.CalculatorButton(
    label: String,
    onClick: () -> Unit,
    isOperator: Boolean = label in listOf("÷", "×", "+", "-", "="),
    backgroundColor: Color = Color.Transparent,
    labelColor: Color = Color.Unspecified
) {
    val finalLabelColor = if (labelColor != Color.Unspecified) {
        labelColor
    } else {
        if (isOperator) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onTertiary
    }
    Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1.5f)
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal,
            color = finalLabelColor
        )
    }
}

@Composable
fun RowScope.IconCalculatorButton(
    icon: ImageVector,
    onClick: () -> Unit,
    isPrimary: Boolean = false,
    isDanger: Boolean = false
) {
    val DangerColor = MaterialTheme.colorScheme.primaryContainer
    val bgColor = when {
        isPrimary -> Color(0xFF1B8B4D)
        isDanger -> DangerColor
        else -> Color.Transparent
    }
    val tintColor = if (isPrimary) Color.White else expenseColor

    Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1.5f)
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint =tintColor,
            modifier = Modifier.size(28.dp)
        )
    }
}