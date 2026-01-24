package dev.samandar.walletapp.wallet.data.local.entity.smartScannEntity

import androidx.room.Embedded
import androidx.room.Relation

data class ReceiptWithItems(
    @Embedded
    val receipt: ReceiptEntity,

    @Relation(
        parentColumn = "id",      // ReceiptEntity ichidagi PK
        entityColumn = "receiptId" // ReceiptItemEntity ichidagi FK
    )
    val items: List<ReceiptItemEntity>
)