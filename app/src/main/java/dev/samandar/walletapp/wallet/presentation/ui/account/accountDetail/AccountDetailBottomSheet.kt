package dev.samandar.walletapp.wallet.presentation.ui.account.accountDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.samandar.walletapp.R
import dev.samandar.walletapp.navigation.Screen
import dev.samandar.walletapp.ui.theme.expenseColor
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.data.local.entity.account.AccountType
import dev.samandar.walletapp.wallet.domain.model.account.Account
import dev.samandar.walletapp.wallet.presentation.ui.account.accountScreen.getCurrencySymbol
import dev.samandar.walletapp.wallet.presentation.ui.charts.expenseListComponents.DeleteConfirmationDialog
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.categories.getTranslatedName
import dev.samandar.walletapp.wallet.presentation.utils.FormatAmountAccount

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountDetailBottomSheet(
    account: Account,
    navController: NavController,
    onDismiss: () -> Unit,
    onUpdate: (Account) -> Unit,
    onDelete: (Account) -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Account rangini aniqlash
    val accountColor = remember(account.colorHex) {
        try {
            Color(android.graphics.Color.parseColor(account.colorHex))
        } catch (e: Exception) {
            Color(0xFF1976D2)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray) },
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(Strings.account_properties),
                    modifier = Modifier.align(Alignment.CenterStart),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(32.dp)
                ) {
                    Icon(Icons.Rounded.Close, contentDescription = null, tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            AccountVisualHeader(account, accountColor)

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(0.5f))
            ) {
                DetailMenuItem(
                    icon = Icons.Rounded.Edit,
                    label = stringResource(Strings.action_edit_account),
                    onClick = { onDismiss(); onUpdate(account) }
                )

                Divider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 0.5.dp,
                    color = Color.LightGray.copy(0.3f)
                )

                DetailMenuItem(
                    icon = Icons.Rounded.History,
                    label = stringResource(Strings.transaction_history),
                    onClick = {
                        onDismiss()
                        navController.navigate(Screen.ExpenseList.createRoute(account.id))
                    }
                )

                Divider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 0.5.dp,
                    color = Color.LightGray.copy(0.3f)
                )

                val isSystem = account.id == "default_cash" || account.id == "default_card"
                if (!isSystem) {
                    DetailMenuItem(
                        icon = Icons.Rounded.DeleteSweep,
                        label = stringResource(Strings.action_delete_account),
                        contentColor = MaterialTheme.colorScheme.error,
                        onClick = { showDeleteDialog = true }
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirmDelete = { onDelete(account); showDeleteDialog = false; onDismiss() },
            title = stringResource(R.string.account_delete_dialog_title),
            text = stringResource(R.string.account_delete_dialog_text)
        )
    }
}


@Composable
fun getAccountTypeDisplayName(type: AccountType): String {
    return when (type) {
        AccountType.CASH -> stringResource(R.string.acc_cash)
        AccountType.CARD -> stringResource(R.string.acc_card)
        // Boshqa turlar bo'lsa shu yerga qo'shiladi
    }
}

@Composable
private fun AccountVisualHeader(account: Account, accountColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(accountColor.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = account.iconResId ?: R.drawable.cash_icon2),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = accountColor
            )
        }

        Spacer(modifier = Modifier.width(12.dp))


        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = getTranslatedName(account.name).toString(),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
            )
            if (account is Account.Card && !account.cardNumber.isNullOrBlank()) {
                val firstFour =
                    if (account.cardNumber.length >= 4) account.cardNumber.take(4) else "****"
                val lastFour =
                    if (account.cardNumber.length >= 8) account.cardNumber.takeLast(4) else "****"

                Text(
                    text = "$firstFour **** **** $lastFour",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            } else {
                Text(
                    text = getAccountTypeDisplayName(account.type),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${FormatAmountAccount(account.amountCurrencyKonverter)} ${
                    getCurrencySymbol(
                        account.currencyCode
                    )
                }",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                color = if (account.balance < 0) expenseColor else MaterialTheme.colorScheme.onTertiary.copy(
                    0.8f
                )

            )
            /*if (account.currencyCode != "UZS") {
                Text(
                    text = "≈ ${FormatAmountAccount(account.balance)} so'm",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.LightGray,
                        fontSize = 10.sp
                    )
                )
            }*/
        }
    }
}

@Composable
private fun DetailMenuItem(
    icon: ImageVector,
    label: String,
    contentColor: Color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(contentColor.copy(alpha = 0.05f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = contentColor
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge,
            color = contentColor
        )

        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = Color.LightGray
        )
    }
}