package com.example.walletapp.wallet.di

import android.content.Context
import androidx.room.Room
import com.example.walletapp.wallet.data.local.AppDatabase
import com.example.walletapp.wallet.data.local.WalletDatabaseCallback
import com.example.walletapp.wallet.data.local.dao.AccountDao
import com.example.walletapp.wallet.data.local.dao.CategoryDao
import com.example.walletapp.wallet.data.local.dao.DebtDao
import com.example.walletapp.wallet.data.local.dao.ShoppingDao
import com.example.walletapp.wallet.data.local.dao.TransactionDao
import com.example.walletapp.wallet.data.local.dao.budjetDao.BudgetDao
import com.example.walletapp.wallet.data.local.dao.budjetDao.BudjetTransactionDao
import com.example.walletapp.wallet.domain.repository.ShoppingRepository
import com.example.walletapp.wallet.domain.usecase.shopping.AddItemUseCase
import com.example.walletapp.wallet.domain.usecase.shopping.CreateListUseCase
import com.example.walletapp.wallet.domain.usecase.shopping.DeleteItemUseCase
import com.example.walletapp.wallet.domain.usecase.shopping.DeleteListUseCase
import com.example.walletapp.wallet.domain.usecase.shopping.GetAllListsUseCase
import com.example.walletapp.wallet.domain.usecase.shopping.GetItemsUseCase
import com.example.walletapp.wallet.domain.usecase.shopping.GetShoppingListByIdUseCase
import com.example.walletapp.wallet.domain.usecase.shopping.ShoppingUseCases
import com.example.walletapp.wallet.domain.usecase.shopping.UpdateItemUseCase
import com.example.walletapp.wallet.domain.usecase.shopping.UpdateListUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
        .fallbackToDestructiveMigration()
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
    ): DebtDao = db.debbtsDao()


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

}