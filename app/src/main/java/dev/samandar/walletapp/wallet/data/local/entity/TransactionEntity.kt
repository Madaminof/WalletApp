package dev.samandar.walletapp.wallet.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
    val amount: Double,
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

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey val id: String,
    val name: String,
    val balance: Double = 0.0,
    val colorHex: String? = null,
    val iconResId: Int? = null

)