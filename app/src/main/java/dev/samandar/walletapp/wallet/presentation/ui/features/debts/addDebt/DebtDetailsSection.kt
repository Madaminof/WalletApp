package dev.samandar.walletapp.wallet.presentation.ui.features.debts.addDebt

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.samandar.walletapp.wallet.domain.model.account.Account
import dev.samandar.walletapp.wallet.presentation.ui.features.debts.AccountSelectionField
import dev.samandar.walletapp.wallet.presentation.ui.features.debts.DebtDateRangeSelection

@Composable
fun DebtDetailsSection(
    accounts: List<Account>,
    selectedAccount: Account?,
    onAccountSelect: (Account) -> Unit,
    startDate: Long,
    dueDate: Long?,
    onStartDateSelect: (Long) -> Unit,
    onDueDateSelect: (Long?) -> Unit,
    accentColor: Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.5f),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            AccountSelectionField(
                accounts = accounts,
                selectedAccount = selectedAccount,
                onAccountSelect = onAccountSelect,
                accentColor = accentColor
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
                thickness = 0.8.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
            )

            DebtDateRangeSelection(
                startDate = startDate,
                dueDate = dueDate,
                onStartDateSelect = onStartDateSelect,
                onDueDateSelect = onDueDateSelect,
                accentColor = accentColor
            )
        }
    }
}