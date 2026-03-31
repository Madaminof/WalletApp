package dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.presentation.view

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.presentation.view.components.additionalAmount.ExtraChargesBlock
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.presentation.view.components.bottomsheets.AddItemBottomSheet
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.presentation.view.components.bottomsheets.AddParticipantBottomSheet
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.presentation.view.components.buttons.ActionButtons
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.presentation.view.components.emptyState.EmptyStatePlaceholder
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.presentation.view.components.features.PdfExporter
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.presentation.view.components.itemCard.PremiumItemCard
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.presentation.view.components.textFields.BillTitleHeader
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.presentation.viewmodel.SplitBillViewModel
import dev.samandar.walletapp.wallet.presentation.ui.topbars.topbarScreen.CustomTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitBillScreen(
    viewModel: SplitBillViewModel = hiltViewModel(),
    billId: String?,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(billId) {
        if (billId != null) {
            viewModel.loadBillDetails(billId)
        }
    }

    var showAddItemSheet by remember { mutableStateOf(false) }
    var showAddParticipantBottomsheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = stringResource(Strings.quick_split_bill),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                onBackClick = onBack,
            )
        },
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    BillTitleHeader(
                        title = uiState.bill.title,
                        onTitleChange = { viewModel.updateBillTitle(it) }
                    )
                }
                item {
                    SectionHeaderWithAction(
                        title = stringResource(Strings.who_is_there),
                        onAdd = { showAddParticipantBottomsheet = true }
                    )
                }
                if (uiState.participants.isEmpty()) {
                    item {
                        EmptyStatePlaceholder(
                            title = stringResource(Strings.no_participants),
                            icon = Icons.Rounded.Groups
                        )
                    }
                }
                item { ParticipantsBlock(uiState, viewModel) }

                item {
                    SectionHeader(stringResource(Strings.assign_items))
                    Spacer(modifier = Modifier.padding(bottom = 16.dp))

                }

                if (uiState.items.isEmpty()) {
                    item {
                        EmptyStatePlaceholder(
                            title = stringResource(Strings.no_items_added),
                            icon = Icons.Rounded.ReceiptLong
                        )
                        Spacer(modifier = Modifier.padding(bottom = 16.dp))

                    }
                }

                items(uiState.items) { item ->
                    /*ItemCard(
                        item = item,
                        participants = uiState.participants,
                        assignments = uiState.assignments.filter { it.itemId == item.id },
                        onToggle = { pId -> viewModel.toggleAssignment(item.id, pId) },
                        onDelete = { viewModel.deleteItem(it) }
                    )*/

                    PremiumItemCard(
                        item = item,
                        participants = uiState.participants,
                        assignments = uiState.assignments.filter { it.itemId == item.id },
                        onToggleParticipant = { pId ->
                            viewModel.toggleAssignment(item.id, pId)
                        },
                        onSelectAll = {
                            // Hamma ishtirokchilarni ushbu mahsulotga biriktirish
                            uiState.participants.forEach { p ->
                                val isAssigned = uiState.assignments.any {
                                    it.itemId == item.id && it.participantId == p.id
                                }
                                if (!isAssigned) {
                                    viewModel.toggleAssignment(item.id, p.id)
                                }
                            }
                        },
                        onClearAll = {
                            // Ushbu mahsulotdan barcha ishtirokchilarni olib tashlash
                            uiState.participants.forEach { p ->
                                val isAssigned = uiState.assignments.any {
                                    it.itemId == item.id && it.participantId == p.id
                                }
                                if (isAssigned) {
                                    viewModel.toggleAssignment(item.id, p.id)
                                }
                            }
                        },
                        onDeleteItem = { itemId ->
                            viewModel.deleteItem(itemId)
                        }
                    )
                }

                // 3. Qo'shish tugmasi
                item {
                    OutlinedButton(
                        onClick = { showAddItemSheet = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(Strings.add_item_button), color = MaterialTheme.colorScheme.primary)
                    }
                }
                item {
                    ExtraChargesBlock(
                        serviceChargePercent = if (uiState.bill.serviceChargePercent == 0.0) "" else uiState.bill.serviceChargePercent.toString(),
                        onServiceChange = { viewModel.updateServiceCharge(it) },

                        taxPercent = if (uiState.bill.taxPercent == 0.0) "" else uiState.bill.taxPercent.toString(),
                        onTaxChange = { viewModel.updateTax(it) },

                        discountAmount = if (uiState.bill.discountAmount == 0.0) "" else uiState.bill.discountAmount.toString(),
                        onDiscountChange = { viewModel.updateDiscount(it) }
                    )
                }

                // 4. Natija
                item { uiState.billSummary?.let { SummaryBlock(it) } }
                item { Spacer(Modifier.height(100.dp)) }
            }

            val haptic = LocalHapticFeedback.current

            val onShare: () -> Unit = {
                try {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)

                    val shareText = viewModel.getShareSummaryText()

                    if (shareText.isNotBlank()) {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                            // Muhim: FLAG_ACTIVITY_NEW_DOCUMENT share oynasidan qaytganda
                            // ilova holatini buzmaslikni ta'minlaydi
                            addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
                        }

                        // Har doim yangi Chooser yaratish
                        val shareIntent = Intent.createChooser(sendIntent, "Hisobni ulashish")

                        val activity = context.findActivity()
                        if (activity != null) {
                            // Activity orqali ochilganda tizim ilovani o'ldirmaydi
                            activity.startActivity(shareIntent)
                        } else {
                            // Oxirgi chora sifatida flag bilan ochish
                            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            context.startActivity(shareIntent)
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("SHARE_DEBUG", "Xatolik: ${e.message}")
                }
            }



            ActionButtons(
                modifier = Modifier.align(Alignment.BottomCenter),
                isValid = viewModel.isFormValid,
                isSaving = uiState.isLoading,
                onSave = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    viewModel.saveBill(onSuccess = {
                        onBack()
                    })
                },
                onShare = {
                    onShare()
                },
                onPdfExport = {
                    // PDF eksport logikasi
                    val summary = uiState.billSummary
                    val billDate = uiState.bill?.date ?: System.currentTimeMillis()

                    if (summary != null) {
                        PdfExporter.exportToPdf(
                            context = context,
                            summary = summary,
                            billDate = billDate
                        )
                    } else {
                        Toast.makeText(context, "Ma'lumot topilmadi", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        // Dialoglar
        if (showAddParticipantBottomsheet) {
            AddParticipantBottomSheet(
                onDismiss = { showAddParticipantBottomsheet = false },
                onConfirm = {
                    viewModel.addParticipant(it)
                    showAddParticipantBottomsheet = false
                }

            )
        }

        if (showAddItemSheet) {
            AddItemBottomSheet(
                onDismiss = { showAddItemSheet = false },
                onAdd = { name, price, qty ->
                    viewModel.addItem(name, price, qty)
                    showAddItemSheet = false
                }
            )
        }
    }
}


fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}



@Composable
fun SectionHeader(title: String) {
    Text(title,
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            fontSize = 15.sp
        )
    )
}

