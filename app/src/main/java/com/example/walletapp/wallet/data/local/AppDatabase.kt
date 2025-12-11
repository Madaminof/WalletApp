package com.example.walletapp.wallet.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.walletapp.wallet.data.local.dao.AccountDao
import com.example.walletapp.wallet.data.local.dao.CategoryDao
import com.example.walletapp.wallet.data.local.dao.DebtDao
import com.example.walletapp.wallet.data.local.dao.ShoppingDao
import com.example.walletapp.wallet.data.local.dao.TransactionDao
import com.example.walletapp.wallet.data.local.dao.budjetDao.BudgetDao
import com.example.walletapp.wallet.data.local.dao.budjetDao.BudjetTransactionDao
import com.example.walletapp.wallet.data.local.entity.AccountEntity
import com.example.walletapp.wallet.data.local.entity.BudgetEntity
import com.example.walletapp.wallet.data.local.entity.CategoryEntity
import com.example.walletapp.wallet.data.local.entity.DebtEntity
import com.example.walletapp.wallet.data.local.entity.ShoppingItemEntity
import com.example.walletapp.wallet.data.local.entity.ShoppingListEntity
import com.example.walletapp.wallet.data.local.entity.TransactionEntity

@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        AccountEntity::class,
        BudgetEntity::class,
        ShoppingListEntity::class,
        ShoppingItemEntity::class,
        DebtEntity::class
               ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun accountDao(): AccountDao
    abstract fun budgetDao(): BudgetDao
    abstract fun budjetTransactionDao(): BudjetTransactionDao
    abstract fun shoppingDao():ShoppingDao
    abstract fun debbtsDao():DebtDao


    companion object {
        const val DATABASE_NAME = "wallet_version_v1.3"
    }
}