package com.example.walletapp.wallet.presentation.ui.otherScreens.shoppingLists

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.walletapp.core.AppStatusBarColor
import com.example.walletapp.wallet.domain.model.ShoppingItem
import com.example.walletapp.wallet.presentation.ui.charts.expenseListComponents.DeleteConfirmationDialog
import com.example.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencyManager
import com.example.walletapp.wallet.presentation.ui.otherScreens.shoppingLists.dialogs.AddTransactionDialog
import com.example.walletapp.wallet.presentation.ui.otherScreens.shoppingLists.dialogs.EditItemDialog
import com.example.walletapp.wallet.presentation.ui.otherScreens.topbar.CustomTopBar
import com.example.walletapp.wallet.presentation.utils.formatAmountWithCurrency
import com.example.walletapp.wallet.presentation.viewmodel.AddTransactionViewModel
import com.example.walletapp.wallet.presentation.viewmodel.ShoppingViewModel
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

    AppStatusBarColor(MaterialTheme.colorScheme.primaryContainer)

    Scaffold(
        topBar = {
            CustomTopBar(
                navController = navController,
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

                        addTransactionViewModel.setupForShoppingList(
                            totalAmount = totalAmount,
                            note = "Xarid ro'yxati: $note"
                        )
                        showAddTransactionDialog = true
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.padding(16.dp),
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Tranzaksiya") },
                    text = {
                        Text(
                            "Xarid qilish (${checkedItems.size})",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        contentWindowInsets = WindowInsets(0.dp)
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
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                shoppingItems.isEmpty() -> {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Ro'yxat bo'sh. Element qo'shing.", color = MaterialTheme.colorScheme.onTertiary.copy(0.7f))
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
            item = itemToEdit!!,
            onDismiss = { itemToEdit = null },
            onUpdate = { updatedItem ->
                shoppingViewModel.updateItem(updatedItem)
                itemToEdit = null
            }
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
            }
        )
    }
}

@Composable
fun TopInputBar(
    newItemName: String,
    onNameChange: (String) -> Unit,
    newItemPrice: String,
    onPriceChange: (String) -> Unit,
    onAddItem: () -> Unit
) {
    Surface(
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = newItemName,
                    onValueChange = onNameChange,
                    label = { Text("Mahsulot nomi", color = MaterialTheme.colorScheme.onTertiary.copy(0.3f)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.onTertiary.copy(0.3f)
                    )
                )
                OutlinedTextField(
                    value = newItemPrice,
                    onValueChange = { onPriceChange(it.replace(Regex("[^0-9.]"), "")) },
                    label = { Text("Narxi", color = MaterialTheme.colorScheme.onTertiary.copy(0.3f)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(120.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = MaterialTheme.colorScheme.onTertiary.copy(0.3f)
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onAddItem,
                enabled = newItemName.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    disabledContentColor = MaterialTheme.colorScheme.onTertiary.copy(0.3f),
                    disabledContainerColor = MaterialTheme.colorScheme.onTertiary.copy(0.1f),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Ro'yxatga qo'shish", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ShoppingListItem(
    item: ShoppingItem,
    onToggleChecked: (String) -> Unit,
    onEdit: (ShoppingItem) -> Unit,
    onDelete: (String) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    val targetAlpha = if (item.isChecked) 0.65f else 1f
    val alpha by animateFloatAsState(targetAlpha, label = "alphaAnim")

    val targetColor = if (item.isChecked) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.primaryContainer
    val cardColor by animateColorAsState(targetColor, label = "colorAnim")

    val textColor = if (item.isChecked) MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onTertiary.copy(0.8f)
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RectangleShape)
                .background(cardColor)
                .alpha(alpha)
                .clickable { onToggleChecked(item.id) }
                .padding(vertical = 8.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Checkbox(
                    checked = item.isChecked,
                    onCheckedChange = { onToggleChecked(item.id) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = item.name,
                    color = textColor,
                    textDecoration = if (item.isChecked) TextDecoration.LineThrough else null,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    maxLines = 1
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = formatAmountWithCurrency(item.price),
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.tertiary,
                    textDecoration = if (item.isChecked) TextDecoration.LineThrough else null,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(8.dp))

                Box {
                    IconButton(onClick = { showMenu = true }, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Qo'shimcha amallar",
                            tint = MaterialTheme.colorScheme.onTertiary.copy(0.7f)
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit", color = MaterialTheme.colorScheme.onTertiary.copy(0.7f)) },
                            onClick = { onEdit(item); showMenu = false },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = "Tahrirlash", tint = MaterialTheme.colorScheme.primary) }
                        )
                        Divider()
                        DropdownMenuItem(
                            text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                            onClick = { showDeleteDialog = true },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = "O'chirish", tint = MaterialTheme.colorScheme.error) }
                        )
                    }
                }
            }
        }
        Divider(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.1f),
            thickness = 1.dp
        )
    }

    if (showDeleteDialog){
        DeleteConfirmationDialog(
            onDismiss = {showDeleteDialog = false},
            onConfirmDelete = {onDelete(item.id); showMenu = false},
            title = "Delete Product",
            text = "Rostdan ham maxsulotni o'chirishni hohlaysizmi?"
        )
    }

}

