package dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.account

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import dev.samandar.walletapp.R
import dev.samandar.walletapp.navigation.Screen
import dev.samandar.walletapp.ui.theme.defaultColor
import dev.samandar.walletapp.ui.theme.expenseColor
import dev.samandar.walletapp.wallet.domain.model.account.Account
import dev.samandar.walletapp.wallet.presentation.ui.account.accountScreen.getCurrencySymbol
import dev.samandar.walletapp.wallet.presentation.ui.account.addAccount.SelectAccountTypeBottomSheet
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.SoundManager
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.categories.getTranslatedName
import dev.samandar.walletapp.wallet.presentation.utils.FormatAmount
import dev.samandar.walletapp.wallet.presentation.utils.FormatAmountAccount

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSelectionDialog(
    accounts: List<Account>,
    selectedAccountId: String?,
    onAccountSelect: (Account) -> Unit,
    navController: NavController,
    onDismiss: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { true }
    )
    var showTypeSelectionSheet by remember { mutableStateOf(false) }


    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { BottomSheetDefaults.DragHandle(color = defaultColor.copy(0.5f)) },
        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        tonalElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Chap tomondagi matnlar
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.title_select_wallet),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-1).sp
                        ),
                        color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                    )
                    Text(
                        text = stringResource(R.string.subtitle_select_wallet),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onTertiary.copy(0.4f)
                    )
                }
                Surface(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        showTypeSelectionSheet = true
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.Add, // Import: androidx.compose.material.icons.rounded.Add
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(accounts, key = { _, acc -> acc.id }) { index, acc ->
                    AccountPremiumItem(
                        acc = acc,
                        index = index,
                        isSelected = acc.id == selectedAccountId,
                        onSelect = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onAccountSelect(it)
                            onDismiss()
                        }
                    )
                }
            }
        }
        if (showTypeSelectionSheet) {
            SelectAccountTypeBottomSheet(
                onTypeSelected = { type ->
                    showTypeSelectionSheet = false
                    navController.navigate("${Screen.addAccound.route}/${type.name}")
                },
                onDismiss = { showTypeSelectionSheet = false }
            )
        }
    }
}

@Composable
private fun AccountPremiumItem(
    acc: Account,
    index: Int,
    isSelected: Boolean,
    onSelect: (Account) -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val accountName = getTranslatedName(acc.name)


    val amountColor =
        if (acc.balance < 0) expenseColor else MaterialTheme.colorScheme.onTertiary.copy(
            0.6f
        )


    val animatedAlpha = animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 500, delayMillis = index * 40),
        label = "alpha"
    )

    val pressedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(durationMillis = 100)
    )

    val accColor = remember(acc.colorHex) {
        try { Color(acc.colorHex?.toColorInt() ?: 0xFF6200EE.toInt()) }
        catch (e: Exception) { Color(0xFF6200EE.toInt()) }
    }

    Surface(
        onClick = {
           /* SoundManager.playClick()*/
            onSelect(acc)
        },
        interactionSource = interactionSource,
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(0.8f) else MaterialTheme.colorScheme.primaryContainer.copy(0.8f),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) accColor.copy(0.8f) else Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                alpha = animatedAlpha.value
                scaleX = pressedScale
                scaleY = pressedScale
                translationY = (1f - animatedAlpha.value) * 15f
            }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(accColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = acc.iconResId ?: R.drawable.ic_card_default),
                    contentDescription = null,
                    tint = accColor,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = accountName.toString(),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                    fontSize = 14.sp
                )
                Text(
                    text = "${FormatAmountAccount(acc.balance)} ${getCurrencySymbol(acc.currencyCode)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = amountColor,
                    fontSize = 10.sp
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = accColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }

}