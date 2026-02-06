package dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.calculator

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.ui.theme.defaultColor
import dev.samandar.walletapp.ui.theme.expenseColor
import dev.samandar.walletapp.wallet.domain.model.Account
import dev.samandar.walletapp.wallet.presentation.ui.home.activeCurrency
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.SoundManager
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.account.PremiumAccountSelectionBox
import dev.samandar.walletapp.wallet.presentation.utils.AmountFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CalculatorPadPremiumUI(
    onSaveConfirmed: (Double) -> Unit,
    onDisplayChange: (String) -> Unit,
    selectedAccount: Account?,
    onDateClick: () -> Unit,
    selectedDate: Long,
    currentValue: String,
    onClick: () -> Unit,
    onAmountClick: () -> Unit
) {
    var state by remember { mutableStateOf(CalculatorState(display = currentValue)) }
    val haptic = LocalHapticFeedback.current

    val todayText = stringResource(R.string.label_today)
    val yesterdayText = stringResource(R.string.label_yesterday)

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

    LaunchedEffect(currentValue) {
        if (state.display != currentValue) {
            state = state.copy(display = currentValue)
        }
    }

    LaunchedEffect(state.display) { onDisplayChange(state.display) }

    val processAction: (CalculatorAction) -> Unit = { action ->
        SoundManager.playClick()
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        state = evaluateExpression(state, action) { value -> onSaveConfirmed(value) }
    }

    Surface(
        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.8f),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(defaultColor.copy(0.05f))
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(0.45f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    PremiumAccountSelectionBox(
                        selectedAccount = selectedAccount,
                        onClick = onClick,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Row(
                    modifier = Modifier
                        .weight(0.55f)
                        .fillMaxHeight()
                        .padding(start = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        val amountDouble = state.display.toDoubleOrNull() ?: 0.0
                        Text(
                            text = AmountFormat(amountDouble, state.display.contains(".")),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            letterSpacing = (-0.5).sp,
                            lineHeight = 22.sp
                        )

                        // Kichik Valyuta ostida
                        Text(
                            text = activeCurrency.uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary.copy(0.5f),
                            letterSpacing = 1.sp,
                            lineHeight = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(
                        painter = painterResource(id = R.drawable.calculator_ic),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(38.dp)
                            .clickable {
                            SoundManager.playClick()
                            onAmountClick()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

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
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        row.forEach { key ->
                            RefinedAppleKey(
                                key = key,
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1.5f),
                                onClick = {
                                    when (key) {
                                        "C" -> processAction(CalculatorAction.Clear)
                                        "⌫" -> processAction(CalculatorAction.Delete)
                                        "." -> processAction(CalculatorAction.Decimal)
                                        "✓" -> processAction(CalculatorAction.Save)
                                        "DATE" -> onDateClick()
                                        else -> processAction(CalculatorAction.Number(key))
                                    }
                                },
                                dateText = dateTimeLabel
                            )
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun RefinedAppleKey(
    key: String,
    dateText: String,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isAction = key == "✓"

    val scale by animateFloatAsState(if (isPressed) 0.88f else 1f, label = "scale")
    val alpha by animateFloatAsState(if (isPressed) 0.6f else 1f, label = "alpha")

    Box(
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale; this.alpha = alpha }
            .clip(RoundedCornerShape(16.dp))
            .background(if (isAction) MaterialTheme.colorScheme.primary else defaultColor.copy(0.05f))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        when (key) {
            "⌫" -> Icon(painter = painterResource(R.drawable.clear_icon), null, tint = expenseColor, modifier = Modifier.size(24.dp))
            "✓" -> Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(24.dp))
            "C" -> Text(key, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = expenseColor)

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