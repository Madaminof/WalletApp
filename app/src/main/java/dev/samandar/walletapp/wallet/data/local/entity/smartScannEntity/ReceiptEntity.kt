package dev.samandar.walletapp.wallet.data.local.entity.smartScannEntity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.samandar.walletapp.wallet.data.local.entity.TransactionEntity

@Entity(
    tableName = "scanned_receipts",
    foreignKeys = [
        ForeignKey(
            entity = TransactionEntity::class,
            parentColumns = ["id"],
            childColumns = ["transactionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("transactionId")]
)
data class ReceiptEntity(
    @PrimaryKey
    val id: String = java.util.UUID.randomUUID().toString(),
    val transactionId: String,
    val originalUrl: String,
    val merchantName: String?,       // Do'kon nomi (Makro, Havas, Korzinka)
    val merchantAddress: String?,    // Manzil
    val date: Long,                  // Chekdagi sana va vaqt
    val totalAmount: Double,         // Chekning jami summasi
    val taxAmount: Double? = 0.0,    // NDS/Soliq summasi
    val paymentMethod: String?,      // Naqd yoki Karta (chekda yozilgan bo'lsa)
    val fiscalSign: String? = null,  // QR-kod (FPU) ma'lumoti
    val rawText: String? = null       // ML Kit o'qigan hamma matn (zaxira uchun)
)