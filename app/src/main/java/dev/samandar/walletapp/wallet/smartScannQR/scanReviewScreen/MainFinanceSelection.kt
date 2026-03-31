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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.domain.model.account.Account
import dev.samandar.walletapp.wallet.domain.model.Category
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.categories.getTranslatedName
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.categories.helper.getSafeIconId
import dev.samandar.walletapp.wallet.smartScannQR.ReviewUiState

@Composable
fun MainFinanceSelection(
    state: ReviewUiState,
    categories: List<Category>,
    accounts: List<Account>,
    onAccountSelected: (Account) -> Unit,
    onCategorySelected: (Category) -> Unit,
) {
    val context = LocalContext.current
    var showCategoryDialog by remember { mutableStateOf(false) }
    var showAccountDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // --- SELECTED ACCOUNT CARD ---
        state.selectedAccount?.let { account ->
            val safeAccountIcon = remember(account.iconResId) {
                getSafeIconId(context, account.iconResId ?: 0)
            }
            val cardColor = remember(account.colorHex) {
                try {
                    Color(account.colorHex?.toColorInt() ?: 0xFF6200EE.toInt())
                } catch (e: Exception) {
                    Color(0xFF6200EE.toInt())
                }
            }
            FinanceCard(
                modifier = Modifier.weight(1f),
                icon = safeAccountIcon,
                label = stringResource(Strings.icon_content_description_account),
                value = getTranslatedName(account.name).toString(),
                onClick = { showAccountDialog = true },
                color = cardColor
            )
        }

        // --- SELECTED CATEGORY CARD ---
        state.selectedCategory?.let { category ->
            val safeCategoryIcon = remember(category.iconResId) {
                getSafeIconId(context, category.iconResId ?: 0)
            }
            FinanceCard(
                modifier = Modifier.weight(1f),
                icon = safeCategoryIcon,
                label = stringResource(Strings.category_label_title),
                value = getTranslatedName(category.name).toString(),
                onClick = { showCategoryDialog = true },
                color = Color(category.colorArgb)
            )
        }
    }

    // --- ACCOUNT SELECTION DIALOG ---
    if (showAccountDialog) {
        PremiumSelectionDialog(
            title = stringResource(Strings.title_select_wallet),
            items = accounts,
            selectedItem = state.selectedAccount,
            onItemSelected = onAccountSelected,
            onDismiss = { showAccountDialog = false },
            itemContent = { account, isSelected ->
                val safeItemIcon = remember(account.iconResId) {
                    getSafeIconId(context, account.iconResId ?: 0)
                }
                SelectionItemContent(
                    name = getTranslatedName(account.name).toString(),
                    isSelected = isSelected,
                    icon = safeItemIcon,
                    color = remember(account.colorHex) {
                        try {
                            Color(account.colorHex?.toColorInt() ?: 0xFF6200EE.toInt())
                        } catch (e: Exception) {
                            Color(0xFF6200EE.toInt())
                        }
                    }
                )
            }
        )
    }

    // --- CATEGORY SELECTION DIALOG ---
    if (showCategoryDialog) {
        PremiumSelectionDialog(
            title = stringResource(Strings.category_label_placeholder),
            items = categories,
            selectedItem = state.selectedCategory,
            onItemSelected = onCategorySelected,
            onDismiss = { showCategoryDialog = false },
            itemContent = { category, isSelected ->
                val safeItemIcon = remember(category.iconResId) {
                    getSafeIconId(context, category.iconResId ?: 0)
                }
                SelectionItemContent(
                    name = getTranslatedName(category.name).toString(),
                    isSelected = isSelected,
                    icon = safeItemIcon,
                    color = Color(category.colorArgb),
                )
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
