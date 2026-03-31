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
import androidx.compose.ui.platform.LocalContext
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.categories.helper.getSafeIconId


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
            // --- Icon Box (O'zgarmadi) ---
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconColor.copy(0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                val context = LocalContext.current
                val safeIconId = remember(category.iconResId) {
                    val id = category.iconResId ?: 0
                    if (id > 0) {
                        getSafeIconId(context, id)
                    } else {
                        R.drawable.ic_other
                    }
                }
                Icon(
                    painter = painterResource(id = safeIconId),
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // --- Category & Account Info (O'zgarmadi) ---
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displayName.toString(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onTertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = accountName.toString(),
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.5f),
                    maxLines = 1
                )
            }

            // --- Amount Section (YANGILANDI) ---
            Column(horizontalAlignment = Alignment.End) {
                val amountPrefix = if (transaction.type == TransactionType.EXPENSE) "-" else "+"
                val amountColor = if (transaction.type == TransactionType.EXPENSE) expenseColor else incomeColor

                // 1. Asosiy Balans (UZS da)
                Text(
                    text = "$amountPrefix${formatAmountWithCurrency(transaction.amount)}",
                    fontSize = 13.sp,
                    color = amountColor,
                    fontWeight = FontWeight.Bold
                )

                // 2. Original Valyuta (Faqat UZS bo'lmasa ko'rsatiladi)
                if (transaction.originalCurrency != "UZS") {
                    Text(
                        text = "$amountPrefix${transaction.originalAmount} ${transaction.originalCurrency}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.4f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
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