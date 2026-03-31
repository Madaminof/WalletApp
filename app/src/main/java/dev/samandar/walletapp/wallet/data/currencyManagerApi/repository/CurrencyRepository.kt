package dev.samandar.walletapp.wallet.data.currencyManagerApi.repository

import dev.samandar.walletapp.wallet.data.currencyManagerApi.apiService.BankApiService
import dev.samandar.walletapp.wallet.data.currencyManagerApi.dao.CurrencyDao
import dev.samandar.walletapp.wallet.data.currencyManagerApi.entities.CurrencyRateEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CurrencyRepository @Inject constructor(
    private val apiService: BankApiService,
    private val currencyDao: CurrencyDao
) {
    val allRates: Flow<List<CurrencyRateEntity>> = currencyDao.getAllRates()

    private val defaultRates = mapOf(
        "USD" to 12250.0,
        "RUB" to 155.0,
        "EUR" to 14350.0,
        "UZS" to 1.0
    )

    suspend fun syncRates(): Result<Unit> = runCatching {
        val response = apiService.getExchangeRates()

        // LOG: Kelayotgan birinchi valyutani ko'raylik, nima deb kelyapti?
        if (response.isNotEmpty()) {
            println("CurrencyDebug: Birinchi valyuta kodi: ${response[0].code}")
        }

        val targetCodes = listOf("USD", "RUB", "EUR")

        // Katta-kichik harfga qaramasdan filter qilamiz
        val entities = response
            .filter { res -> targetCodes.any { it.equals(res.code, ignoreCase = true) } }
            .map {
                CurrencyRateEntity(
                    code = it.code.uppercase(),
                    rate = it.rate.toDoubleOrNull() ?:defaultRates[it.code.uppercase()] ?: 0.0,
                    lastUpdated = System.currentTimeMillis()
                )
            }

        if (entities.isNotEmpty()) {
            currencyDao.insertRates(entities)
            println("CurrencyDebug: Bazaga ${entities.size} ta kurs saqlandi!")
        } else {
            // Agar hali ham topilmasa, API'dagi hamma kodlarni chiqarib beradi (aniqlash uchun)
            val allCodes = response.map { it.code }.take(5).joinToString(", ")
            println("CurrencyDebug: Mos kelmadi. API'dan kelgan ba'zi kodlar: $allCodes")
        }
    }

    suspend fun getRate(code: String): Double {
        val upperCode = code.uppercase()
        if (upperCode == "UZS") return 1.0

        // 2. Bazadan olishga harakat qilamiz
        val rateFromDb = currencyDao.getRate(upperCode)

        // 3. Agar bazada yo'q bo'lsa, default kursni qaytaramiz (Crash va 0 ning oldini oladi)
        return rateFromDb ?: defaultRates[upperCode] ?: 1.0
    }


    suspend fun prePopulateDatabase() {
        val existingRates = currencyDao.getAllRatesOnce()
        if (existingRates.isEmpty()) {
            val initialEntities = defaultRates.filter { it.key != "UZS" }.map { (code, rate) ->
                CurrencyRateEntity(code = code, rate = rate)
            }
            currencyDao.insertRates(initialEntities)
            println("CurrencyDebug: Baza bo'sh edi, default kurslar bilan to'ldirildi.")
        }
    }

    suspend fun getLatestRatesOnce(): List<CurrencyRateEntity> {
        return currencyDao.getAllRatesOnce() // DAO-da bu funksiyani suspend qilib yozish kerak
    }
}