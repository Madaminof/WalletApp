package dev.samandar.walletapp.wallet.presentation.ui.features.shoppingLists.shoppingListScreen

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.samandar.walletapp.R
import dev.samandar.walletapp.navigation.Screen
import dev.samandar.walletapp.wallet.domain.model.ShoppingList
import dev.samandar.walletapp.wallet.presentation.ui.features.shoppingLists.dialogs.AddListDialog
import dev.samandar.walletapp.wallet.presentation.ui.features.shoppingLists.dialogs.EditListDialog
import dev.samandar.walletapp.wallet.presentation.ui.topbars.topbarScreen.CustomTopBar
import dev.samandar.walletapp.wallet.presentation.viewmodel.ShoppingViewModel
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencyManager
import dev.samandar.walletapp.wallet.presentation.ui.features.shoppingLists.EmptyListState


val activeCurrency by CurrencyManager.currentCurrency

@Composable
fun ShoppingListScreen(
    navController: NavController,
    shoppingViewModel: ShoppingViewModel = hiltViewModel()
) {
    val uiState by shoppingViewModel.state.collectAsState()
    val shoppingListsWithStats = uiState.listsWithStats
    val context = LocalContext.current

    var showAddListDialog by remember { mutableStateOf(false) }
    var listToEdit by remember { mutableStateOf<ShoppingList?>(null) }
    var showEditListDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { shoppingViewModel.loadLists() }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CustomTopBar(
                title = stringResource(R.string.title_shopping_lists),
                onBackClick = { navController.popBackStack() },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddListDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
            ) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter))
            } else if (shoppingListsWithStats.isEmpty()) {
                EmptyListState(modifier = Modifier.fillMaxSize())
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    itemsIndexed(
                        items = shoppingListsWithStats,
                        key = { _, item -> item.list.id }
                    ) { index, listWithStats ->
                        val boughtAmount = listWithStats.items
                            .filter { it.isChecked }
                            .sumOf { it.price }

                        ShoppingListRow(
                            list = listWithStats.list,
                            totalItems = listWithStats.stats.totalItems,
                            boughtItems = listWithStats.stats.boughtItems,
                            totalAmount = boughtAmount,
                            index = index,
                            onItemClick = { listId ->
                                navController.navigate("${Screen.ShoppingDetail.route}/$listId")
                            },
                            onDelete = { listId -> shoppingViewModel.deleteList(listId) },
                            onEdit = { list ->
                                listToEdit = list
                                showEditListDialog = true
                            },
                            onShare = {
                                val itemDetails = listWithStats.items.map { item ->
                                    "${item.name}: ${"%.0f".format(item.price)} $activeCurrency"
                                }
                                shareShoppingList(context, listWithStats.list.title, listWithStats.stats.totalAmount, itemDetails)
                            },
                            modifier = Modifier.animateItem(
                                placementSpec = spring(
                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                        )
                    }
                }
            }
        }
    }

    if (showAddListDialog) {
        AddListDialog(
            onDismiss = { showAddListDialog = false },
            onAddList = { inputTitle ->
                if (inputTitle.isNotBlank()) {
                    shoppingViewModel.createList(title = inputTitle.trim()) { newListId ->
                        navController.navigate("${Screen.ShoppingDetail.route}/$newListId")
                    }
                    showAddListDialog = false
                }
            }
        )
    }

    if (showEditListDialog && listToEdit != null) {
        EditListDialog(
            list = listToEdit!!,
            onDismiss = { showEditListDialog = false; listToEdit = null },
            onUpdateList = { updatedTitle ->
                val updatedList = listToEdit!!.copy(title = updatedTitle.trim())
                shoppingViewModel.updateList(updatedList)
                showEditListDialog = false
                listToEdit = null
            }
        )
    }
}


