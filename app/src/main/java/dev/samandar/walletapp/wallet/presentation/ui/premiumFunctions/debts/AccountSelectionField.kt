package dev.samandar.walletapp.wallet.presentation.ui.premiumFunctions.debts

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.domain.model.Account
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.categories.getTranslatedName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSelectionField(
    accounts: List<Account>,
    selectedAccount: Account?,
    onAccountSelect: (Account) -> Unit,
    accentColor: Color
) {
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showSheet = true }
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            modifier = Modifier.size(44.dp),
            shape = RoundedCornerShape(14.dp),
            color = accentColor.copy(alpha = 0.12f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(Strings.title_account),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.6f)
            )
            Text(
                text = getTranslatedName(selectedAccount?.name ?: stringResource(Strings.placeholder_select_account)).toString(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
            )
        }

        Icon(
            Icons.Default.KeyboardArrowDown,
            null,
            tint = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.size(24.dp)
        )
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
            tonalElevation = 8.dp,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 40.dp)
            ) {
                Text(
                    stringResource(Strings.title_select_wallet),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                    modifier = Modifier.padding(bottom = 20.dp, start = 4.dp),
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                )

                accounts.forEach { account ->
                    val isSelected = account.id == selectedAccount?.id
                    val accountColor = remember(account.colorHex) {
                        try {
                            Color(android.graphics.Color.parseColor(account.colorHex ?: "#808080"))
                        } catch (e: Exception) {
                            accentColor
                        }
                    }

                    Surface(
                        onClick = {
                            onAccountSelect(account)
                            showSheet = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) accountColor.copy(0.05f) else MaterialTheme.colorScheme.onTertiary.copy(0.03f),
                        border = if (isSelected) BorderStroke(1.dp, accountColor.copy(alpha = 0.5f)) else null
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(35.dp)
                                    .background(accountColor.copy(0.1f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ){
                                Icon(
                                    painter = painterResource(account.iconResId?: R.drawable.ic_card_default),
                                    tint = Color.Unspecified,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(20.dp)
                                )
                            }

                            Spacer(Modifier.width(16.dp))

                            Text(
                                getTranslatedName(account.name).toString(),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                            )

                            if (isSelected) {
                                Spacer(Modifier.weight(1f))
                                Icon(Icons.Default.Check, null, tint = accentColor, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}