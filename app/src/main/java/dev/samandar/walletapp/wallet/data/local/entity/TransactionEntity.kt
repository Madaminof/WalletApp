package dev.samandar.walletapp.wallet.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.samandar.walletapp.wallet.data.local.entity.account.AccountEntity

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = AccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE
        ),
    ],
    indices = [
        Index(value = ["accountId"]),
    ]
)
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    val amount: Double, // UZS ga aylantirib saqlanadiga amount
    val originalAmount: Double,   // Foydalanuvchi kiritgan (masalan: 15.0) $
    val originalCurrency: String, // Valyuta kodi (masalan: "USD")
    val amountInBase: Double,
    val exchangeRate: Double, // O'sha paytdagi kurs (masalan: 12850.0)
    val type: String,
    val categoryId: String,
    val accountId: String,
    val note: String? = null,
    val date: Long,
    val originalUrl: String? = null
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String,
    val iconResId: Int? = null,
    val colorArgb: Long
)
