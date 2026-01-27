package dev.samandar.walletapp.wallet.presentation.ui.charts.expenseListComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.presentation.ui.charts.SortState



import androidx.compose.material3.*

@Composable
fun SortSelectionMenu(
    isExpanded: Boolean,
    onDismiss: () -> Unit,
    currentSortState: SortState,
    onSortSelected: (SortState) -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryAccent = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onTertiary.copy(0.8f)

    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = onDismiss,
        modifier = modifier
            .background(MaterialTheme.colorScheme.onPrimaryContainer)
    ) {
        // Sort variantlari ro'yxati
        val sortOptions = listOf(
            Triple(SortState.DATE_DESC, R.string.sort_option_date_desc, Icons.Default.AccessTime),
            Triple(SortState.AMOUNT_DESC, R.string.sort_option_amount_desc, Icons.Default.ArrowUpward),
            Triple(SortState.AMOUNT_ASC, R.string.sort_option_amount_asc, Icons.Default.ArrowDownward)
        )

        sortOptions.forEach { (state, labelRes, icon) ->
            val isSelected = currentSortState == state

            DropdownMenuItem(
                leadingIcon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) primaryAccent else textColor.copy(0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                },
                text = {
                    Text(
                        text = stringResource(labelRes),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) primaryAccent else textColor
                    )
                },
                trailingIcon = {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = primaryAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                onClick = {
                    onSortSelected(state)
                    onDismiss()
                }
            )
        }
    }
}