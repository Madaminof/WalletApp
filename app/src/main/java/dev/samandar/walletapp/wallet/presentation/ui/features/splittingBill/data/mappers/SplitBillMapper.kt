package dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.mappers

import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity.BillEntity
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity.BillItemEntity
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity.ItemAssignmentEntity
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity.ParticipantEntity
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.relation.BillWithDetails
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.relation.ItemWithAssignments
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.domain.model.ItemAssignment
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.domain.model.Participant
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.domain.model.SplitBill
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.domain.model.SplitBillItem


// --- Relation to Domain Model ---

fun BillWithDetails.toDomain(): SplitBill {
    return SplitBill(
        id = bill.id,
        title = bill.title,
        date = bill.date,
        serviceChargePercent = bill.serviceChargePercent,
        taxPercent = bill.taxPercent,
        discountAmount = bill.discountAmount,
        totalAmount = bill.totalAmount,
        currency = bill.currency,
        participants = participants.map { it.toDomain() },
        items = items.map { it.toDomain() }
    )
}

fun ParticipantEntity.toDomain() = Participant(id = id, name = name)

fun ItemWithAssignments.toDomain() = SplitBillItem(
    id = item.id,
    name = item.itemName,
    price = item.price,
    quantity = item.quantity,
    assignments = assignments.map { it.toDomain() }
)

fun ItemAssignmentEntity.toDomain() = ItemAssignment(
    participantId = participantId,
    share = share
)

// --- Domain Model to Entity (Saqlash uchun) ---

fun SplitBill.toEntity() = BillEntity(
    id = id,
    title = title,
    date = date,
    serviceChargePercent = serviceChargePercent,
    taxPercent = taxPercent,
    discountAmount = discountAmount,
    totalAmount = totalAmount,
    currency = currency
)

fun Participant.toEntity(billId: String) = ParticipantEntity(
    id = id,
    billId = billId,
    name = name
)

fun SplitBillItem.toEntity(billId: String) = BillItemEntity(
    id = id,
    billId = billId,
    itemName = name,
    price = price,
    quantity = quantity
)

fun ItemAssignment.toEntity(itemId: String) = ItemAssignmentEntity(
    itemId = itemId,
    participantId = participantId,
    share = share
)