package dev.samandar.walletapp.wallet.presentation.ui.account.addAccount

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.data.local.entity.account.AccountType
import dev.samandar.walletapp.wallet.domain.model.account.Account
import dev.samandar.walletapp.wallet.presentation.ui.account.addAccount.currency.AccountCurrencyBottomSheet
import dev.samandar.walletapp.wallet.presentation.ui.account.addAccount.currency.AddAccountCurrencyManager
import dev.samandar.walletapp.wallet.presentation.ui.topbars.addTopbar.AddTopBar
import java.util.UUID


fun isLightColor(color: Color): Boolean {
    val r = color.red
    val g = color.green
    val b = color.blue
    val luminance = (0.2126 * r + 0.7152 * g + 0.0722 * b)
    return luminance > 0.5
}


@Composable
fun AddAccountScreen(
    navController: NavController,
    existingAccounts: List<Account>,
    accountType: String?,
    onSave: (Account) -> Unit,
) {
    val focusManager = LocalFocusManager.current

    val selectedCurrency by AddAccountCurrencyManager.localCurrency
    var showCurrencySheet by remember { mutableStateOf(false) }

    var selectedType by remember {
        mutableStateOf(if (accountType == "CASH") AccountType.CASH else AccountType.CARD)
    }

    var name by remember { mutableStateOf("") }
    var balanceText by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(extendedColors.first()) }
    var selectedIcon by remember { mutableStateOf(extendedIcons.first()) }
    var showTypeSheet by remember { mutableStateOf(false) }

    val canSave = name.trim().isNotEmpty()

    // 2. BottomSheet mantiqi
    if (showCurrencySheet) {
        if (showCurrencySheet) {
            AccountCurrencyBottomSheet(onDismiss = { showCurrencySheet = false })
        }
    }
    if (showTypeSheet) {
        SelectAccountTypeBottomSheet(
            selectedType = selectedType, // Hozirgi tanlangan tur
            onTypeSelected = { newType ->
                selectedType = newType      // 1. Yangi turni saqlaymiz
                showTypeSheet = false       // 2. Sheetni yopamiz 👈 SHU JOYI ETISHMAYOTGAN EDI
            },
            onDismiss = { showTypeSheet = false }
        )
    }

    val handleSave: () -> Unit = {
        focusManager.clearFocus()
        val balance = balanceText.toDoubleOrNull() ?: 0.0
        val colorHex = "#" + Integer.toHexString(selectedColor.toArgb()).uppercase().substring(2)

        val account = when (selectedType) {
            AccountType.CARD -> Account.Card(
                id = UUID.randomUUID().toString(),
                name = name.trim(),
                balance = balance, // Masalan: 100.0 (USD bo'lsa)
                amountCurrencyKonverter = balance, // 👈 Bu ham 100.0 (USD) bo'ladi
                currencyCode = selectedCurrency,
                colorHex = colorHex,
                iconResId = selectedIcon,
                cardNumber = cardNumber.ifBlank { null },
                cardProvider = detectCardProvider(cardNumber),
                isDefault = false
            )

            AccountType.CASH -> Account.Cash(
                id = UUID.randomUUID().toString(),
                name = name.trim(),
                balance = balance,
                amountCurrencyKonverter = balance, // 👈 Bu ham balance bilan bir xil
                currencyCode = selectedCurrency,
                colorHex = colorHex,
                iconResId = selectedIcon,
                isDefault = false
            )
        }
        onSave(account)
        navController.popBackStack()
    }

    Scaffold(
        topBar = {
            AddTopBar(
                navController = navController,
                canSave = canSave,
                onSave = handleSave,
                title = if (selectedType == AccountType.CARD) stringResource(Strings.add_card) else stringResource(
                    Strings.add_cash
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            TypeSelectorRow(
                selectedType = selectedType,
                selectedIconRes = selectedIcon,
                activeColor = selectedColor,
                onClick = { showTypeSheet = true }
            )

            AppleTextField(
                value = name,
                onValueChange = { name = it },
                label = stringResource(Strings.participant_name),
                placeholder = if (selectedType == AccountType.CARD) stringResource(Strings.card_name) else stringResource(
                    Strings.wallet_name
                )
            )

            if (selectedType == AccountType.CARD) {
                AppleTextField(
                    value = cardNumber,
                    onValueChange = { if (it.length <= 16) cardNumber = it },
                    label = stringResource(Strings.card_number),
                    placeholder = "0000 0000 0000 0000",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            // 3. Lokal valyuta uzatilyapti
            BalanceAndCurrencyRow(
                balanceText = balanceText,
                onBalanceChange = { balanceText = it },
                selectedCurrency = selectedCurrency,
                onCurrencyClick = { showCurrencySheet = true }
            )

            ColorSelector(selectedColor = selectedColor, onColorSelected = { selectedColor = it })

            IconSelector(
                selectedIcon = selectedIcon,
                onIconSelected = { selectedIcon = it },
                selectedColor = selectedColor
            )
        }
    }
}

@Composable
fun TypeSelectorRow(
    selectedType: AccountType,
    onClick: () -> Unit,
    activeColor: Color,
    selectedIconRes: Int,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(Strings.account_type),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            ),
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )

        Surface(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.onTertiary.copy(0.03f),
            border = null
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        // Endi bu yerda standart ikonka emas, tanlangan ikonka chiqadi
                        painter = painterResource(id = selectedIconRes),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = activeColor
                    )

                    Spacer(Modifier.width(12.dp))

                    Text(
                        text = if (selectedType == AccountType.CARD) stringResource(Strings.card) else stringResource(
                            Strings.cash
                        ),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                            fontSize = 15.sp
                        )
                    )
                }
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.Gray.copy(alpha = 0.4f)
                )
            }
        }
    }
}


@Composable
fun BalanceAndCurrencyRow(
    balanceText: String,
    onBalanceChange: (String) -> Unit,
    selectedCurrency: String,
    onCurrencyClick: () -> Unit,
    enabled: Boolean = true,
) {
    val focusRequester = remember { FocusRequester() }
    val alpha = if (enabled) 1f else 0.6f

    Column(modifier = Modifier
        .fillMaxWidth()
        .alpha(alpha)) {
        Text(
            text = stringResource(Strings.balance_and_currency),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            ),
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Surface(
                onClick = { focusRequester.requestFocus() },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                enabled = enabled,
                shape = RoundedCornerShape(
                    topStart = 14.dp,
                    bottomStart = 14.dp,
                    topEnd = 0.dp,
                    bottomEnd = 0.dp
                ),
                color = MaterialTheme.colorScheme.onTertiary.copy(0.03f)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    BasicTextField(
                        value = balanceText,
                        readOnly = !enabled, // 👈 Klaviatura chiqmaydi
                        enabled = enabled,
                        onValueChange = onBalanceChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        textStyle = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                            fontSize = 18.sp
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            Box(contentAlignment = Alignment.CenterStart) {
                                if (balanceText.isEmpty()) {
                                    Text(
                                        text = "0.00",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.Gray.copy(alpha = 0.3f),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }
            }
            Surface(
                onClick = onCurrencyClick,
                enabled = enabled, // 👈 Bosishni butunlay o'chiradi
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    bottomStart = 0.dp,
                    topEnd = 14.dp,
                    bottomEnd = 14.dp
                ),
                color = MaterialTheme.colorScheme.onTertiary.copy(0.03f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    val flagEmoji = when (selectedCurrency) {
                        "UZS" -> "🇺🇿"
                        "USD" -> "🇺🇸"
                        "EUR" -> "🇪🇺"
                        "RUB" -> "🇷🇺"
                        else -> "🏳️"
                    }

                    Text(text = flagEmoji, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = selectedCurrency,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                            fontSize = 14.sp
                        )
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color.Gray.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

fun detectCardProvider(number: String): String? {
    return when {
        number.startsWith("8600") -> "UZCARD"
        number.startsWith("9860") -> "HUMO"
        number.startsWith("4") -> "VISA"
        number.startsWith("5") -> "MASTERCARD"
        else -> null
    }
}

