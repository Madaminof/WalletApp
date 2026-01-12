package dev.samandar.walletapp.wallet.data.local.entity.debt

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "debts")
data class DebtEntity(
    @PrimaryKey val id: String,
    val personName: String,
    val totalAmount: Double,
    val remainingAmount: Double,
    val type: String,
    val startDate: Long,
    val dueDate: Long?,
    val createdAt: Long,
    val isSettled: Boolean,
    val description: String?,
    val accountId: String
)