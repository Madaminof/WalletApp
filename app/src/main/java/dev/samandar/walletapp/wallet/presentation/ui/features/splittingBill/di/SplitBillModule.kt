package dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.samandar.walletapp.wallet.data.local.AppDatabase
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.dao.SplitBillDao
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.repository.SplitBillRepositoryImpl
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.domain.repository.SplitBillRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SplitBillModule {

    @Binds
    @Singleton
    abstract fun bindSplitBillRepository(
        splitBillRepositoryImpl: SplitBillRepositoryImpl,
    ): SplitBillRepository

    companion object {

        // 2. DAO ni taqdim etamiz
        @Provides
        @Singleton
        fun provideSplitBillDao(database: AppDatabase): SplitBillDao {
            return database.splitBillDao()
        }

    }
}