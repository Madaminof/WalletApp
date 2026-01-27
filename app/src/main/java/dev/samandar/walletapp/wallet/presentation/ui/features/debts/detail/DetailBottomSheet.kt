package dev.samandar.walletapp.wallet.presentation.ui.features.debts.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.ui.theme.expenseColor
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
    val accentColor = if (debt.type == DebtType.LENT) MaterialTheme.colorScheme.primary else expenseColor

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
        tonalElevation = 8.dp,
        dragHandle = { BottomSheetDefaults.DragHandle(color = MaterialTheme.colorScheme.outlineVariant.copy(0.4f)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
        ) {

            DebtDetailHeader(
                debt = debt,
                accentColor = accentColor,
                onEdit = {
                    onDismiss()
                    onEdit(debt)
                },
                onDelete = {
                    onDelete(debt)
                }
            )

            Spacer(Modifier.height(24.dp))

            DebtProgressCard(debt, accentColor)

            Spacer(Modifier.height(24.dp))

            if (!debt.isSettled) {
                Button(
                    onClick = { onAddPayment(debt) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 4.dp)
                ) {
                    Icon(Icons.Default.AddCard, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Text(
                        stringResource(R.string.add_payment),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.payment_history),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                ),
                color = MaterialTheme.colorScheme.onTertiary
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 350.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (transactions.isEmpty()) {
                    item { EmptyHistoryState() }
                } else {
                    items(transactions, key = { it.id }) { transaction ->
                        PaymentHistoryRow(transaction, accentColor)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}