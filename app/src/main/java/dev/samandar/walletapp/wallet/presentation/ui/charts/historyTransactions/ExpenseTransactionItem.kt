package dev.samandar.walletapp.wallet.presentation.ui.charts.historyTransactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.ui.theme.expenseColor
import dev.samandar.walletapp.ui.theme.incomeColor
import dev.samandar.walletapp.wallet.domain.model.Transaction
import dev.samandar.walletapp.wallet.domain.model.TransactionType
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.categories.getTranslatedName
import dev.samandar.walletapp.wallet.presentation.utils.formatAmountWithCurrency
import androidx.compose.foundation.clickable


data class Category(val name: String, val iconResId: Int?)
data class Account(val name: String,val iconResId: Int?)

@Composable
fun ExpenseTransactionItem(
    transaction: Transaction,
    onItemClick: (Transaction) -> Unit,
    showDivider: Boolean = true
) {
    val category = transaction.category
    val displayName = getTranslatedName(category.name)
    val accountName = getTranslatedName(transaction.account.name)
    val iconColor = remember(category.colorArgb) { Color(category.colorArgb) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(transaction) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconColor.copy(0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = category.iconResId ?: R.drawable.ic_other),
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displayName.toString(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onTertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )
                Text(
                    text = accountName.toString(),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.5f),
                    maxLines = 1,
                    lineHeight = 16.sp
                )
            }

            val amountPrefix = if (transaction.type == TransactionType.EXPENSE) "-" else "+"
            val amountColor = if (transaction.type == TransactionType.EXPENSE) expenseColor else incomeColor

            Text(
                text = "$amountPrefix${formatAmountWithCurrency(transaction.amount)}",
                fontSize = 13.sp,
                color = amountColor,
                fontWeight = FontWeight.Bold
            )
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.05f)
            )
        }
    }
}