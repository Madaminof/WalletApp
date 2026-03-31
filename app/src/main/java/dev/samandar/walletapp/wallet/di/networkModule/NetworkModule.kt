package dev.samandar.walletapp.wallet.di.networkModule

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.samandar.walletapp.wallet.data.currencyManagerApi.apiService.BankApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://cbu.uz/uz/arkhiv-kursov-valyut/") // Markaziy bank API bazaviy manzili
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideBankApiService(retrofit: Retrofit): BankApiService {
        // Hilt endi BankApiService so'ralganda shu metodni ishlatadi
        return retrofit.create(BankApiService::class.java)
    }
}