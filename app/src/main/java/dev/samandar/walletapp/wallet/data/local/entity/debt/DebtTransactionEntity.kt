package dev.samandar.walletapp.wallet.data.local.entity.debt

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "debt_transactions",
    foreignKeys = [
        ForeignKey(
            entity = DebtEntity::class,
            parentColumns = ["id"],
            childColumns = ["debtId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["debtId"])]
)
data class DebtTransactionEntity(
    @PrimaryKey val id: String,
    val debtId: String,
    val amount: Double,
    val date: Long,
    val note: String?,
    val accountId: String?
)