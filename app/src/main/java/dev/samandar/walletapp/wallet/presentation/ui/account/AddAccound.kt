package dev.samandar.walletapp.wallet.presentation.ui.account

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.domain.model.Account
import dev.samandar.walletapp.wallet.presentation.ui.topbars.addTopbar.AddTopBar
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import java.util.UUID

private val extendedColors = listOf(
    Color(0xFF1976D2), Color(0xFF0F9915), Color(0xFFFF9800), Color(0xFFE91E63), Color(0xFF9C27B0),
    Color(0xFF795548), Color(0xFF009688), Color(0xFFB0BEC5), Color(0xFFFDD835), Color(0xFFC62828),
    Color(0xFF37474F), Color(0xFF3F51B5), Color(0xFFFF5722)
)
private val extendedIcons = listOf(
    R.drawable.cash_ic,
    R.drawable.cash_default_ic,
    R.drawable.cash_ic2,
    R.drawable.cash_ic3,
    R.drawable.cash_ic4,
    R.drawable.cash_ic5,
    R.drawable.cash_ic6,
    R.drawable.cash_ic8,
    R.drawable.cash_ic9,
    R.drawable.cash_ic10,
    R.drawable.cash_ic11,
    R.drawable.cash_ic12,
    )

private fun isLightColor(color: Color): Boolean {
    val r = color.red
    val g = color.green
    val b = color.blue
    val luminance = (0.2126 * r + 0.7152 * g + 0.0722 * b)
    return luminance > 0.5
}

@Composable
private fun formatBalance(balanceText: String): String {
    val balance = balanceText.toDoubleOrNull() ?: 0.0
    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance(Locale("uz", "UZ")).apply {
            minimumFractionDigits = if (balanceText.contains('.')) 2 else 0
            maximumFractionDigits = 2
        }
    }
    return currencyFormatter.format(balance)
}


@Composable
fun AddAccountScreen(
    navController: NavController,
    existingAccounts: List<Account>,
    onSave: (Account) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var balanceText by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(extendedColors.first()) }
    var selectedIcon by remember { mutableStateOf(extendedIcons.first()) }

    val trimmedName = name.trim()
    val isNameBlank = trimmedName.isEmpty()
    val isNameExists = existingAccounts.any { it.name.equals(trimmedName, ignoreCase = true) && trimmedName.isNotEmpty() }

    val canSave = !isNameBlank && !isNameExists

    val previewColor by animateColorAsState(targetValue = selectedColor, label = "PreviewColorAnimation")
    val previewTextColor = if (isLightColor(previewColor)) Color.Black.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.9f)

    val handleSave: () -> Unit = {
        focusManager.clearFocus()
        if (!canSave) {
            scope.launch {
                val message = when {
                    isNameBlank -> context.getString(R.string.add_account_error_name_blank)
                    isNameExists -> context.getString(R.string.add_account_error_name_exists)
                    else -> context.getString(R.string.add_account_error_name_blank)
                }

                snackbarHostState.showSnackbar(message = message)
            }
        } else {
            val balance = balanceText.toDoubleOrNull() ?: 0.0
            val colorHex = "#" + Integer.toHexString(previewColor.toArgb()).uppercase().substring(2)

            val account = Account(
                id = UUID.randomUUID().toString(),
                name = trimmedName,
                initialBalance = balance,
                colorHex = colorHex,
                iconResId = selectedIcon,
            )
            onSave(account)
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            AddTopBar(
                navController = navController,
                canSave = canSave,
                onSave = handleSave,
                title = stringResource(R.string.add_account_title)
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    Snackbar(
                        snackbarData = data,
                        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiary,
                        actionColor = MaterialTheme.colorScheme.primary,
                        dismissActionContentColor = MaterialTheme.colorScheme.primary
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            AccountPreviewCard(
                name = trimmedName,
                balanceText = balanceText,
                selectedIcon = selectedIcon,
                previewColor = previewColor,
                previewTextColor = previewTextColor
            )
            AccountInputFields(
                name = name,
                onNameChange = { new ->
                    val filtered = new.filter { it.isLetterOrDigit() || it.isWhitespace() }
                    if (filtered.length <= 30) name = filtered
                },
                isNameExists = isNameExists,
                balanceText = balanceText,
                onBalanceChange = { new ->
                    if (new.isEmpty() || (new.count { it == '.' } <= 1 && new.all { it.isDigit() || it == '.' })) {
                        balanceText = new
                    }
                }
            )
            Divider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant)

            ColorSelector(
                availableColors = extendedColors,
                selectedColor = selectedColor,
                onColorSelected = { selectedColor = it }
            )
            IconSelector(
                availableIcons = extendedIcons,
                selectedIcon = selectedIcon,
                onIconSelected = { selectedIcon = it }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun AccountPreviewCard(
    name: String,
    balanceText: String,
    selectedIcon: Int,
    previewColor: Color,
    previewTextColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = previewColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onTertiary.copy(0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = selectedIcon),
                        contentDescription = stringResource(R.string.add_account_default_name),
                        tint = Color.Unspecified,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = if (name.isNotEmpty()) name else stringResource(R.string.add_account_default_name),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = previewTextColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Column(horizontalAlignment = Alignment.Start) {
                Text(
                    text = stringResource(R.string.add_account_current_balance),
                    style = MaterialTheme.typography.labelMedium,
                    color = previewTextColor.copy(alpha = 0.7f)
                )
                Text(
                    text = formatBalance(balanceText),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = previewTextColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
@Composable
fun AccountInputFields(
    name: String,
    onNameChange: (String) -> Unit,
    isNameExists: Boolean,
    balanceText: String,
    onBalanceChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        TextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text(stringResource(R.string.add_account_name_input_label)) },
            isError = isNameExists,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                focusedContainerColor = MaterialTheme.colorScheme.onBackground,
                unfocusedContainerColor = MaterialTheme.colorScheme.onBackground,
                errorContainerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(12.dp),
            supportingText = {
                if (isNameExists && name.isNotBlank()) {
                    Text(stringResource(R.string.add_account_error_name_exists), color = MaterialTheme.colorScheme.error)
                } else {
                    Text(stringResource(R.string.add_account_name_placeholder), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
        TextField(
            value = NumFormatter(balanceText, includeFraction = true),
            onValueChange = { newFormattedValue ->

                val newCleanValue = newFormattedValue.replace("\\s".toRegex(), "") // Bo'shliqlarni olib tashlash
                    .replace(",", "")
                    .filter { it.isDigit() || it == '.' }
                val dotCount = newCleanValue.count { it == '.' }

                if (dotCount <= 1) {
                    onBalanceChange(newCleanValue)
                }
            },
            label = { Text(stringResource(R.string.add_account_balance_input_label)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedContainerColor = MaterialTheme.colorScheme.onBackground,
                unfocusedContainerColor = MaterialTheme.colorScheme.onBackground,
            ),
            shape = RoundedCornerShape(12.dp),
            supportingText = {
                Text(stringResource(R.string.add_account_balance_placeholder), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        )
    }
}
@Composable
fun ColorSelector(
    availableColors: List<Color>,
    selectedColor: Color,
    onColorSelected: (Color) -> Unit
) {
    Text(stringResource(R.string.add_account_select_color), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onTertiary)
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(vertical = 8.dp)) {
        items(availableColors) { c ->
            val isSelected = c == selectedColor
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(c)
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        shape = CircleShape
                    )
                    .clickable { onColorSelected(c) }
            )
        }
    }
}
@Composable
fun IconSelector(
    availableIcons: List<Int>,
    selectedIcon: Int,
    onIconSelected: (Int) -> Unit
) {
    Text(stringResource(R.string.add_account_select_icon), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onTertiary)
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(vertical = 8.dp)) {
        items(availableIcons) { iconRes ->
            val selected = iconRes == selectedIcon

            val containerColor by animateColorAsState(
                targetValue = if (selected) MaterialTheme.colorScheme.primary.copy(0.1f) else MaterialTheme.colorScheme.onTertiary.copy(0.03f),
                label = "IconContainerColorAnimation"
            )

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(containerColor)
                    .clickable { onIconSelected(iconRes) }
                    .border(
                        width = if (selected) 2.dp else 1.dp,
                        color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = stringResource(R.string.add_account_select_icon),
                    tint = Color.Unspecified,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}