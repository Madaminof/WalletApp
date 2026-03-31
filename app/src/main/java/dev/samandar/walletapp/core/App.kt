package dev.samandar.walletapp.core

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import dev.samandar.walletapp.wallet.data.currencyManagerApi.currencySyncWorker.setupCurrencySync
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Configuration.Provider { // 🔥 1. Provider interfeysini qo'shdik

    @Inject
    lateinit var workerFactory: HiltWorkerFactory // 🔥 2. Hilt Worker zavodini chaqirdik

    // 🔥 3. WorkManager'ga Hilt zavodini ulab berdik
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()


    override fun onCreate() {
        super.onCreate()
        // 🔥 Worker setupini shu yerga ko'chiring.
        // Bu ilova har qanday holatda uyg'onganda (hatto fonda ham)
        // Worker'ni to'g'ri konfiguratsiya bilan ishga tushiradi.
        setupCurrencySync(this)
    }
}