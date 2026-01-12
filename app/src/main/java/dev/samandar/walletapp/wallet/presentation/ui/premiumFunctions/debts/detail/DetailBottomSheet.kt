package dev.samandar.walletapp.wallet.presentation.ui.premiumFunctions.debts.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCard
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
import dev.samandar.walletapp.ui.theme.expenseColor
import dev.samandar.walletapp.ui.theme.incomeColor
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.domain.model.debt.Debt
import dev.samandar.walletapp.wallet.domain.model.debt.DebtTransaction
import dev.samandar.walletapp.wallet.domain.model.debt.DebtType


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtDetailBottomSheet(
    debt: Debt,
    transactions: List<DebtTransaction>,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onAddPayment: (Debt) -> Unit,
    onEdit: (Debt) -> Unit,
    onDelete: (Debt) -> Unit
) {
    val accentColor = if (debt.type == DebtType.LENT) incomeColor else expenseColor
    val progress = remember(debt) {
        if (debt.totalAmount > 0)
            ((debt.totalAmount - debt.remainingAmount) / debt.totalAmount).toFloat().coerceIn(0f, 1f)
        else 0f
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
        tonalElevation = 0.dp,
        dragHandle = { BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.outlineVariant) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            DebtDetailHeader(
                debt = debt,
                accentColor = accentColor,
                onEdit = { onEdit(debt) },
                onDelete = { onDelete(debt) }
            )

            Spacer(Modifier.height(16.dp))

            DebtProgressCard(
                debt = debt,
                progress = progress,
                accentColor = accentColor
            )

            Spacer(Modifier.height(24.dp))

            if (!debt.isSettled) {
                Button(
                    onClick = { onAddPayment(debt) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(Icons.Default.AddCard, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(stringResource(Strings.add_payment), fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(28.dp))

            Text(
                text = stringResource(Strings.payment_history),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp,
                color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                fontSize = 20.sp
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 150.dp, max = 400.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (transactions.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                stringResource(Strings.no_payments_yet),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                } else {
                    items(transactions, key = { it.id }) { transaction ->
                        PaymentHistoryRow(transaction, accentColor)
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}