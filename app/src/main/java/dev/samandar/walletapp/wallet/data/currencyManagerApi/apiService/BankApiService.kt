package dev.samandar.walletapp.wallet.data.currencyManagerApi.apiService

import retrofit2.http.GET

import com.google.gson.annotations.SerializedName

data class CurrencyResponse(
    @SerializedName("Ccy") val code: String,      // "USD", "EUR" shu yerda keladi
    @SerializedName("Rate") val rate: String,     // Kurs String bo'lib keladi "12850.00"
    @SerializedName("Diff") val diff: String,
    @SerializedName("Date") val date: String
)

interface BankApiService {
    @GET("https://cbu.uz/uz/arkhiv-kursov-valyut/json/")
    suspend fun getExchangeRates(): List<CurrencyResponse>
}