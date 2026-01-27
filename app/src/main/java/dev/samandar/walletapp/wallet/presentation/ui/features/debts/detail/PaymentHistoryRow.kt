package dev.samandar.walletapp.wallet.presentation.ui.features.debts.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.domain.model.debt.DebtTransaction
import dev.samandar.walletapp.wallet.presentation.utils.formatAmountWithCurrency
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PaymentHistoryRow(transaction: DebtTransaction, accentColor: Color) {
    val dateFormatter = remember { SimpleDateFormat("dd MMMM, HH:mm", Locale.getDefault()) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(accentColor.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Payments, null, modifier = Modifier.size(20.dp), tint = accentColor)
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                transaction.note?.let {
                    Text(
                        text = it.ifBlank { stringResource(R.string.cat_debt_payment) },
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                    )
                }
                Text(
                    text = dateFormatter.format(Date(transaction.date)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.6f)
                )
            }

            Text(
                text = "+ " + formatAmountWithCurrency(transaction.amount),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = accentColor
            )
        }
    }
}