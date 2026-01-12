package dev.samandar.walletapp.wallet.presentation.ui.premiumFunctions.debts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.dateTime.PremiumDateTimePickerDialog
import java.text.DateFormat
import java.util.Date

@Composable
fun DebtDateRangeSelection(
    startDate: Long,
    dueDate: Long?,
    onStartDateSelect: (Long) -> Unit,
    onDueDateSelect: (Long?) -> Unit,
    accentColor: Color
) {
    var activePicker by remember { mutableStateOf<DatePickerType?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DateItemCard(
            label = stringResource(R.string.debt_start_date),
            date = startDate,
            icon = Icons.Default.CalendarMonth,
            accentColor = accentColor,
            onClick = { activePicker = DatePickerType.CREATED_AT }
        )

        DateItemCard(
            label = stringResource(R.string.debt_due_date),
            date = dueDate,
            icon = Icons.Default.EventAvailable,
            accentColor = accentColor,
            isOptional = true,
            onClick = { activePicker = DatePickerType.DUE_DATE },
            onClear = { onDueDateSelect(null) }
        )
    }

    activePicker?.let { type ->
        PremiumDateTimePickerDialog(
            initialDateTime = if (type == DatePickerType.DUE_DATE) (dueDate ?: System.currentTimeMillis()) else startDate,
            onConfirm = {
                if (type == DatePickerType.CREATED_AT) onStartDateSelect(it)
                else onDueDateSelect(it)
                activePicker = null
            },
            onDismiss = { activePicker = null },
        )
    }
}

@Composable
private fun DateItemCard(
    label: String,
    date: Long?,
    icon: ImageVector,
    accentColor: Color,
    isOptional: Boolean = false,
    onClick: () -> Unit,
    onClear: () -> Unit = {}
) {
    val isSet = date != null
    val displayDate = if (isSet) {
        DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(date!!))
    } else {
       stringResource(Strings.unknown)
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.onTertiary.copy(0.02f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon Box
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (isSet) accentColor.copy(alpha = 0.1f) else MaterialTheme.colorScheme.outlineVariant.copy(0.2f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSet) accentColor else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Text Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.7f),
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = displayDate,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = if (isSet) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = if (isSet) MaterialTheme.colorScheme.onTertiary.copy(0.8f) else MaterialTheme.colorScheme.outline
                )
            }
            if (isOptional && isSet) {
                IconButton(
                    onClick = onClear,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Clear",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

private enum class DatePickerType { CREATED_AT, DUE_DATE }