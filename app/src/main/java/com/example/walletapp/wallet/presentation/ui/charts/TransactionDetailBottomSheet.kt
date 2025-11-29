package com.example.walletapp.wallet.presentation.ui.charts

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.ui.theme.expenseColor
import com.example.walletapp.ui.theme.incomeColor
import com.example.walletapp.wallet.domain.model.Transaction
import com.example.walletapp.wallet.domain.model.TransactionType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import java.text.NumberFormat


fun formatAmount(amount: Double): String {
    val formatter = NumberFormat.getInstance(Locale.US)
    return formatter.format(amount).replace(",", " ")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailBottomSheet(
    transaction: Transaction,
    onDismiss: () -> Unit,
    onUpdate: (Transaction) -> Unit,
    onDelete: (Transaction) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val dateTimeFormatter = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 16.dp,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(width = 50.dp, height = 5.dp)
                    .clip(RoundedCornerShape(2.5.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            TransactionHeader(
                transaction = transaction,
                onUpdate = onUpdate,
                onDelete = onDelete
            )
            Spacer(Modifier.height(16.dp))
            PremiumTransactionSummaryCard(transaction = transaction)

            Spacer(Modifier.height(24.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.height(16.dp))

            transaction.note?.let { note ->
                if (note.isNotEmpty()) {
                    ExpandableNoteCard(note = note)
                    Spacer(Modifier.height(8.dp))
                }
            }
            DetailSectionCard(
                transaction = transaction,
                dateTimeFormatter = dateTimeFormatter
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun DetailSectionCard(
    transaction: Transaction,
    dateTimeFormatter: SimpleDateFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        ) {
            PremiumDetailRow(
                label = "Hamyon",
                value = transaction.account.name,
                icon = Icons.Default.AccountBalanceWallet,
                valueColor = MaterialTheme.colorScheme.onTertiary.copy(0.7f)

            )
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
            PremiumDetailRow(
                label = "Turi",
                value = if (transaction.type == TransactionType.EXPENSE) "Chiqim" else "Daromad",
                icon = Icons.Default.Category,
                valueColor = if (transaction.type == TransactionType.EXPENSE) expenseColor else incomeColor
            )
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
            PremiumDetailRow(
                label = "Sana va vaqt",
                value = dateTimeFormatter.format(Date(transaction.date)),
                icon = Icons.Default.Schedule,
                valueColor = MaterialTheme.colorScheme.onTertiary.copy(0.7f)
            )
        }
    }
}

@Composable
fun PremiumDetailRow(label: String, value: String, icon: ImageVector, valueColor: Color = MaterialTheme.colorScheme.onSurface) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onTertiary,
                fontSize = 14.sp
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            color = valueColor,
            modifier = Modifier.weight(1f, fill = false)
        )
    }
}


@Composable
fun TransactionHeader(
    transaction: Transaction,
    onUpdate: (Transaction) -> Unit,
    onDelete: (Transaction) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.category.name,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 20.sp
            )
            Text(
                text = "Tranzaksiya ma'lumotlari",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(onClick = { onUpdate(transaction) }) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Tahrirlash",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
            IconButton(onClick = { onDelete(transaction) }) {
                Icon(
                    imageVector = Icons.Default.DeleteForever,
                    contentDescription = "O'chirish",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}


@Composable
fun PremiumTransactionSummaryCard(transaction: Transaction) {
    val typeColor = if (transaction.type == TransactionType.EXPENSE) expenseColor else incomeColor
    val icon = if (transaction.type == TransactionType.EXPENSE) Icons.Default.RemoveCircle else Icons.Default.AddCircle

    val formattedAmount = formatAmount(transaction.amount)
    val amountPrefix = if (transaction.type == TransactionType.EXPENSE) "-" else "+"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = typeColor.copy(alpha = 0.15f),
            contentColor = typeColor.copy(alpha = 0.9f)
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = typeColor,
                modifier = Modifier.size(30.dp)
            )
            Spacer(Modifier.width(20.dp))

            Column {
                Text(
                    text = "Umumiy Miqdor",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = typeColor
                        )) {
                            append("$amountPrefix $formattedAmount")
                        }
                        withStyle(style = SpanStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = typeColor.copy(alpha = 0.7f)
                        )) {
                            append(" UZS")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ExpandableNoteCard(note: String) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
    ) {
        Column(
            modifier = Modifier
                .clickable { expanded = !expanded }
                .padding(16.dp)
                .animateContentSize(animationSpec = tween(300))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Notes,
                    contentDescription = "Izoh",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(16.dp))
                Text(
                    text = "Izoh",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onTertiary,
                    modifier = Modifier.weight(1f),
                    fontSize = 14.sp,
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Yopish" else "Kengaytirish",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = note,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.8f),
                maxLines = if (expanded) Int.MAX_VALUE else 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 12.sp,
            )
        }
    }
}