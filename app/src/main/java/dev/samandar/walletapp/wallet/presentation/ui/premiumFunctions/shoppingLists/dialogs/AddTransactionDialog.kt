package dev.samandar.walletapp.wallet.presentation.ui.premiumFunctions.shoppingLists.dialogs

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.graphics.toColorInt
import androidx.navigation.NavController
import dev.samandar.walletapp.R
import dev.samandar.walletapp.navigation.Screen
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.categories.getTranslatedName
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencyManager
import dev.samandar.walletapp.wallet.presentation.utils.getCurrencySymbol
import dev.samandar.walletapp.wallet.presentation.viewmodel.AddTransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    viewModel: AddTransactionViewModel,
    navController: NavController,
    onDismiss: () -> Unit
) {
    val activeCurrency by CurrencyManager.currentCurrency
    val uiState = viewModel.uiState

    var expandedCategory by remember { mutableStateOf(false) }
    var expandedAccount by remember { mutableStateOf(false) }

    val categoryName = getTranslatedName(uiState.selectedCategory?.name ?: stringResource(R.string.dialog_label_select_category))
    val accountName = getTranslatedName(uiState.selectedAccount?.name ?: stringResource(R.string.dialog_label_select_account))

    val isFormValid by remember {
        derivedStateOf {
            uiState.selectedCategory != null &&
                    uiState.selectedAccount != null &&
                    (uiState.amountInput.toDoubleOrNull() ?: 0.0) > 0.0
        }
    }


    // Master Animation System
    var isAnimate by remember { mutableStateOf(false) }
    val animProgress by animateFloatAsState(
        targetValue = if (isAnimate) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ), label = "TransactionAnim"
    )

    LaunchedEffect(Unit) { isAnimate = true }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onDismiss()
            navController.navigate(Screen.ShoppingLists.route)
            viewModel.resetSuccessState()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = true,
            decorFitsSystemWindows = true
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .graphicsLayer {
                    scaleX = 0.95f + (0.05f * animProgress)
                    scaleY = 0.95f + (0.05f * animProgress)
                    alpha = animProgress
                    transformOrigin = TransformOrigin(0.9f, 0.9f)
                },
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            tonalElevation = 0.dp,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Drag Handle
                Box(
                    modifier = Modifier
                        .size(36.dp, 4.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(0.3f))
                )

                Text(
                    text = stringResource(R.string.dialog_add_transaction_title),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onTertiary.copy(0.9f),
                        letterSpacing = (-1).sp
                    ),
                    modifier = Modifier.padding(top = 20.dp, bottom = 20.dp)
                )

                // 1. Category Selection
                ExposedDropdownMenuBox(
                    expanded = expandedCategory,
                    onExpandedChange = { expandedCategory = !expandedCategory },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = categoryName.toString(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.dialog_label_category), color = Color.Gray.copy(0.4f)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.onTertiary.copy(0.04f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.onTertiary.copy(0.04f),
                            focusedBorderColor = MaterialTheme.colorScheme.primary.copy(0.4f),
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                            unfocusedTextColor = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = { expandedCategory = false },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.onPrimaryContainer)
                            .width(IntrinsicSize.Max)
                    ) {
                        uiState.expenseCategories.forEach { category ->
                            val isSelected = uiState.selectedCategory?.id == category.id

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = getTranslatedName(category.name).toString(),
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected)
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                                        )
                                    )
                                },
                                onClick = {
                                    viewModel.onCategorySelect(category)
                                    expandedCategory = false
                                },
                                leadingIcon = {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(Color(category.colorArgb).copy(0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(category.iconResId?:R.drawable.ic_add),
                                            contentDescription = null,
                                            tint = Color.Unspecified,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected)
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                        else
                                            Color.Transparent
                                    ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Account Selection
                ExposedDropdownMenuBox(
                    expanded = expandedAccount,
                    onExpandedChange = { expandedAccount = !expandedAccount },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = accountName.toString(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.dialog_label_account), color = Color.Gray.copy(0.4f)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAccount) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.onTertiary.copy(0.04f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.onTertiary.copy(0.04f),
                            focusedBorderColor = MaterialTheme.colorScheme.primary.copy(0.4f),
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                            unfocusedTextColor = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedAccount,
                        onDismissRequest = { expandedAccount = false },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.onPrimaryContainer)
                            .width(IntrinsicSize.Max)
                    ) {
                        uiState.accounts.forEach { account ->
                            val isSelected = uiState.selectedAccount?.id == account.id

                            val accColor = remember(account.colorHex) {
                                try { Color(account.colorHex?.toColorInt() ?: 0xFF6200EE.toInt()) }
                                catch (e: Exception) { Color(0xFF6200EE.toInt()) }
                            }

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = getTranslatedName(account.name).toString(),
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected)
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                                        )
                                    )
                                },
                                onClick = {
                                    viewModel.onAccountSelect(account)
                                    expandedAccount = false
                                },
                                leadingIcon = {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(accColor.copy(0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(account.iconResId?:R.drawable.ic_add),
                                            contentDescription = null,
                                            tint = Color.Unspecified,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected)
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                        else
                                            Color.Transparent
                                    ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 3. Amount Input
                OutlinedTextField(
                    value = uiState.amountInput,
                    onValueChange = viewModel::onAmountChange,
                    label = { Text(stringResource(R.string.dialog_label_amount_currency, getCurrencySymbol(activeCurrency)), color = Color.Gray.copy(0.4f)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.onTertiary.copy(0.04f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.onTertiary.copy(0.04f),
                        focusedBorderColor = MaterialTheme.colorScheme.primary.copy(0.4f),
                        unfocusedBorderColor = Color.Transparent
                    ),
                    isError = (uiState.amountInput.toDoubleOrNull() ?: 0.0) <= 0.0 && uiState.amountInput.isNotEmpty()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 4. Note Input
                OutlinedTextField(
                    value = uiState.note,
                    onValueChange = viewModel::onNoteChange,
                    label = { Text(stringResource(R.string.dialog_label_note_optional), color = Color.Gray.copy(0.4f)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.onTertiary.copy(0.04f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.onTertiary.copy(0.04f),
                        focusedBorderColor = MaterialTheme.colorScheme.primary.copy(0.4f),
                        unfocusedBorderColor = Color.Transparent
                    )
                )

                if (uiState.errorMessage != null) {
                    Text(
                        text = uiState.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            stringResource(R.string.dialog_button_cancel),
                            color = Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.saveTransaction()
                        },
                        enabled = isFormValid && !uiState.isSaving,
                        modifier = Modifier.weight(1.5f).height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(0.1f)
                        )
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text(stringResource(R.string.dialog_button_save_transaction), fontWeight = FontWeight.Black, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}