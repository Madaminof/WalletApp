package dev.samandar.walletapp.wallet.presentation.ui.features.shoppingLists.shoppingListDetail

import ShoppingListItem
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.samandar.walletapp.R
import dev.samandar.walletapp.core.AppStatusBarColor
import dev.samandar.walletapp.utils.EMPTY_SPACE
import dev.samandar.walletapp.wallet.domain.model.ShoppingItem
import dev.samandar.walletapp.wallet.presentation.ui.features.shoppingLists.dialogs.AddTransactionDialog
import dev.samandar.walletapp.wallet.presentation.ui.features.shoppingLists.dialogs.EditItemDialog
import dev.samandar.walletapp.wallet.presentation.ui.topbars.topbarScreen.CustomTopBar
import dev.samandar.walletapp.wallet.presentation.viewmodel.AddTransactionViewModel
import dev.samandar.walletapp.wallet.presentation.viewmodel.ShoppingViewModel
import kotlinx.coroutines.delay
import java.util.UUID

@Composable
fun ShoppingListDetailScreen(
    listId: String,
    navController: NavController,
    shoppingViewModel: ShoppingViewModel = hiltViewModel(),
    addTransactionViewModel: AddTransactionViewModel = hiltViewModel()
) {
    val uiState by shoppingViewModel.state.collectAsState()
    val shoppingItems = uiState.items


    val title by shoppingViewModel.listName.collectAsState()

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(500)
        try {
            focusRequester.requestFocus()
        } catch (e: Exception) {
            Log.e("FocusError", "Fokus berishda xato: ${e.message}")
        }
    }

    LaunchedEffect(listId) {
        shoppingViewModel.loadItems(listId)
    }

    var newItemName by remember { mutableStateOf("") }
    var newItemPrice by remember { mutableStateOf("") }

    var showAddTransactionDialog by remember { mutableStateOf(false) }
    var checkedItemsForTransaction by remember { mutableStateOf<List<ShoppingItem>>(emptyList()) }
    var itemToEdit by remember { mutableStateOf<ShoppingItem?>(null) }

    val checkedItems = shoppingItems.filter { it.isChecked }
    val isAnyItemSelected = checkedItems.isNotEmpty()
    val notePrefix = stringResource(R.string.shopping_list_note_prefix)

    val totalCheckedPrice = checkedItems.sumOf { it.price }

    AppStatusBarColor(MaterialTheme.colorScheme.primaryContainer)

    Scaffold(
        topBar = {
            CustomTopBar(
                title = title,
                onBackClick = { navController.popBackStack() },
            )
        },
        floatingActionButton = {
            if (isAnyItemSelected) {
                ExtendedFloatingActionButton(
                    onClick = {
                        checkedItemsForTransaction = checkedItems
                        val totalAmount = checkedItems.sumOf { it.price }
                        val note = checkedItems.joinToString(", ") { it.name }
                        val noteResult = notePrefix + String.EMPTY_SPACE + note
                        addTransactionViewModel.setupForShoppingList(
                            totalAmount = totalAmount,
                            note = noteResult
                        )
                        showAddTransactionDialog = true
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.padding(4.dp)
                        .navigationBarsPadding(),
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = stringResource(R.string.detail_screen_transaction_desc)) },
                    text = {
                        Text(
                            stringResource(R.string.detail_screen_transaction_button, checkedItems.size),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TopInputBar(
                newItemName = newItemName,
                onNameChange = { newItemName = it },
                newItemPrice = newItemPrice,
                onPriceChange = { newItemPrice = it },
                onAddItem = {
                    val price = newItemPrice.toDoubleOrNull() ?: 0.0
                    if (newItemName.isNotBlank()) {
                        val item = ShoppingItem(
                            id = UUID.randomUUID().toString(),
                            listId = listId,
                            name = newItemName.trim(),
                            price = price,
                            isChecked = false
                        )
                        shoppingViewModel.addItem(item)
                        newItemName = ""
                        newItemPrice = ""
                    }
                },
                focusRequester = focusRequester
            )

            Spacer(modifier = Modifier.height(8.dp))

            ShoppingSummaryBar(
                checkedCount = checkedItems.size,
                totalPrice = totalCheckedPrice
            )

            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                shoppingItems.isEmpty() -> {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            stringResource(R.string.detail_screen_empty_items),
                            color = MaterialTheme.colorScheme.onTertiary.copy(0.7f)
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                    ) {
                        items(shoppingItems, key = { it.id }) { item ->
                            ShoppingListItem(
                                item = item,
                                onToggleChecked = { id ->
                                    val currentItem = shoppingItems.find { it.id == id }
                                    currentItem?.let {
                                        shoppingViewModel.updateItem(
                                            it.copy(isChecked = !it.isChecked)
                                        )
                                    }
                                },
                                onEdit = { itemToEdit = it },
                                onDelete = { id -> shoppingViewModel.deleteItem(id) }
                            )
                        }
                        item { Spacer(Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }

    if (itemToEdit != null) {
        EditItemDialog(
            onDismiss = { itemToEdit = null },
            onUpdate = { updatedItem ->
                shoppingViewModel.updateItem(updatedItem)
                itemToEdit = null
            },
            item = itemToEdit!!,
        )
    }

    if (showAddTransactionDialog && checkedItemsForTransaction.isNotEmpty()) {
        AddTransactionDialog(
            viewModel = addTransactionViewModel,
            onDismiss = {
                if (addTransactionViewModel.uiState.saveSuccess) {
                    checkedItemsForTransaction.forEach { item ->
                        shoppingViewModel.updateItem(item.copy(isChecked = false))
                    }
                }
                addTransactionViewModel.clearState()
                showAddTransactionDialog = false
                checkedItemsForTransaction = emptyList()
            },
            navController = navController
        )
    }
}


