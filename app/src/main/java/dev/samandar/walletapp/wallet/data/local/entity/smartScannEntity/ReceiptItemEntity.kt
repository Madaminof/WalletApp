package dev.samandar.walletapp.wallet.data.local.entity.smartScannEntity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "receipt_details",
    foreignKeys = [
        ForeignKey(
            entity = ReceiptEntity::class,
            parentColumns = ["id"],
            childColumns = ["receiptId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("receiptId")]
)
data class ReceiptItemEntity(
    @PrimaryKey
    val id: String = java.util.UUID.randomUUID().toString(),
    val receiptId: String,           // Qaysi chekka tegishli
    val productName: String,         // Mahsulot nomi (masalan: "Sut 1L")
    val quantity: Double,            // Miqdori (1.0, 0.500)
    val unitPrice: Double,           // Bir birlik narxi
    val totalPrice: Double,          // Jami (quantity * unitPrice)
    val categoryId: String? = null   // Avtomatik aniqlangan kategoriya IDsi
)