package dev.samandar.walletapp.wallet.smartScannQR.scanReviewScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.domain.model.Account
import dev.samandar.walletapp.wallet.domain.model.Category
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.categories.getTranslatedName
import dev.samandar.walletapp.wallet.smartScannQR.ReviewUiState

@Composable
fun MainFinanceSelection(
    state: ReviewUiState,
    categories: List<Category>,
    accounts: List<Account>,
    onAccountSelected: (Account) -> Unit,
    onCategorySelected: (Category) -> Unit,
) {
    var showCategoryDialog by remember { mutableStateOf(false) }
    var showAccountDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        state.selectedAccount?.iconResId?.let {
            state.selectedAccount.colorHex?.let { it1 ->
                FinanceCard(
                    modifier = Modifier.weight(1f),
                    icon = it,
                    label = stringResource(Strings.icon_content_description_account),
                    value = getTranslatedName(state.selectedAccount.name).toString(),
                    onClick = { showAccountDialog = true },
                    color = remember(state.selectedAccount.colorHex) {
                        try {
                            Color(
                                state.selectedAccount.colorHex?.toColorInt() ?: 0xFF6200EE.toInt()
                            )
                        } catch (e: Exception) {
                            Color(0xFF6200EE.toInt())
                        }
                    }
                )
            }
        }
        state.selectedCategory?.iconResId?.let {
            FinanceCard(
                modifier = Modifier.weight(1f),
                icon = it,
                label = stringResource(Strings.category_label_title),
                value = getTranslatedName(state.selectedCategory.name).toString(),
                onClick = { showCategoryDialog = true },
                color = Color(state.selectedCategory.colorArgb)
            )
        }
    }
    if (showAccountDialog) {
        PremiumSelectionDialog(
            title = stringResource(Strings.title_select_wallet),
            items = accounts,
            selectedItem = state.selectedAccount,
            onItemSelected = onAccountSelected,
            onDismiss = { showAccountDialog = false },
            itemContent = { account, isSelected ->
                account.iconResId?.let {
                    SelectionItemContent(
                        name = getTranslatedName(account.name).toString(),
                        isSelected = isSelected,
                        icon = it,
                        color = remember(account.colorHex) {
                            try {
                                Color(account.colorHex?.toColorInt() ?: 0xFF6200EE.toInt())
                            } catch (e: Exception) {
                                Color(0xFF6200EE.toInt())
                            }
                        }
                    )
                }
            }
        )
    }

    // KATEGORIYA TANLASH DIALOGI
    if (showCategoryDialog) {
        PremiumSelectionDialog(
            title = stringResource(Strings.category_label_placeholder),
            items = categories,
            selectedItem = state.selectedCategory,
            onItemSelected = onCategorySelected,
            onDismiss = { showCategoryDialog = false },
            itemContent = { category, isSelected ->
                category.iconResId?.let {
                    SelectionItemContent(
                        name = getTranslatedName(category.name).toString(),
                        isSelected = isSelected,
                        icon = it,
                        color = Color(category.colorArgb),
                    )
                }
            }
        )
    }
}

@Composable
fun SelectionItemContent(
    name: String,
    isSelected: Boolean,
    icon: Int,
    color: Color,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Rounded.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
