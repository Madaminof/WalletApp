
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


class CalculatorState {
    var display by mutableStateOf("0")
    var lastResult: Double? = null
    var pendingOperator: String? = null
    var resetNext: Boolean = false
}

@Composable
fun rememberCalculatorState(): CalculatorState = remember { CalculatorState() }

@Composable
fun CalculatorInput(
    calcState: CalculatorState,
    onSaveClick: (Double) -> Unit,
    isSaveEnabled: Boolean,
    isSaving: Boolean
) {
    val buttons = listOf(
        "7", "8", "9", "Del",
        "4", "5", "6", "×",
        "1", "2", "3", "÷",
        "00", "0", ".", "+",
        "000", "C", "-", "="
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(buttons.size) { index ->
            val text = buttons[index]
            CalculatorButton(
                text = text,
                isOperator = text in setOf("+", "-", "×", "÷", "=", "C", "Del"),
                isSaveButton = text == "=",
                isSaveEnabled = isSaveEnabled,
                isSaving = isSaving && text == "=",
                onClick = { handleCalculatorInput(text, calcState, onSaveClick) }
            )
        }
    }
}


@Composable
fun CalculatorButton(
    text: String,
    isOperator: Boolean,
    isSaveButton: Boolean = false,
    isSaveEnabled: Boolean = true,
    isSaving: Boolean = false,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (pressed) 0.85f else 1f, animationSpec = tween(80))
    val targetColor = when {
        isSaveButton -> if (isSaveEnabled) MaterialTheme.colorScheme.primary else Color.Gray
        isOperator -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        else -> MaterialTheme.colorScheme.onPrimaryContainer.copy(0.7f)
    }
    val buttonColor by animateColorAsState(targetValue = targetColor, animationSpec = tween(120))
    val contentColor = when {
        isSaveButton -> MaterialTheme.colorScheme.onPrimary
        isOperator -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onTertiary
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clip(RoundedCornerShape(20.dp))
            .background(buttonColor)
            .clickable(enabled = !isSaveButton || isSaveEnabled) {
                pressed = true
                onClick()
                pressed = false
            }
            .shadow(0.dp, RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (isSaving) {
            CircularProgressIndicator(modifier = Modifier.size(26.dp), color = MaterialTheme.colorScheme.onPrimary)
        } else {
            Text(text = text, fontSize = 22.sp, fontWeight = FontWeight.SemiBold, color = contentColor)
        }
    }
}

fun handleCalculatorInput(
    input: String,
    state: CalculatorState,
    onSaveClick: (Double) -> Unit
) {
    val operators = setOf("+", "-", "×", "÷")

    when (input) {
        "C" -> {
            state.display = "0"
            state.lastResult = null
            state.pendingOperator = null
            state.resetNext = false
        }
        "Del" -> {
            if (!state.resetNext && state.display.isNotEmpty()) {
                state.display = state.display.dropLast(1).ifEmpty { "0" }
            }
        }
        "=" -> {
            if (state.pendingOperator != null && state.lastResult != null) {
                val right = state.display.toDoubleOrNull() ?: return
                val result = calculate(state.lastResult!!, right, state.pendingOperator!!)
                state.display = formatResult(result)
                state.lastResult = result
                state.pendingOperator = null
                state.resetNext = true
                onSaveClick(result)
            }
        }
        in operators -> {
            if (state.lastResult == null) {
                state.lastResult = state.display.toDoubleOrNull()
            } else if (!state.resetNext) {
                val right = state.display.toDoubleOrNull() ?: return
                state.lastResult = calculate(state.lastResult!!, right, state.pendingOperator ?: input)
            }
            state.pendingOperator = input
            state.resetNext = true
        }
        "." -> {
            if (state.resetNext) {
                state.display = "0."
                state.resetNext = false
            } else if (!state.display.contains(".")) {
                state.display += "."
            }
        }
        else -> { // Raqamlar
            if (state.resetNext) {
                state.display = input
                state.resetNext = false
            } else {
                state.display = if (state.display == "0") input else state.display + input
            }
        }
    }
}

// Natijani .0 dan tozalash
fun formatResult(value: Double): String {
    return if (value % 1.0 == 0.0) value.toInt().toString() else value.toString()
}


fun calculate(a: Double, b: Double, operator: String): Double {
    return when (operator) {
        "+" -> a + b
        "-" -> a - b
        "×" -> a * b
        "÷" -> a / b
        else -> b
    }
}

