package dev.samandar.walletapp.wallet.presentation.ui.charts.expenseListComponents

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
import java.text.SimpleDateFormat
import java.util.Locale

data class Category(val name: String, val iconResId: Int?)
data class Account(val name: String,val iconResId: Int?)

@Composable
fun ExpenseTransactionItem(
    transaction: Transaction,
    index: Int,
    modifier: Modifier,
    onItemClick: (Transaction) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy),
        label = "press_scale"
    )

    Surface(
        onClick = { onItemClick(transaction) },
        interactionSource = interactionSource,
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        Column {
            ExpenseTransactionItemContent(transaction = transaction)

            Divider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onTertiary.copy(0.07f),
                thickness = 0.5.dp
            )
        }
    }
}

@Composable
private fun ExpenseTransactionItemContent(transaction: Transaction) {
    val dateText = remember(transaction.date) {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(transaction.date)
    }

    val category = transaction.category
    val displayName = getTranslatedName(category.name)
    val accountName = getTranslatedName(transaction.account.name)

    val iconColor = remember(category.colorArgb) { Color(category.colorArgb) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(35.dp)
                .background(iconColor.copy(0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = category.iconResId ?: R.drawable.ic_other),
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = displayName.toString(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )
            Text(
                text = "${accountName} | $dateText",
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.5f),
                lineHeight = 14.sp
            )
        }

        val amountPrefix = if (transaction.type == TransactionType.EXPENSE) "-" else "+"
        val amountColor = if (transaction.type == TransactionType.EXPENSE) expenseColor else incomeColor

        Text(
            text = "$amountPrefix${formatAmountWithCurrency(transaction.amount)}",
            fontSize = 12.sp,
            color = amountColor,
            fontWeight = FontWeight.Bold
        )
    }
}