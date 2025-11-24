package com.example.walletapp.wallet.di.RepositoryModule

import com.example.walletapp.wallet.data.repository.AccountRepositoryImpl
import com.example.walletapp.wallet.data.repository.BudgetRepositoryImpl
import com.example.walletapp.wallet.data.repository.CategoryRepositoryImpl
import com.example.walletapp.wallet.data.repository.impl.TransactionRepositoryImpl
import com.example.walletapp.wallet.domain.repository.AccountRepository
import com.example.walletapp.wallet.domain.repository.BudgetRepository
import com.example.walletapp.wallet.domain.repository.CategoryRepository
import com.example.walletapp.wallet.domain.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        transactionRepositoryImpl: TransactionRepositoryImpl
    ): TransactionRepository
    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        categoryRepositoryImpl: CategoryRepositoryImpl
    ): CategoryRepository
    @Binds
    @Singleton
    abstract fun bindAccountRepository(
        accountRepositoryImpl: AccountRepositoryImpl
    ): AccountRepository

    @Binds
    @Singleton
    abstract fun bindBudgetRepository(
        budgetRepositoryImpl: BudgetRepositoryImpl
    ): BudgetRepository


}