package dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.calculator

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.ui.theme.defaultColor
import dev.samandar.walletapp.ui.theme.expenseColor
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.currency.AddTransactionCurrencyManager
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.currency.CurrencySelectionBottomSheet
import dev.samandar.walletapp.wallet.presentation.utils.AmountFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CalculatorPadPremiumUI(
    onSaveConfirmed: (Double) -> Unit,
    onDisplayChange: (String) -> Unit,
    onDateClick: () -> Unit,
    selectedDate: Long,
    currentValue: String,
    onAmountClick: () -> Unit,
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
    var showCurrencySheet by remember { mutableStateOf(false) }
    val localCurrency by AddTransactionCurrencyManager.localCurrency // Lokalni chaqiramiz

    LaunchedEffect(currentValue) {
        if (state.display != currentValue) {
            state = state.copy(display = currentValue)
        }
    }

    LaunchedEffect(state.display) { onDisplayChange(state.display) }

    val processAction: (CalculatorAction) -> Unit = { action ->
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
            AmountDisplay(
                amount = state.display,
                localCurrency = localCurrency,
                showCurrencySheet = showCurrencySheet,
                onCurrencyClick = { showCurrencySheet = true },
                onCalculatorClick = { onAmountClick() },
                currencySheetContent = {
                    CurrencySelectionBottomSheet(
                        onDismiss = { showCurrencySheet = false }
                    )
                }
            )

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
fun AmountDisplay(
    amount: String,
    localCurrency: String,
    showCurrencySheet: Boolean,
    onCurrencyClick: () -> Unit,
    onCalculatorClick: () -> Unit,
    modifier: Modifier = Modifier,
    currencySheetContent: @Composable () -> Unit = {},
) {
    if (showCurrencySheet) currencySheetContent()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp),
        shape = RoundedCornerShape(20.dp),
        color = defaultColor.copy(0.05f),
        border = BorderStroke(1.dp, defaultColor.copy(0.08f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 8.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                onClick = onCurrencyClick,
                shape = RoundedCornerShape(12.dp),
                color = defaultColor.copy(0.1f),
                modifier = Modifier.height(44.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = getFlagEmoji(localCurrency), fontSize = 18.sp)
                    Text(
                        text = localCurrency.uppercase(),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier
                            .size(16.dp)
                            .alpha(0.6f),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            val amountDouble = amount.toDoubleOrNull() ?: 0.0
            val isDecimal = amount.contains(".")

            Text(
                text = AmountFormat(amountDouble, isDecimal),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = if (amount.length > 10) 24.sp else 30.sp,
                    fontWeight = FontWeight.Bold,
                    fontFeatureSettings = "tnum",
                    color = MaterialTheme.colorScheme.onTertiary
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            FilledIconButton(
                onClick = onCalculatorClick,
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(16.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.calculator_ic),
                    contentDescription = "Calculator",
                    modifier = Modifier.size(34.dp)
                )
            }
        }
    }
}

fun getFlagEmoji(currencyCode: String): String {
    if (currencyCode.length != 3) return "🌐"
    val firstLetter = Character.codePointAt(currencyCode, 0) - 0x41 + 0x1F1E6
    val secondLetter = Character.codePointAt(currencyCode, 1) - 0x41 + 0x1F1E6
    return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
}


@Composable
fun RefinedAppleKey(
    key: String,
    dateText: String,
    modifier: Modifier,
    onClick: () -> Unit,
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
            "⌫" -> Icon(
                painter = painterResource(R.drawable.clear_icon),
                null,
                tint = expenseColor,
                modifier = Modifier.size(24.dp)
            )

            "✓" -> Icon(
                Icons.Default.Check,
                null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )

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