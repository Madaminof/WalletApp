package com.example.walletapp.wallet.presentation.ui.charts.expenseListComponents

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.R
import com.example.walletapp.ui.theme.expenseColor
import com.example.walletapp.ui.theme.incomeColor
import com.example.walletapp.wallet.domain.model.Transaction
import com.example.walletapp.wallet.domain.model.TransactionType
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

data class Category(val name: String, val iconResId: Int?)
data class Account(val name: String,val iconResId: Int?)

@Composable
fun ExpenseTransactionItem(
    transaction: Transaction,
    onItemClick: (Transaction) -> Unit
) {
    val offsetX = remember { Animatable(0f) }

    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = {
                        onItemClick(transaction)
                    }
                )
            }
    ) {
        ExpenseTransactionItemContent(
            transaction = transaction,
            modifier = Modifier
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
        )
        Divider(
            modifier = Modifier
                .padding(start = 76.dp, end = 16.dp)
                .align(Alignment.BottomStart),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            thickness = 1.dp
        )
    }
}

@Composable
private fun ExpenseTransactionItemContent(
    transaction: Transaction,
    modifier: Modifier
) {
    val dateFormatter = remember { SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()) }

    val amountColor = when (transaction.type) {
        TransactionType.EXPENSE -> expenseColor
        TransactionType.INCOME -> incomeColor
    }
    val iconBackgroundColor = when (transaction.type) {
        TransactionType.EXPENSE -> expenseColor.copy(alpha = 0.15f)
        TransactionType.INCOME -> incomeColor.copy(alpha = 0.15f)
    }
    val iconTintColor = when (transaction.type) {
        TransactionType.EXPENSE -> expenseColor
        TransactionType.INCOME -> incomeColor
    }

    val amountPrefix = if (transaction.type == TransactionType.EXPENSE) "-" else "+"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(35.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBackgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = transaction.category?.iconResId ?: R.drawable.google_icon),
                contentDescription = null,
                tint = iconTintColor,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.category?.name?:"",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                lineHeight = 12.sp
            )
            Row {
                Text(
                    text = transaction.account.name,
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,

                )
                Text(
                    text = " • ${dateFormatter.format(transaction.date)}",
                    fontSize = 8.sp,
                    color = Color.Gray.copy(alpha = 0.8f)
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$amountPrefix ${"%,.0f".format(transaction.amount)} UZS",
            fontSize = 12.sp,
            color = amountColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(end = 4.dp)
        )
    }
}
