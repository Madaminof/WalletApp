package dev.samandar.walletapp.wallet.presentation.ui.account.addAccount

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.domain.model.Account
import dev.samandar.walletapp.wallet.presentation.ui.topbars.addTopbar.AddTopBar
import kotlinx.coroutines.launch
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
                selectedColor = selectedColor,
                onColorSelected = { selectedColor = it }
            )
            IconSelector(
                selectedIcon = selectedIcon,
                onIconSelected = { selectedIcon = it }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

