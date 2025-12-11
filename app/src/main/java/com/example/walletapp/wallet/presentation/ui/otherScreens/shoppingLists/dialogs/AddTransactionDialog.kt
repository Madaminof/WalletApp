package com.example.walletapp.wallet.presentation.ui.otherScreens.shoppingLists.dialogs

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencyManager
import com.example.walletapp.wallet.presentation.utils.getCurrencySymbol
import com.example.walletapp.wallet.presentation.viewmodel.AddTransactionViewModel

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
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp, bottomStart = 24.dp, bottomEnd = 24.dp)),
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
                    text = "Shopping",
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
                        value = uiState.selectedCategory?.name ?: "Select category",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category", color = MaterialTheme.colorScheme.onTertiary.copy(0.3f)) },
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
                                text = { Text(category.name, color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)) },
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
                        value = uiState.selectedAccount?.name ?: "Select account",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Account",color = MaterialTheme.colorScheme.onTertiary.copy(0.3f)) },
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
                                text = { Text(account.name, color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)) },
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
                    label = { Text("Amount (${getCurrencySymbol(activeCurrency)})",color = MaterialTheme.colorScheme.onTertiary.copy(0.3f)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    isError = (uiState.amountInput.toDoubleOrNull() ?: 0.0) <= 0.0 && uiState.amountInput.isNotEmpty(),
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = uiState.note,
                    onValueChange = viewModel::onNoteChange,
                    label = { Text("Note (Opsional)",color = MaterialTheme.colorScheme.onTertiary.copy(0.3f)) },
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
                        Text("Cancel", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onTertiary.copy(0.5f))
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
                            Text("Save", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}