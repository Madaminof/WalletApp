package com.example.walletapp.wallet.presentation.ui.otherScreens.debts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.ui.theme.expenseColor
import com.example.walletapp.ui.theme.incomeColor
import com.example.walletapp.wallet.domain.model.Debt
import com.example.walletapp.wallet.presentation.ui.charts.expenseListComponents.DeleteConfirmationDialog // delete dialog uchun
import com.example.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencyManager
import com.example.walletapp.wallet.presentation.utils.FormatAmount
import com.example.walletapp.wallet.presentation.utils.formatAmountWithCurrency
import com.example.walletapp.wallet.presentation.utils.getCurrencySymbol
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val activeCurrency by CurrencyManager.currentCurrency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtDetailBottomSheet(
    debt: Debt,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onEdit: (Debt) -> Unit,
    onDelete: (Debt) -> Unit
) {
    val dateTimeFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val isLent = debt.isLent
    val lentColor = expenseColor
    val owedColor = incomeColor
    val primaryColor = if (isLent) lentColor else owedColor

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiary,
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
            DebtHeader(
                debt = debt,
                primaryColor = primaryColor,
                onUpdate = onEdit,
                onDelete = { showDeleteDialog = true }
            )
            Spacer(Modifier.height(16.dp))

            PremiumDebtSummaryCard(debt = debt, primaryColor = primaryColor)

            Spacer(Modifier.height(24.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.height(16.dp))

            DebtDetailSectionCard(
                debt = debt,
                dateFormatter = dateTimeFormatter,
                primaryColor = primaryColor
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (showDeleteDialog){
            DeleteConfirmationDialog(
                onDismiss = { showDeleteDialog = false },
                onConfirmDelete = {
                    onDelete(debt)
                    onDismiss()
                },
                title = "Delete Debt",
                text = "Rostdan ham ushbu qarz ma'lumotini o'chirishni xohlaysizmi?"
            )
        }
    }
}

@Composable
fun DebtHeader(
    debt: Debt,
    primaryColor: Color,
    onUpdate: (Debt) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = debt.person,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = primaryColor,
                fontSize = 20.sp
            )
            Text(
                text = "Qarz tafsilotlari",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(onClick = { onUpdate(debt) }) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.onTertiary.copy(0.6f)
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun PremiumDebtSummaryCard(debt: Debt, primaryColor: Color) {
    val isLent = debt.isLent
    val typeText = if (isLent) "BERILGAN QARZ" else "OLINGAN QARZ"
    val icon = if (isLent) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = primaryColor.copy(alpha = 0.09f),
            contentColor = primaryColor.copy(alpha = 0.8f)
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
                tint = primaryColor.copy(0.8f),
                modifier = Modifier.size(30.dp)
            )
            Spacer(Modifier.width(20.dp))

            Column {
                Text(
                    text = typeText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.8f),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = formatAmountWithCurrency(debt.amount),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = primaryColor,
                )
            }
        }
    }
}

@Composable
fun DebtDetailSectionCard(
    debt: Debt,
    dateFormatter: SimpleDateFormat,
    primaryColor: Color
) {
    val statusColor = if (debt.isSettled) MaterialTheme.colorScheme.tertiary else primaryColor

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        ) {
            PremiumDetailRow(
                label = "Shaxs/Tashkilot",
                value = debt.person,
                icon = { Icon(Icons.Default.Person, contentDescription = null, tint = primaryColor, modifier = Modifier.size(20.dp)) },
                valueColor = MaterialTheme.colorScheme.onTertiary
            )
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))

            PremiumDetailRow(
                label = "Sana",
                value = dateFormatter.format(Date(debt.date)),
                icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = primaryColor, modifier = Modifier.size(20.dp)) },
                valueColor = MaterialTheme.colorScheme.onTertiary
            )
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))

            PremiumDetailRow(
                label = "Holat",
                value = if (debt.isSettled) "Qaytarilgan" else "Kutilmoqda",
                icon = { Icon(if (debt.isSettled) Icons.Default.CheckCircle else Icons.Default.Update, contentDescription = null, tint = statusColor, modifier = Modifier.size(20.dp)) },
                valueColor = statusColor
            )
        }
    }
}

@Composable
fun PremiumDetailRow(
    label: String,
    value: String,
    icon: @Composable () -> Unit,
    valueColor: Color = MaterialTheme.colorScheme.onTertiary.copy(0.7f)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
            icon()
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
            fontSize = 12.sp,
            color = valueColor,
            modifier = Modifier.weight(1f, fill = false)
        )
    }
}