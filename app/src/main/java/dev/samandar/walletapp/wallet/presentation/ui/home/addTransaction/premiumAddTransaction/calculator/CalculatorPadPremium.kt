package dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.calculator

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.domain.model.account.Account
import dev.samandar.walletapp.wallet.presentation.ui.home.activeCurrency
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.SoundManager
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.account.PremiumAccountSelectionBox
import dev.samandar.walletapp.wallet.presentation.utils.AmountFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@Composable
fun CalculatorPadPremium(
    onSaveConfirmed: (Double) -> Unit,
    onDisplayChange: (String) -> Unit,
    selectedAccount: Account?,
    onDateClick: () -> Unit,
    selectedDate: Long,
    currentValue: String,
    onClick: () -> Unit,
) {
    val todayText = stringResource(R.string.label_today)
    val yesterdayText = stringResource(R.string.label_yesterday)

    var state by remember { mutableStateOf(CalculatorState(display = currentValue)) }
    val haptic = LocalHapticFeedback.current

    val dateTimeLabel = remember(selectedDate) {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply { timeInMillis = selectedDate }

        val isSameDay = now.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR)

        val yesterday = Calendar.getInstance().apply { add(Calendar.DATE, -1) }
        val isYesterday = yesterday.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                yesterday.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR)

        when {
            isSameDay -> todayText
            isYesterday -> yesterdayText
            else -> {
                val pattern = if (now.get(Calendar.YEAR) == target.get(Calendar.YEAR)) {
                    "dd MMM"
                } else {
                    "dd MMM yyyy"
                }
                SimpleDateFormat(pattern, Locale.getDefault()).format(Date(selectedDate))
            }
        }
    }

    LaunchedEffect(state.display) {
        onDisplayChange(state.display)
    }

    val processAction: (CalculatorAction) -> Unit = { action ->
        SoundManager.playClick()
        state = evaluateExpression(state, action) { value -> onSaveConfirmed(value) }
    }

    Surface(
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        tonalElevation = 8.dp,
        shadowElevation = 20.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PremiumAccountSelectionBox(
                    selectedAccount = selectedAccount,
                    onClick = onClick,
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Center
                    ) {
                        val amountDouble = state.display.toDoubleOrNull() ?: 0.0

                        val formattedAmount = AmountFormat(
                            amount = amountDouble,
                            includeFraction = state.display.contains(".")
                        )

                        Text(
                            text = formattedAmount,
                            fontSize = when {
                                formattedAmount.length > 14 -> 15.sp
                                formattedAmount.length > 10 -> 18.sp
                                else -> 20.sp
                            },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.5).sp
                            ),
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onTertiary,
                            textAlign = TextAlign.End
                        )
                        Text(
                            text = activeCurrency,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                            lineHeight = 8.sp
                        )
                    }
                }
            }

            val keys = listOf(
                listOf("7", "8", "9", "⌫"),
                listOf("4", "5", "6", "C"),
                listOf("1", "2", "3", "DATE"),
                listOf(".", "0", "000", "✓")
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                keys.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { key ->
                            CompactPremiumKey(
                                key = key,
                                dateText = dateTimeLabel,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    when (key) {
                                        "C" -> processAction(CalculatorAction.Clear)
                                        "⌫" -> processAction(CalculatorAction.Delete)
                                        "." -> processAction(CalculatorAction.Decimal)
                                        "✓" -> {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            processAction(CalculatorAction.Save)
                                        }

                                        "DATE" -> onDateClick()
                                        else -> processAction(CalculatorAction.Number(key))
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CompactPremiumKey(
    key: String,
    dateText: String,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val isAction = key == "✓"
    val isDelete = key == "⌫"
    val isSpecial = key == "C" || key == "DATE"

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium), label = ""
    )

    Box(
        modifier = modifier
            .height(45.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(14.dp))
            .background(
                when {
                    isAction -> MaterialTheme.colorScheme.primary
                    isDelete -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
                    isSpecial -> MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.03f)
                    else -> MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.03f)
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(color = Color.White),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        when (key) {
            "⌫" -> Icon(
                Icons.Default.Backspace,
                null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(18.dp)
            )

            "✓" -> Icon(
                Icons.Default.Check,
                null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )

            "DATE" -> Text(
                text = dateText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            else -> Text(
                text = key,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = if (key == "000") 16.sp else 20.sp
                ),
                color = MaterialTheme.colorScheme.onTertiary.copy(0.7f)
            )
        }
    }
}