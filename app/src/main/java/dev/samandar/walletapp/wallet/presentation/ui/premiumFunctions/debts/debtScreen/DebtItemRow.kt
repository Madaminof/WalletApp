package dev.samandar.walletapp.wallet.presentation.ui.premiumFunctions.debts.debtScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.CallMade
import androidx.compose.material.icons.rounded.CallReceived
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.samandar.walletapp.ui.theme.expenseColor
import dev.samandar.walletapp.ui.theme.incomeColor
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.domain.model.debt.Debt
import dev.samandar.walletapp.wallet.domain.model.debt.DebtType
import dev.samandar.walletapp.wallet.presentation.utils.formatAmountWithCurrency
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DebtItemRow(
    debt: Debt,
    showDivider: Boolean,
    onClick: (Debt) -> Unit,
    onQuickAddPayment: (Debt) -> Unit
) {
    val activeColor = if (debt.type == DebtType.LENT) MaterialTheme.colorScheme.primary else expenseColor
    val isOverdue = debt.dueDate != null && debt.dueDate < System.currentTimeMillis() && !debt.isSettled

    val progress = if (debt.totalAmount > 0) {
        ((debt.totalAmount - debt.remainingAmount) / debt.totalAmount).coerceIn(0.0, 1.0).toFloat()
    } else 0f

    val textDecoration = if (debt.isSettled) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
    val contentAlpha = if (debt.isSettled) 0.5f else 1f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(debt) }
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .alpha(contentAlpha)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Progress va Icon
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(42.dp)) {
                CircularProgressIndicator(
                    progress = { if (debt.isSettled) 1f else progress },
                    modifier = Modifier.fillMaxSize(),
                    color = if (debt.isSettled) MaterialTheme.colorScheme.primary else activeColor,
                    strokeWidth = 2.5.dp,
                    trackColor = activeColor.copy(alpha = 0.1f),
                    strokeCap = StrokeCap.Round
                )
                Icon(
                    imageVector = if (debt.isSettled) Icons.Rounded.CheckCircle
                    else if (debt.type == DebtType.LENT) Icons.Rounded.CallMade
                    else Icons.Rounded.CallReceived,
                    contentDescription = null,
                    tint = if (debt.isSettled) MaterialTheme.colorScheme.primary else activeColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = debt.personName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        textDecoration = textDecoration
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                )
                Text(
                    text = "${debt.startDate.toFormattedShortDateNoTime()} → ${debt.dueDate?.toFormattedShortDateNoTime() ?: stringResource(Strings.unknown)}",
                    style = MaterialTheme.typography.labelSmall.copy(textDecoration = textDecoration),
                    color = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatAmountWithCurrency(debt.remainingAmount),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        textDecoration = textDecoration,
                        color = if (debt.isSettled) MaterialTheme.colorScheme.outline else activeColor
                    )
                )

                if (!debt.isSettled) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        if (progress > 0f) {
                            Text(
                                text = "${(progress * 100).toInt()}% ${stringResource(Strings.debt_settled)}",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                            )
                            Spacer(Modifier.width(8.dp))
                        }

                        Surface(
                            onClick = { onQuickAddPayment(debt) },
                            shape = CircleShape,
                            color = activeColor.copy(alpha = 0.12f),
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Add, null, Modifier.padding(6.dp).size(16.dp), activeColor)
                        }
                    }
                } else {
                    Text(
                        text = stringResource(Strings.debt_paid_percent).replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        // Tavsif
        if (!debt.description.isNullOrBlank()) {
            Text(
                text = debt.description,
                style = MaterialTheme.typography.bodySmall.copy(textDecoration = textDecoration),
                color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.7f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 54.dp, top = 2.dp)
            )
        }
    }

    if (showDivider) {
        HorizontalDivider(
            modifier = Modifier.padding(start = 70.dp, end = 16.dp),
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(0.5f)
        )
    }
}


fun Long.toFormattedShortDateNoTime(): String {
    val date = Date(this)
    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)

    calendar.time = date
    val dateYear = calendar.get(Calendar.YEAR)

    return if (currentYear == dateYear) {
        // Agar joriy yil bo'lsa: "12 May"
        val format = SimpleDateFormat("d MMM", Locale.getDefault())
        format.format(date)
    } else {
        // Agar boshqa yil bo'lsa: "12 May, 2024"
        val format = SimpleDateFormat("d MMM, yyyy", Locale.getDefault())
        format.format(date)
    }
}