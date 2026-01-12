package dev.samandar.walletapp.wallet.presentation.ui.premiumFunctions.shoppingLists.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource // stringResource uchun import
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import dev.samandar.walletapp.R // R.string resurslaringiz importi
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.categories.getTranslatedName
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencyManager
import dev.samandar.walletapp.wallet.presentation.utils.getCurrencySymbol
import dev.samandar.walletapp.wallet.presentation.viewmodel.AddTransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    viewModel: AddTransactionViewModel,
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

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Divider(
                        modifier = Modifier.width(40.dp).clip(CircleShape),
                        thickness = 4.dp,
                        color = MaterialTheme.colorScheme.onTertiary.copy(0.2f)
                    )
                }
                Text(
                    text = stringResource(R.string.dialog_add_transaction_title), // "Shopping" -> dialog_add_transaction_title
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 12.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                )
                ExposedDropdownMenuBox(
                    expanded = expandedCategory,
                    onExpandedChange = { expandedCategory = !expandedCategory },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    OutlinedTextField(
                        value = categoryName.toString(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.dialog_label_category), color = MaterialTheme.colorScheme.onTertiary.copy(0.3f)) }, // "Category" -> dialog_label_category
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                            unfocusedTextColor = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = { expandedCategory = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.onPrimaryContainer)
                    ) {
                        uiState.expenseCategories.forEach { category ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        getTranslatedName(category.name).toString(),
                                        color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                                    )
                                       },
                                onClick = {
                                    viewModel.onCategorySelect(category)
                                    expandedCategory = false
                                },
                                leadingIcon = if (uiState.selectedCategory?.id == category.id) {
                                    { Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                                } else null
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                ExposedDropdownMenuBox(
                    expanded = expandedAccount,
                    onExpandedChange = { expandedAccount = !expandedAccount },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = accountName.toString(),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.dialog_label_account),color = MaterialTheme.colorScheme.onTertiary.copy(0.3f)) }, // "Account" -> dialog_label_account
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAccount) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                            unfocusedTextColor = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandedAccount,
                        onDismissRequest = { expandedAccount = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.onPrimaryContainer)
                    ) {
                        uiState.accounts.forEach { account ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        getTranslatedName(account.name).toString(),
                                        color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                                    )
                                       },
                                onClick = {
                                    viewModel.onAccountSelect(account)
                                    expandedAccount = false
                                },
                                leadingIcon = if (uiState.selectedAccount?.id == account.id) {
                                    { Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                                } else null
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = uiState.amountInput,
                    onValueChange = viewModel::onAmountChange,
                    label = { Text(stringResource(R.string.dialog_label_amount_currency, getCurrencySymbol(activeCurrency)),color = MaterialTheme.colorScheme.onTertiary.copy(0.3f)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    isError = (uiState.amountInput.toDoubleOrNull() ?: 0.0) <= 0.0 && uiState.amountInput.isNotEmpty(),
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = uiState.note,
                    onValueChange = viewModel::onNoteChange,
                    label = { Text(stringResource(R.string.dialog_label_note_optional),color = MaterialTheme.colorScheme.onTertiary.copy(0.3f)) }, // "Note (Opsional)" -> dialog_label_note_optional
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                if (uiState.errorMessage != null) {
                    Text(
                        text = uiState.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.dialog_button_cancel), fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onTertiary.copy(0.5f)) // "Cancel" -> dialog_button_cancel
                    }
                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = {
                            viewModel.saveTransaction()
                            onDismiss()
                        },
                        enabled = isFormValid && !uiState.isSaving,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f).height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(
                                Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Text(stringResource(R.string.dialog_button_save_transaction), fontWeight = FontWeight.Bold) // "Save" -> dialog_button_save_transaction
                        }
                    }
                }
            }
        }
    }
}