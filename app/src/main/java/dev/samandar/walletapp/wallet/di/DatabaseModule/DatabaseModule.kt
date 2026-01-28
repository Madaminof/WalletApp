package dev.samandar.walletapp.wallet.di

import android.content.Context
import androidx.room.Room
import dev.samandar.walletapp.wallet.data.local.AppDatabase
import dev.samandar.walletapp.wallet.data.local.WalletDatabaseCallback
import dev.samandar.walletapp.wallet.data.local.dao.AccountDao
import dev.samandar.walletapp.wallet.data.local.dao.CategoryDao
import dev.samandar.walletapp.wallet.data.local.dao.debtDao.DebtDao
import dev.samandar.walletapp.wallet.data.local.dao.ShoppingDao
import dev.samandar.walletapp.wallet.data.local.dao.TransactionDao
import dev.samandar.walletapp.wallet.data.local.dao.budjetDao.BudgetDao
import dev.samandar.walletapp.wallet.data.local.dao.budjetDao.BudjetTransactionDao
import dev.samandar.walletapp.wallet.domain.repository.ShoppingRepository
import dev.samandar.walletapp.wallet.domain.usecase.shopping.AddItemUseCase
import dev.samandar.walletapp.wallet.domain.usecase.shopping.CreateListUseCase
import dev.samandar.walletapp.wallet.domain.usecase.shopping.DeleteItemUseCase
import dev.samandar.walletapp.wallet.domain.usecase.shopping.DeleteListUseCase
import dev.samandar.walletapp.wallet.domain.usecase.shopping.GetAllListsUseCase
import dev.samandar.walletapp.wallet.domain.usecase.shopping.GetItemsUseCase
import dev.samandar.walletapp.wallet.domain.usecase.shopping.GetShoppingListByIdUseCase
import dev.samandar.walletapp.wallet.domain.usecase.shopping.ShoppingUseCases
import dev.samandar.walletapp.wallet.domain.usecase.shopping.UpdateItemUseCase
import dev.samandar.walletapp.wallet.domain.usecase.shopping.UpdateListUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.samandar.walletapp.core.onBoarding.OnboardingManager
import dev.samandar.walletapp.wallet.data.local.dao.smartScannDao.ReceiptDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope = CoroutineScope(Dispatchers.IO)

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        callback: WalletDatabaseCallback
    ): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        AppDatabase.DATABASE_NAME
    )
        .addCallback(callback)
        .build()

    @Provides
    fun provideTransactionDao(db: AppDatabase): TransactionDao = db.transactionDao()

    @Provides
    fun provideCategoryDao(db: AppDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun provideAccountDao(db: AppDatabase): AccountDao = db.accountDao()

    @Provides
    fun provideBudgetDao(db: AppDatabase): BudgetDao = db.budgetDao()

    @Provides
    fun provideBudjetTransactionDao(db: AppDatabase): BudjetTransactionDao = db.budjetTransactionDao()

    @Provides
    @Singleton
    fun provideShoppingRepositoryDao(
        db: AppDatabase
    ): ShoppingDao = db.shoppingDao()

    @Provides
    @Singleton
    fun provideDebtsRepositoryDao(
        db: AppDatabase
    ): DebtDao = db.debtDao()

    @Provides
    @Singleton
    fun provideReceiptDao(database: AppDatabase): ReceiptDao {
        return database.receiptDao() // AppDatabase ichida 'receiptDao()' abstract funksiyasi bo'lishi shart
    }


    @Provides
    @Singleton
    fun provideShoppingUseCases(
        repo: ShoppingRepository
    ): ShoppingUseCases {
        return ShoppingUseCases(
            getAllLists = GetAllListsUseCase(repo),
            createList = CreateListUseCase(repo),
            updateList = UpdateListUseCase(repo),
            deleteList = DeleteListUseCase(repo),
            getItems = GetItemsUseCase(repo),
            addItem = AddItemUseCase(repo),
            updateItem = UpdateItemUseCase(repo),
            deleteItem = DeleteItemUseCase(repo),
            getShoppingListByIdUseCase = GetShoppingListByIdUseCase(repo)
        )
    }


    @Provides
    @Singleton
    fun provideOnboardingManager(
        @ApplicationContext context: Context
    ): OnboardingManager = OnboardingManager(context)

}