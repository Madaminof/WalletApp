package dev.samandar.walletapp.wallet.di.RepositoryModule

import dev.samandar.walletapp.wallet.data.repository.AccountRepositoryImpl
import dev.samandar.walletapp.wallet.data.repository.BudgetRepositoryImpl
import dev.samandar.walletapp.wallet.data.repository.CategoryRepositoryImpl
import dev.samandar.walletapp.wallet.data.repository.debtRepository.DebtsRepositoryImpl
import dev.samandar.walletapp.wallet.data.repository.ShoppingRepositoryImpl
import dev.samandar.walletapp.wallet.data.repository.impl.TransactionRepositoryImpl
import dev.samandar.walletapp.wallet.domain.repository.AccountRepository
import dev.samandar.walletapp.wallet.domain.repository.BudgetRepository
import dev.samandar.walletapp.wallet.domain.repository.CategoryRepository
import dev.samandar.walletapp.wallet.domain.repository.debtRepository.DebtsRepository
import dev.samandar.walletapp.wallet.domain.repository.ShoppingRepository
import dev.samandar.walletapp.wallet.domain.repository.TransactionRepository
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

    @Binds
    @Singleton
    abstract fun provideShoppingRepository(
        shoppingRepositoryImpl: ShoppingRepositoryImpl
    ):ShoppingRepository

    @Binds
    @Singleton
    abstract fun provideDebtsRepository(
        debtsRepositoryImpl: DebtsRepositoryImpl
    ): DebtsRepository

}