package dev.samandar.walletapp.wallet.presentation.ui.premiumFunctions.debts.detail

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
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.domain.model.debt.DebtTransaction
import dev.samandar.walletapp.wallet.presentation.utils.formatAmountWithCurrency
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PaymentHistoryRow(transaction: DebtTransaction, accentColor: Color) {
    val dateFormatter = remember { SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.02f), RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(accentColor.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Payments, null, modifier = Modifier.size(20.dp), tint = accentColor)
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (transaction.note.isNullOrBlank()) stringResource(Strings.cat_debt_payment) else transaction.note,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                fontSize = 14.sp
            )
            Text(
                text = dateFormatter.format(Date(transaction.date)),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onTertiary.copy(0.5f),
                fontSize = 10.sp
            )
        }

        Text(
            text = formatAmountWithCurrency(transaction.amount),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            color = accentColor,
            fontSize = 14.sp
        )
    }
}