package dev.samandar.walletapp.wallet.data.local.entity.account

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val balance: Double,

    @ColumnInfo(defaultValue = "#4CAF50")
    val colorHex: String,

    val iconResId: Int? = null,

    @ColumnInfo(defaultValue = "CASH")
    val type: AccountType,

    @ColumnInfo(defaultValue = "0.0")
    val amountCurrencyKonverter: Double,

    @ColumnInfo(defaultValue = "UZS")
    val currencyCode: String,

    val cardNumber: String? = null,
    val cardProvider: String? = null,
    val bankName: String? = null,

    @ColumnInfo(defaultValue = "0")
    val isDefault: Boolean,

    @ColumnInfo(defaultValue = "0")
    val createdAt: Long
)