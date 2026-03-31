package dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "participants",
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
data class ParticipantEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val billId: String,
    val name: String
)