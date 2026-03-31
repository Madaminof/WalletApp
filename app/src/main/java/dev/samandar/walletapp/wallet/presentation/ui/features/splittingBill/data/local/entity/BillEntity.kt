package dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "bills")
data class BillEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val date: Long = System.currentTimeMillis(),
    val serviceChargePercent: Double = 0.0,
    val taxPercent: Double = 0.0,
    val discountAmount: Double = 0.0,
    val totalAmount: Double = 0.0,
    val currency: String = "UZS",
    val fiscalSign: String? = null, // Dublikat skanerlashni oldini olish uchun
    val isScanned: Boolean = false
)