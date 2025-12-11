package com.example.walletapp.wallet.presentation.ui.otherScreens.shoppingLists

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.walletapp.core.AppStatusBarColor
import com.example.walletapp.navigation.Screen
import com.example.walletapp.wallet.domain.model.ShoppingList
import com.example.walletapp.wallet.presentation.ui.otherScreens.shoppingLists.dialogs.AddListDialog
import com.example.walletapp.wallet.presentation.ui.otherScreens.shoppingLists.dialogs.EditListDialog
import com.example.walletapp.wallet.presentation.ui.otherScreens.topbar.CustomTopBar
import com.example.walletapp.wallet.presentation.viewmodel.ShoppingViewModel
import java.text.NumberFormat
import java.util.Locale
import java.util.UUID
import android.util.Log
import android.content.ActivityNotFoundException
import com.example.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencyManager
import com.example.walletapp.wallet.presentation.utils.formatAmountWithCurrency

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

    LaunchedEffect(Unit) {
        shoppingViewModel.loadLists()
    }
    AppStatusBarColor(MaterialTheme.colorScheme.primaryContainer)

    Scaffold(
        topBar = {
            CustomTopBar(
                navController = navController,
                title = "Shopping lists",
                onBackClick = { navController.popBackStack() },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddListDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Yangi ro'yxat qo'shish")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                shoppingListsWithStats.isEmpty() -> {
                    EmptyListState(modifier = Modifier.fillMaxSize())
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(top = 8.dp)
                    ) {
                        items(shoppingListsWithStats, key = { it.list.id }) { listWithStats ->
                            ShoppingListRow(
                                list = listWithStats.list,
                                totalItems = listWithStats.stats.totalItems,
                                boughtItems = listWithStats.stats.boughtItems,
                                totalAmount = listWithStats.stats.totalAmount,
                                onItemClick = { listId ->
                                    navController.navigate("${Screen.ShoppingDetail.route}/$listId")
                                },
                                onDelete = { listId ->
                                    shoppingViewModel.deleteList(listId)
                                },
                                onEdit = { list ->
                                    listToEdit = list
                                    showEditListDialog = true
                                },
                                onShare = {
                                    val itemDetails = listWithStats.items.map { item ->
                                        "${item.name}: ${"%.0f".format(item.price)} $activeCurrency"
                                    }

                                    shareShoppingList(
                                        context = context,
                                        listName = listWithStats.list.title,
                                        totalAmount = listWithStats.stats.totalAmount,
                                        itemDetails = itemDetails
                                    )
                                }
                            )
                        }
                        item { Spacer(Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }

    if (showAddListDialog) {
        AddListDialog(
            onDismiss = { showAddListDialog = false },
            onAddList = { title ->
                val list = ShoppingList(
                    id = UUID.randomUUID().toString(),
                    title = title.trim(),
                    createdAt = System.currentTimeMillis()
                )
                shoppingViewModel.createList(list)
                showAddListDialog = false
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


@Composable
fun ShoppingListRow(
    list: ShoppingList,
    totalItems: Int,
    boughtItems: Int,
    totalAmount: Double,
    onItemClick: (String) -> Unit,
    onDelete: (String) -> Unit,
    onEdit: (ShoppingList) -> Unit,
    onShare: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth().padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onItemClick(list.id) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.6f)),
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = list.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                    modifier = Modifier.weight(1f)
                )
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Qo'shimcha amallar",
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit", color = MaterialTheme.colorScheme.onTertiary.copy(0.7f)) },
                            onClick = {
                                onEdit(list)
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Tahrirlash",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        )
                        Divider()
                        DropdownMenuItem(
                            text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                            onClick = {
                                onDelete(list.id)
                                showMenu = false
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "O'chirish",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$boughtItems/$totalItems items",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.6f),
                )
                Text(
                    text = formatAmountWithCurrency(totalAmount),
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.7f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.3f), thickness = 0.5.dp)
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                ShareButton(onShare = onShare)
            }
        }
    }
}

@Composable
fun EmptyListState(modifier: Modifier = Modifier) {
    val containerColor = MaterialTheme.colorScheme.primaryContainer

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(containerColor)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.ListAlt,
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .padding(bottom = 8.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Sizda Xarid Ro'yxatlari Yo'q",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onTertiary.copy(0.6f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Boshlash uchun, '➕' tugmasini bosing va birinchi ro'yxatingizni yarating.",
            style = MaterialTheme.typography.bodyLarge, // Kattaroq shrift
            color = MaterialTheme.colorScheme.onTertiary.copy(0.4f),
            textAlign = TextAlign.Center
        )
    }
}

fun shareShoppingList(
    context: Context,
    listName: String,
    totalAmount: Double,
    itemDetails: List<String>
) {
    val numberFormat = NumberFormat.getNumberInstance(Locale("uz", "UZ")).apply {
        maximumFractionDigits = 0
    }
    val formattedTotalAmount = numberFormat.format(totalAmount)

    val cleanItemDetails = itemDetails
        .mapNotNull { it?.trim() }
        .filter { it.isNotBlank() }

    val itemsFormatted = if (cleanItemDetails.isNotEmpty()) {
        cleanItemDetails.joinToString(separator = "\n")
    } else {
        "Xarid uchun mahsulotlar hali kiritilmagan."
    }

    val shareText = """
          Xarid Ro'yxati Tafsilotlari

Ro'yxat nomi: $listName
Mahsulotlar soni: (${cleanItemDetails.size})

$itemsFormatted

Umumiy Hisob: $formattedTotalAmount $activeCurrency
    """.trimIndent()

    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    try {
        context.startActivity(shareIntent)
    } catch (e: ActivityNotFoundException) {
        Log.e("ShareError", "Qabul qiluvchi ilova topilmadi: ${e.message}")
    } catch (e: Exception) {
        Log.e("ShareError", "Intentni ishga tushirishda kutilmagan xato: ${e.message}")
    }
}

@Composable
fun ShareButton(onShare: () -> Unit) {
    OutlinedButton(
        onClick = onShare,
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Icon(
            Icons.Default.Share,
            contentDescription = "Ulashish",
            modifier = Modifier.size(16.dp)
        )

        Spacer(Modifier.width(8.dp))
        Text(
            "Share",
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
        )
    }
}