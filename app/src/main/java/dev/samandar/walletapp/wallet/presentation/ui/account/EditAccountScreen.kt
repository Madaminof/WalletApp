package dev.samandar.walletapp.wallet.presentation.ui.account

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.domain.model.Account
import dev.samandar.walletapp.wallet.presentation.ui.account.addAccount.AccountInputFields
import dev.samandar.walletapp.wallet.presentation.ui.account.addAccount.ColorSelector
import dev.samandar.walletapp.wallet.presentation.ui.account.addAccount.IconSelector
import dev.samandar.walletapp.wallet.presentation.ui.account.addAccount.isLightColor
import dev.samandar.walletapp.wallet.presentation.ui.topbars.addTopbar.AddTopBar

@Composable
fun EditAccountScreen(
    account: Account,
    onSaveClick: (Account) -> Unit,
    navController: NavController
) {
    var name by remember { mutableStateOf(account.name) }
    var balanceText by remember { mutableStateOf(account.initialBalance.toString()) }
    val accounts = listOf("Cash","Card")

    val trimmedName = name.trim()
    val isNameBlank = trimmedName.isEmpty()

    val canSave = !isNameBlank
    val isSystemAccount = remember(account.name) {
        account.name == accounts[0] || account.name == accounts[1]
    }

    val initialColor = remember(account.colorHex) {
        try { Color(android.graphics.Color.parseColor(account.colorHex)) }
        catch (e: Exception) { Color(0xFF1976D2) }
    }

    val previewColor by animateColorAsState(targetValue = initialColor, label = "PreviewColorAnimation")
    val previewTextColor = if (isLightColor(previewColor)) Color.Black.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.9f)


    var selectedColor by remember { mutableStateOf(initialColor) }
    var selectedIcon by remember { mutableStateOf(account.iconResId ?: R.drawable.cash_ic1) }


    val handleSave: () -> Unit = {
        val hexColor = String.format("#%06X", (0xFFFFFF and selectedColor.toArgb()))
        val updatedAccount = account.copy(
            id = account.id,
            name = name,
            initialBalance = balanceText.toDoubleOrNull() ?: 0.0,
            colorHex = hexColor,
            iconResId = selectedIcon
        )
        onSaveClick(updatedAccount)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AddTopBar(
                navController = navController,
                canSave = canSave,
                onSave = handleSave,
                title = stringResource(R.string.title_edit_account)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            dev.samandar.walletapp.wallet.presentation.ui.account.addAccount.AccountPreviewCard(
                name = name,
                balanceText = balanceText,
                selectedIcon = selectedIcon,
                previewColor = selectedColor,
                previewTextColor = previewTextColor
            )

            AccountInputFields(
                name = name,
                onNameChange = {if (!isSystemAccount) name = it},
                isNameExists = false,
                balanceText = balanceText,
                onBalanceChange = { new ->
                    if (new.isEmpty() || (new.count { it == '.' } <= 1 && new.all { it.isDigit() || it == '.' })) {
                        balanceText = new
                    }
                }
            )

            ColorSelector(
                selectedColor = selectedColor,
                onColorSelected = { selectedColor = it }
            )

            IconSelector(
                selectedIcon = selectedIcon,
                onIconSelected = { selectedIcon = it }
            )
        }
    }
}
