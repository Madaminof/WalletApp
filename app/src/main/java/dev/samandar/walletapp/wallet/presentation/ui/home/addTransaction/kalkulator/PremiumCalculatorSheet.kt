package dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.kalkulator

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.ui.theme.defaultColor
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.SoundManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumCalculatorSheet(
    initialValue: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var expression by remember { mutableStateOf(if (initialValue == "0") "" else initialValue) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val haptic = LocalHapticFeedback.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 32.dp, top = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(
                        defaultColor.copy(0.05f),
                        RoundedCornerShape(24.dp)
                    )
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = expression.ifEmpty { "0" },
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = if (expression.length > 12) 24.sp else 32.sp
                    ),
                    color = MaterialTheme.colorScheme.onTertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                val preview = calculatePreview(expression)
                if (expression.any { it in "+-×÷" } && preview != expression) {
                    Text(
                        text = "= $preview",
                        modifier = Modifier.animateContentSize(),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.primary.copy(0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- KEYPAD SECTION ---
            val buttons = listOf(
                listOf("C", "÷", "×", "⌫"),
                listOf("7", "8", "9", "-"),
                listOf("4", "5", "6", "+"),
                listOf("1", "2", "3", "="), // "=" tugmasi displayni yangilaydi
                listOf("0", "000", ".", "DONE_ICON") // "DONE" yakuniy natijani qaytaradi
            )

            buttons.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    row.forEach { label ->
                        CalculatorButton(
                            label = label,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(if (label == "DONE_ICON") 1.2f else 1.3f),
                            onClick = {
                                SoundManager.playClick()
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)

                                when (label) {
                                    "=" -> {
                                        val result = calculatePreview(expression)
                                        expression = if (result == "0") "" else result
                                    }

                                    "DONE_ICON" -> {
                                        val finalResult = calculatePreview(expression)
                                        onConfirm(finalResult)
                                    }

                                    else -> {
                                        expression = handleInput(expression, label)
                                    }
                                }
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}


@Composable
fun CalculatorButton(
    label: String,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    // Shartlarni aniqlab olamiz
    val isOperator = label in listOf("+", "-", "×", "÷", "=")
    val isDone = label == "DONE_ICON"
    val isClear = label == "C" || label == "⌫"

    // Ranglar palitrasi
    val containerColor = when {
        isDone -> MaterialTheme.colorScheme.primary
        isOperator -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        isClear -> MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
        else -> defaultColor.copy(0.05f)
    }

    val contentColor = when {
        isDone -> MaterialTheme.colorScheme.onPrimary
        isOperator -> MaterialTheme.colorScheme.primary
        isClear -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.8f)
    }

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = containerColor,
        tonalElevation = if (isDone) 4.dp else 0.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (label) {
                "DONE_ICON" -> {
                    Icon(
                        imageVector = Icons.Rounded.Check, // Minimalistik bitta check yaxshi turadi
                        contentDescription = "Confirm",
                        tint = contentColor,
                        modifier = Modifier.size(28.dp)
                    )
                }

                "⌫" -> {
                    Icon(
                        painter = painterResource(id = R.drawable.clear_icon), // O'zingdagi backspace icon
                        contentDescription = "Delete",
                        tint = contentColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                else -> {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = if (label == "000") 18.sp else 22.sp // 000 sig'ishi uchun kichikroq
                        ),
                        color = contentColor
                    )
                }
            }
        }
    }
}