package com.example.walletapp.wallet.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "debts")
data class DebtEntity(
    @PrimaryKey val id: String,
    val person: String,
    val amount: Double,
    val isLent: Boolean,
    val date: Long,
    val isSettled: Boolean,
)