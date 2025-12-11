package com.example.walletapp.wallet.presentation.ui.otherScreens.debts

import AddEditDebtDialog
import DebtItemRow
import DebtsSummaryCard
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.walletapp.wallet.domain.model.Debt
import com.example.walletapp.wallet.presentation.ui.charts.expenseListComponents.DeleteConfirmationDialog
import com.example.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencyManager
import com.example.walletapp.wallet.presentation.ui.otherScreens.topbar.CustomTopBar
import com.example.walletapp.wallet.presentation.viewmodel.DebtsViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtsScreen(
    navController: NavController,
    viewModel: DebtsViewModel = hiltViewModel()
) {

    val uiState by viewModel.state.collectAsState()

    var debtToEdit by remember { mutableStateOf<Debt?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteDialogId by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var selectedDebtForDetails by remember { mutableStateOf<Debt?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(showAddDialog, uiState.accounts) {
        if (showAddDialog && uiState.selectedAccount == null && uiState.accounts.isNotEmpty()) {
            viewModel.onAccountSelect(uiState.accounts.first())
        }
    }

    if (uiState.errorMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("Xatolik Yuz Berdi") },
            text = { Text(uiState.errorMessage ?: "Noma'lum xato.") },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) { Text("OK") }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { CustomTopBar(title = "Debts", onBackClick = { navController.popBackStack() }, navController = navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    debtToEdit = Debt(
                        id = "",
                        person = "",
                        amount = 0.0,
                        isLent = true,
                        date = System.currentTimeMillis()
                    )
                    viewModel.onDateSelect(System.currentTimeMillis())
                    showAddDialog = true
                },
                modifier = Modifier.size(60.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Yangi Qarz Qo'shish",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {

            DebtsSummaryCard(
                totalLent = uiState.totalLent,
                totalOwed = uiState.totalOwed
            )

            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp))
            } else if (uiState.debts.isEmpty()) {
                EmptyDebtsState()
            } else {
                val outstandingDebts = uiState.debts.filter { !it.isSettled }
                val settledDebts = uiState.debts.filter { it.isSettled }.sortedByDescending { it.date }

                LazyColumn(
                    contentPadding = PaddingValues(top = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    if (outstandingDebts.isNotEmpty()) {
                        items(outstandingDebts, key = { it.id }) { debt ->
                            DebtItemRow(
                                debt = debt,
                                onToggleSettled = viewModel::toggleSettled,
                                onEdit = {
                                    debtToEdit = it.copy(id = it.id)
                                    viewModel.onDateSelect(it.date)
                                    showAddDialog = true
                                },
                                onDelete = { showDeleteDialogId = it.id },
                                onClick = {
                                    selectedDebtForDetails = it
                                    scope.launch { sheetState.show() }
                                }
                            )
                        }
                    }
                    if (settledDebts.isNotEmpty()) {

                        item {
                            Spacer(Modifier.height(8.dp))
                            Divider(color = MaterialTheme.colorScheme.onTertiary.copy(0.1f), thickness = 1.dp)
                            Spacer(Modifier.height(8.dp))

                        }

                        items(settledDebts, key = { it.id }) { debt ->
                            DebtItemRow(
                                debt = debt,
                                onToggleSettled = viewModel::toggleSettled,
                                onEdit = {
                                    debtToEdit = it.copy(id = it.id)
                                    viewModel.onDateSelect(it.date)
                                    showAddDialog = true
                                },
                                onDelete = { showDeleteDialogId = it.id },
                                onClick = {
                                    selectedDebtForDetails = it
                                    scope.launch { sheetState.show() }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
    if (selectedDebtForDetails != null) {
        DebtDetailBottomSheet(
            debt = selectedDebtForDetails!!,
            sheetState = sheetState,
            onDismiss = {
                scope.launch {
                    sheetState.hide()
                    selectedDebtForDetails = null
                }
            },
            onEdit = {
                debtToEdit = it.copy(id = it.id)
                viewModel.onDateSelect(it.date)
                showAddDialog = true
            },
            onDelete = { showDeleteDialogId = it.id },

        )
    }

    if (showAddDialog) {
        AddEditDebtDialog(
            initialDebt = debtToEdit,
            accounts = uiState.accounts,
            selectedAccount = uiState.selectedAccount,
            selectedDate = uiState.selectedDate,
            onAccountSelect = viewModel::onAccountSelect,
            onDateSelect = viewModel::onDateSelect,
            onDismiss = { showAddDialog = false; debtToEdit = null },
            onConfirm = { person, amount, isLent ->
                val debt = (debtToEdit ?: Debt(id = "", person = "", amount = 0.0, isLent = isLent, date = uiState.selectedDate)).copy(
                    person = person,
                    amount = amount,
                    isLent = isLent
                )
                viewModel.addUpdateDebt(debt)
                val message =
                    if (debtToEdit != null) "Qarz muvaffaqiyatli tahrirlandi."
                    else "Yangi qarz muvaffaqiyatli qo'shildi."
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = message,
                        actionLabel = "OK",
                        duration = SnackbarDuration.Short
                    )
                }

                showAddDialog = false
                debtToEdit = null
            }
        )
    }

    if (showDeleteDialogId != null) {
        DeleteConfirmationDialog(
            onDismiss = { showDeleteDialogId = null },
            onConfirmDelete = {
                viewModel.deleteDebt(showDeleteDialogId!!)
                showDeleteDialogId = null
            },
            title = "Qarzni O'chirish",
            text = "Rostdan ham ushbu qarz ma'lumotini o'chirishni xohlaysizmi?"
        )
    }
}

