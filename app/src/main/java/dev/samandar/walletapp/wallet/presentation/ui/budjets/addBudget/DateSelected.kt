package dev.samandar.walletapp.wallet.presentation.ui.budjets.addBudget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.domain.model.BudgetPeriod
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.dateTimePicker.AppDatePickerDialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private fun Long.toDateString(): String {
    val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return formatter.format(Date(this))
}

@Composable
fun DateRangeSelector(
    startDateMillis: Long,
    endDateMillis: Long?,
    onStartDateClick: (Long) -> Unit,
    onEndDateClick: (Long) -> Unit
) {
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Text(
            text = stringResource(R.string.period_custom_range_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(bottom = 12.dp),
            color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DateInputField(
                value = startDateMillis.toDateString(),
                label = stringResource(R.string.date_input_label_start),
                onClick = { showStartPicker = true }, // Faqat shu yerda ochiladi
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(8.dp))

            DateInputField(
                value = endDateMillis?.toDateString() ?: stringResource(R.string.date_input_placeholder_end),
                label = stringResource(R.string.date_input_label_end),
                onClick = { showEndPicker = true },
                modifier = Modifier.weight(1f)
            )
        }
    }

    if (showStartPicker) {
        AppDatePickerDialog(
            initialDate = startDateMillis,
            onDateSelected = { timestamp ->
                onStartDateClick(timestamp)
                showStartPicker = false
            },
            onDismiss = { showStartPicker = false }
        )
    }

    if (showEndPicker) {
        AppDatePickerDialog(
            initialDate = endDateMillis ?: System.currentTimeMillis(),
            onDateSelected = { timestamp ->
                onEndDateClick(timestamp)
                showEndPicker = false
            },
            onDismiss = { showEndPicker = false }
        )
    }
}

@Composable
fun DateInputField(
    value: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier
) {
    val isPlaceholder = value == stringResource(R.string.date_input_placeholder_end)

    Surface(
        onClick = onClick,
        modifier = modifier.heightIn(min = 64.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.3f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onTertiary.copy(0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (isPlaceholder) FontWeight.Normal else FontWeight.Black
                    ),
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun PeriodSelector(
    selectedPeriod: BudgetPeriod,
    onPeriodSelected: (BudgetPeriod) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
        Text(
            text = stringResource(R.string.period_selector_title),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            modifier = Modifier.padding(bottom = 12.dp),
            color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BudgetPeriod.entries.forEach { period ->
                FilterChip(
                    selected = selectedPeriod == period,
                    onClick = { onPeriodSelected(period) },
                    label = {
                        Text(
                            text = period.toLocalizedString().uppercase(),
                            fontWeight = if (selectedPeriod == period) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f),
                        labelColor = MaterialTheme.colorScheme.onTertiary.copy(0.7f),
                    ),
                    border = null
                )
            }
        }
    }
}