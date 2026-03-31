package dev.samandar.walletapp.wallet.data.currencyManagerApi.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency_rates")
data class CurrencyRateEntity(
    @PrimaryKey val code: String, // "USD", "RUB"
    val rate: Double,             // 12850.0
    val lastUpdated: Long = System.currentTimeMillis()
)