package dev.samandar.walletapp.wallet.presentation.ui.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.data.local.entity.account.AccountType
import dev.samandar.walletapp.wallet.domain.model.account.Account
import dev.samandar.walletapp.wallet.presentation.ui.account.addAccount.AppleTextField
import dev.samandar.walletapp.wallet.presentation.ui.account.addAccount.BalanceAndCurrencyRow
import dev.samandar.walletapp.wallet.presentation.ui.account.addAccount.ColorSelector
import dev.samandar.walletapp.wallet.presentation.ui.account.addAccount.IconSelector
import dev.samandar.walletapp.wallet.presentation.ui.account.addAccount.TypeSelectorRow
import dev.samandar.walletapp.wallet.presentation.ui.account.addAccount.detectCardProvider
import dev.samandar.walletapp.wallet.presentation.ui.topbars.addTopbar.AddTopBar
import java.text.DecimalFormat

@Composable
fun EditAccountScreen(
    account: Account,
    onSaveClick: (Account) -> Unit,
    navController: NavController,
) {
    val focusManager = LocalFocusManager.current

    val isDefaultAccount = account.id == "default_cash" || account.id == "default_card"
    val isCard = account is Account.Card

    val formattedBalance = remember(account.amountCurrencyKonverter) {
        DecimalFormat("#,###.##").format(account.amountCurrencyKonverter).replace(",", " ")
    }

    var name by remember { mutableStateOf(account.name) }
    var cardNumber by remember {
        mutableStateOf(if (account is Account.Card) account.cardNumber ?: "" else "")
    }

    val initialColor = remember(account.colorHex) {
        try {
            Color(android.graphics.Color.parseColor(account.colorHex))
        } catch (e: Exception) {
            Color(0xFF1976D2)
        }
    }
    var selectedColor by remember { mutableStateOf(initialColor) }
    var selectedIcon by remember { mutableStateOf(account.iconResId ?: R.drawable.cash_ic1) }

    val canSave = name.trim().isNotEmpty()

    val handleSave = {
        focusManager.clearFocus()
        val hexColor = String.format("#%06X", (0xFFFFFF and selectedColor.toArgb()))

        val updatedAccount = when (account) {
            is Account.Cash -> account.copy(
                name = if (isDefaultAccount) account.name else name.trim(),
                colorHex = hexColor,
                iconResId = selectedIcon
            )

            is Account.Card -> account.copy(
                name = if (isDefaultAccount) account.name else name.trim(),
                cardNumber = if (isDefaultAccount) account.cardNumber else cardNumber.ifBlank { null },
                cardProvider = detectCardProvider(cardNumber),
                colorHex = hexColor,
                iconResId = selectedIcon
            )
        }
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
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TypeSelectorRow(
                    selectedType = if (isCard) AccountType.CARD else AccountType.CASH,
                    selectedIconRes = selectedIcon,
                    activeColor = selectedColor,
                    onClick = {} // Disabled
                )

                BalanceAndCurrencyRow(
                    balanceText = formattedBalance,
                    onBalanceChange = {},
                    selectedCurrency = account.currencyCode,
                    onCurrencyClick = {},
                    enabled = false
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                AppleTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = stringResource(R.string.account_name),
                    placeholder = stringResource(R.string.account_name),
                    enabled = !isDefaultAccount
                )

                // Karta raqami (Default bo'lsa ReadOnly)
                if (isCard) {
                    AppleTextField(
                        value = cardNumber,
                        onValueChange = { if (it.length <= 16) cardNumber = it },
                        label = stringResource(R.string.card_number),
                        placeholder = "0000 0000 0000 0000",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        enabled = !isDefaultAccount
                    )
                }
            }

            // 3. DESIGN SECTION (Hamma accountlar uchun ochiq)
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ColorSelector(
                    selectedColor = selectedColor,
                    onColorSelected = { selectedColor = it }
                )

                IconSelector(
                    selectedIcon = selectedIcon,
                    onIconSelected = { selectedIcon = it },
                    selectedColor = selectedColor
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}