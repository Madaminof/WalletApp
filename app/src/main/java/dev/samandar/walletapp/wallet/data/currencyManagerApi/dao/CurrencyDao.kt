package dev.samandar.walletapp.wallet.data.currencyManagerApi.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.samandar.walletapp.wallet.data.currencyManagerApi.entities.CurrencyRateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRates(rates: List<CurrencyRateEntity>)

    @Query("SELECT rate FROM currency_rates WHERE code = :currencyCode")
    suspend fun getRate(currencyCode: String): Double?

    @Query("SELECT * FROM currency_rates")
    fun getAllRates(): Flow<List<CurrencyRateEntity>>

    @Query("SELECT * FROM currency_rates")
    suspend fun getAllRatesOnce(): List<CurrencyRateEntity>
}