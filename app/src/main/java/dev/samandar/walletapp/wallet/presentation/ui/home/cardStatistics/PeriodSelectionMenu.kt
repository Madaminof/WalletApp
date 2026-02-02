package dev.samandar.walletapp.wallet.presentation.ui.home.cardStatistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.samandar.walletapp.utils.FilterKeys
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.presentation.ui.home.totalBalanceCard.primaryAccent

@Composable
fun PeriodSelectionMenu(
    isExpanded: Boolean,
    onDismiss: () -> Unit,
    selectedPeriodKey: String,
    onPeriodSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val periods = remember {
        listOf(
            FilterKeys.DAY to Strings.filter_day,
            FilterKeys.WEEK to Strings.filter_week,
            FilterKeys.MONTH to Strings.filter_month,
            FilterKeys.YEAR to Strings.filter_year,
            FilterKeys.ALL to Strings.filter_all
        )
    }

    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = onDismiss,
        modifier = modifier
            .background(MaterialTheme.colorScheme.onPrimaryContainer)
    ) {
        periods.forEach { (key, resId) ->
            val isSelected = selectedPeriodKey == key

            DropdownMenuItem(
                leadingIcon = {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = primaryAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Spacer(modifier = Modifier.size(20.dp))
                    }
                },
                text = {
                    Text(
                        text = stringResource(resId),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) primaryAccent else MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                    )
                },
                onClick = {
                    onPeriodSelected(key)
                    onDismiss()
                }
            )
        }
    }
}