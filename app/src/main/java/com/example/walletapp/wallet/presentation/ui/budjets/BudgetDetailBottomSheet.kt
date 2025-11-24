package com.example.walletapp.wallet.presentation.ui.budjets

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.walletapp.wallet.domain.model.Budget
import com.example.walletapp.wallet.domain.model.BudgetStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetDetailBottomSheet(
    budgetStatus: BudgetStatus,
    onDismiss: () -> Unit,
    onUpdate: (Budget) -> Unit,
    onDelete: (BudgetStatus) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val budget = budgetStatus.budget
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onTertiary,
        tonalElevation = 16.dp,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = budget.category.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Budjet ma'lumotlari",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row {
                    IconButton(onClick = { onUpdate(budget) }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Budjetni tahrirlash",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    IconButton(onClick = { onDelete(budgetStatus) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Budjetni o'chirish",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            BudgetSummaryCard(status = budgetStatus)
            Spacer(Modifier.height(24.dp))
            Divider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.height(16.dp))
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DetailRow(
                    label = "Davr",
                    value = budget.period.name,
                    icon = Icons.Default.Timelapse
                )
                DetailRow(
                    label = "Boshlanish sanasi",
                    value = dateFormatter.format(Date(budget.startDate)),
                    icon = Icons.Default.CalendarToday
                )
                budget.endDate?.let { endDate ->
                    DetailRow(
                        label = "Tugash sanasi",
                        value = dateFormatter.format(Date(endDate)),
                        icon = Icons.Default.CalendarToday
                    )
                }
                DetailRow(
                    label = "Holat",
                    value = if (budget.isActive) "Faol" else "Nofaol",
                    icon = Icons.Default.Info
                )
                DetailRow(
                    label = "Qolgan kunlar",
                    value = "${budgetStatus.daysRemaining} kun",
                    icon = Icons.Default.Timer
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
@Composable
fun BudgetSummaryCard(status: BudgetStatus) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            DetailSummaryRow(
                label = "Maksimal Budjet",
                value = "${status.budget.maxAmount}",
                icon = Icons.Default.AccountBalanceWallet,
                valueColor = Color(0xFF66BB6A)
            )
            Divider(Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            DetailSummaryRow(
                label = "Sarflangan Miqdor",
                value = "${status.spentAmount}",
                icon = Icons.Default.RemoveCircle,
                valueColor = Color(0xFFE57373)
            )
            Divider(Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            DetailSummaryRow(
                label = "Qoldiq Miqdor",
                value = "${status.remainingAmount}",
                icon = Icons.Default.AddCircle,
                valueColor = if (status.isOverBudget) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = (status.percentageUsed / 100).toFloat().coerceIn(0f, 1f),
                color = if (status.isOverBudget) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${status.percentageUsed.toInt()}% sarflangan",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.End),
                color = MaterialTheme.colorScheme.onTertiary.copy(0.7f)
            )
        }
    }
}

@Composable
fun DetailRow(label: String, value: String, icon: ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onTertiary
            )
        }

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onTertiary
        )
    }
}
@Composable
fun DetailSummaryRow(label: String, value: String, icon: ImageVector, valueColor: Color = MaterialTheme.colorScheme.onPrimaryContainer) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onTertiary
            )
        }

        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = valueColor
        )
    }
}