package com.example.walletapp.wallet.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey val id: String,
    val name: String,
    val balance: Double = 0.0,
    val colorHex: String? = null,
    val iconResId: Int? = null

)