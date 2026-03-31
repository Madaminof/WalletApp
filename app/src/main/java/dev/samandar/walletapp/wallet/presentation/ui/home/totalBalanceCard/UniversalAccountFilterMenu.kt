package dev.samandar.walletapp.wallet.presentation.ui.home.totalBalanceCard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.domain.model.account.Account


@Composable
fun UniversalAccountFilterMenu(
    isExpanded: Boolean,
    onDismiss: () -> Unit,
    accounts: List<Account>,
    selectedAccountIds: Set<String>,
    onAccountSelectionChange: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = onDismiss,
        modifier = modifier
            .background(MaterialTheme.colorScheme.onPrimaryContainer)
    ) {
        // Agar sarlavha kerak bo'lsa (CashFlow menyudagi kabi)
        Text(
            text = stringResource(Strings.total_balance_title_dialog),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onTertiary.copy(0.5f)
        )

        accounts.forEach { account ->
            val isChecked = selectedAccountIds.contains(account.id)

            DropdownMenuItem(
                leadingIcon = {
                    Icon(
                        imageVector = if (isChecked) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (isChecked) primaryAccent else Color.Gray.copy(0.3f),
                        modifier = Modifier.size(18.dp)
                    )
                },
                text = {
                    Text(
                        text = account.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Normal,
                        color = if (isChecked) primaryAccent else MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                    )
                },
                onClick = {
                    onAccountSelectionChange(account.id, !isChecked)
                    // Multi-select bo'lgani uchun onDismiss() chaqirilmaydi
                }
            )
        }
    }
}