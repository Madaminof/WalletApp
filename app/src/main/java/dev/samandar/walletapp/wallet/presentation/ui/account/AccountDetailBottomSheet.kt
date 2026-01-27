package dev.samandar.walletapp.wallet.presentation.ui.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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

    val accountColor = remember(account.colorHex) {
        try { Color(android.graphics.Color.parseColor(account.colorHex ?: "#1976D2")) }
        catch (e: Exception) { Color.Gray }
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = 0.dp,
        dragHandle = { BottomSheetDefaults.DragHandle(color = accountColor.copy(alpha = 0.3f)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp, top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(accountColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = account.iconResId ?: R.drawable.cash_icon2),
                    contentDescription = null,
                    modifier = Modifier.size(42.dp),
                    tint = accountColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = accountName.toString(),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                ),
                color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(1.dp, accountColor.copy(alpha = 0.1f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(Strings.current_balance).uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onTertiary.copy(0.6f),
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = FormatAmount(account.initialBalance),
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Black,
                            color = accountColor,
                            fontSize = 20.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Edit Button
                Button(
                    onClick = { onDismiss(); onUpdate(account) },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accountColor,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(Icons.Default.Edit, contentSize = 18.dp)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(Strings.action_edit_account), fontWeight = FontWeight.Bold)
                }

                // Delete Button (Faqat tizim hisobi bo'lmasa)
                if (account.name != "Cash" && account.name != "Card") {
                    FilledTonalIconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f),
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(painter = painterResource(R.drawable.delete_icon), contentDescription = null)
                    }
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
private fun Icon(imageVector: androidx.compose.ui.graphics.vector.ImageVector, contentSize: androidx.compose.ui.unit.Dp) {
    Icon(imageVector = imageVector, contentDescription = null, modifier = Modifier.size(contentSize))
}