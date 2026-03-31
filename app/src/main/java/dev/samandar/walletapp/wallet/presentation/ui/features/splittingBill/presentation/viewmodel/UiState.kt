package dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.presentation.viewmodel

import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity.BillEntity
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity.BillItemEntity
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity.ItemAssignmentEntity
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity.ParticipantEntity
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.domain.model.BillSummary

// UI holati
data class SplitBillUiState(
    val bill: BillEntity = BillEntity(title = ""),
    val participants: List<ParticipantEntity> = emptyList(),
    val items: List<BillItemEntity> = emptyList(),
    val assignments: List<ItemAssignmentEntity> = emptyList(),
    val billSummary: BillSummary? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

// UI dan ViewModel ga keladigan buyruqlar
sealed class SplitBillEvent {
    data class AddParticipant(val name: String) : SplitBillEvent()
    data class AddItem(val name: String, val price: Double, val qty: Double) : SplitBillEvent()
    data class ToggleAssignment(val itemId: String, val participantId: String) : SplitBillEvent()
    object SaveBill : SplitBillEvent()
}