package dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// ItemAssignmentEntity (Bog'lovchi jadval)
//Kim qaysi mahsulotni yegani va uning ulushini belgilaydi.

@Entity(
    tableName = "item_assignments",
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = BillItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["itemId"],
            onDelete = androidx.room.ForeignKey.CASCADE
        ),
        androidx.room.ForeignKey(
            entity = ParticipantEntity::class,
            parentColumns = ["id"],
            childColumns = ["participantId"],
            onDelete = androidx.room.ForeignKey.CASCADE
        )
    ],
    indices = [
        androidx.room.Index("itemId"),
        androidx.room.Index("participantId")
    ]
)
data class ItemAssignmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val itemId: String,
    val participantId: String,
    val share: Double = 1.0 // Ulush (masalan, 0.5 bo'lsa yarmi)
)