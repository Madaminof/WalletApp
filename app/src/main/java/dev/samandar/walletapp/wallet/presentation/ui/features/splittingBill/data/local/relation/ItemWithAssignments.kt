package dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity.BillItemEntity
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity.ItemAssignmentEntity


// ItemWithAssignments (Mahsulot va uni yeganlar)
//Bu model bitta mahsulotni (masalan, "Osh") va unga biriktirilgan barcha ishtirokchilarni ko'rish uchun kerak.

data class ItemWithAssignments(
    @Embedded val item: BillItemEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "itemId"
    )
    val assignments: List<ItemAssignmentEntity>
)