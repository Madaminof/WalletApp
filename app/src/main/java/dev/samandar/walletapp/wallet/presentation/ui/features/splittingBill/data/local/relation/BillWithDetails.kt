package dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity.BillEntity
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity.BillItemEntity
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity.ParticipantEntity


// BillWithDetails (To'liq chek modeli)
//Bu eng asosiy Relation bo'lib, butun "Hisobni bo'lish" ekranining ma'lumotlar manbai hisoblanadi.


data class BillWithDetails(
    @Embedded val bill: BillEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "billId"
    )
    val participants: List<ParticipantEntity>,

    @Relation(
        entity = BillItemEntity::class,
        parentColumn = "id",
        entityColumn = "billId"
    )
    val items: List<ItemWithAssignments> // Har bir mahsulot o'zining assignmentlari bilan
)