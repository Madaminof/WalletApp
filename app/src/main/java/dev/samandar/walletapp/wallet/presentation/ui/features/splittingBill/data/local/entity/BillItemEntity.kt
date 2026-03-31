package dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "bill_items",
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = BillEntity::class,
            parentColumns = ["id"],
            childColumns = ["billId"],
            onDelete = androidx.room.ForeignKey.CASCADE
        )
    ],
    indices = [androidx.room.Index("billId")]
)
data class BillItemEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val billId: String,
    val itemName: String,
    val price: Double,
    val quantity: Double = 1.0,
    val splitStrategy: String = "EQUAL" // EQUAL, EXACT, PERCENT
)