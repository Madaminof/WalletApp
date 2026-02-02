package dev.samandar.walletapp.wallet.presentation.ui.charts.historyTransactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp



import androidx.compose.material3.*
import androidx.compose.ui.res.stringResource
import dev.samandar.walletapp.utils.Strings


@Composable
fun PeriodSelectionMenu(
    isExpanded: Boolean,
    onDismiss: () -> Unit,
    currentPeriod: TransactionPeriod,
    onPeriodSelected: (TransactionPeriod) -> Unit
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = onDismiss,
        modifier = Modifier.background(MaterialTheme.colorScheme.onPrimaryContainer)
    ) {
        val options = listOf(
            TransactionPeriod.MONTHLY to stringResource(Strings.period_monthly),
            TransactionPeriod.YEARLY to stringResource(Strings.period_yearly),
            TransactionPeriod.ALL to stringResource(Strings.period_all),
            )

        options.forEach { (period, label) ->
            DropdownMenuItem(
                text = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight =  if (currentPeriod == period) FontWeight.Bold else FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                    )
                },
                leadingIcon = {
                    if (currentPeriod == period) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                onClick = {
                    onPeriodSelected(period)
                    onDismiss()
                }
            )
        }
    }
}