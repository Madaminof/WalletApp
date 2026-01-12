package dev.samandar.walletapp.wallet.presentation.ui.charts.detailScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.samandar.walletapp.R
import dev.samandar.walletapp.ui.theme.expenseColor
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.domain.model.Transaction
import dev.samandar.walletapp.wallet.domain.model.TransactionType
import dev.samandar.walletapp.wallet.presentation.ui.charts.expenseListComponents.DeleteConfirmationDialog
import dev.samandar.walletapp.wallet.presentation.ui.topbars.topbarScreen.CustomTopBar
import dev.samandar.walletapp.wallet.presentation.utils.FormatAmount
import dev.samandar.walletapp.wallet.presentation.viewmodel.HomeViewModel

@Composable
fun TransactionDetailScreen(
    transaction: Transaction,
    viewModel: HomeViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onEdit: (Transaction) -> Unit,
    onDelete: (Transaction) -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    val incomeCats by viewModel.incomeCategories.collectAsState()
    val expenseCats by viewModel.expenseCategories.collectAsState()

    val filteredCategories = if (transaction.type == TransactionType.INCOME) {
        incomeCats
    } else {
        expenseCats
    }
    val sign = if (transaction.type == TransactionType.INCOME) "+" else "-"
    val formattedAmountWithSign = "$sign ${FormatAmount(transaction.amount)}"
    val accounts by viewModel.accounts.collectAsState()

    Scaffold(
        topBar = {
            CustomTopBar(
                title = stringResource(Strings.details),
                onBackClick = {onBack()},
            )
        },
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            TransactionMainCard(
                transaction = transaction,
                categories = filteredCategories,
                accounts = accounts,
                onEdit = onEdit
            )

            Spacer(modifier = Modifier.height(12.dp))

            ActionButtonsRow(
                onDelete = { showDeleteDialog = true },
                transaction =transaction,
                amountText = formattedAmountWithSign,
            )
        }
    }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirmDelete = {
                onDelete(transaction)
                showDeleteDialog = false
            },
            title = stringResource(R.string.delete_dialog_title),
            text = stringResource(R.string.delete_dialog_text)
        )
    }
}


@Composable
fun ActionButtonsRow(
    onDelete: () -> Unit,
    transaction: Transaction,
    amountText: String
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilledTonalButton(
            onClick = { shareTransaction(context, transaction, amountText) },
            modifier = Modifier.height(44.dp),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.5f),
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            Icon(
                imageVector = Icons.Rounded.Share,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.primary

            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(Strings.list_row_share_button),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        FilledIconButton(
            onClick = onDelete,
            modifier = Modifier.size(44.dp),
            shape = RoundedCornerShape(12.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.5f),
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(
                imageVector = Icons.Rounded.DeleteOutline,
                contentDescription = "Delete",
                modifier = Modifier.size(22.dp),
                tint = expenseColor
            )
        }
    }
}
