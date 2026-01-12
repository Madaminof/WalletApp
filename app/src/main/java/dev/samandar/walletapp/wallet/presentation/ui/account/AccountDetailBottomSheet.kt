package dev.samandar.walletapp.wallet.presentation.ui.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.domain.model.Account
import dev.samandar.walletapp.wallet.presentation.ui.charts.expenseListComponents.DeleteConfirmationDialog
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.categories.getTranslatedName
import dev.samandar.walletapp.wallet.presentation.utils.FormatAmount


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailBottomSheet(
    account: Account,
    onDismiss: () -> Unit,
    onUpdate: (Account) -> Unit,
    onDelete: (Account) -> Unit
) {
    val accountName = getTranslatedName(account.name)

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val accountColor = account.colorHex?.let {
        try { Color(android.graphics.Color.parseColor(it)) } catch (e: IllegalArgumentException) { MaterialTheme.colorScheme.primary }
    } ?: MaterialTheme.colorScheme.primary
    val iconBackgroundColor = accountColor.copy(alpha = 0.15f)

    var showDeleteDialog by remember { mutableStateOf(false) }


    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiary,
        tonalElevation = 16.dp,
        dragHandle = {
            BottomSheetDefaults.DragHandle()
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = accountName.toString(),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = { onUpdate(account) },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = stringResource(Strings.action_edit_account),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    if (account.name != "Cash" && account.name != "Card") {
                        IconButton(
                            onClick = { showDeleteDialog =true },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = stringResource(Strings.action_delete_account),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                account.iconResId?.let { iconRes ->
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(iconBackgroundColor),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = null,
                            modifier = Modifier.size(36.dp),
                            tint = Color.Unspecified
                        )
                    }
                }
                Column {
                    Text(
                        text = stringResource(Strings.current_balance),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                    Text(
                        text = FormatAmount(account.initialBalance),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onTertiary,
                        fontSize = 16.sp
                    )
                }
            }
            Spacer(Modifier.height(32.dp))
        }
        if (showDeleteDialog){
            DeleteConfirmationDialog(
                onDismiss = {showDeleteDialog = false},
                onConfirmDelete = {onDelete(account)},
                title = stringResource(R.string.account_delete_dialog_title),
                text = stringResource(R.string.account_delete_dialog_text)
            )
        }
    }
}