package dev.samandar.walletapp.wallet.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.samandar.walletapp.wallet.data.local.dao.AccountDao
import dev.samandar.walletapp.wallet.data.local.dao.CategoryDao
import dev.samandar.walletapp.wallet.data.local.dao.debtDao.DebtDao
import dev.samandar.walletapp.wallet.data.local.dao.ShoppingDao
import dev.samandar.walletapp.wallet.data.local.dao.TransactionDao
import dev.samandar.walletapp.wallet.data.local.dao.budjetDao.BudgetDao
import dev.samandar.walletapp.wallet.data.local.dao.budjetDao.BudjetTransactionDao
import dev.samandar.walletapp.wallet.data.local.entity.AccountEntity
import dev.samandar.walletapp.wallet.data.local.entity.BudgetEntity
import dev.samandar.walletapp.wallet.data.local.entity.CategoryEntity
import dev.samandar.walletapp.wallet.data.local.entity.debt.DebtEntity
import dev.samandar.walletapp.wallet.data.local.entity.ShoppingItemEntity
import dev.samandar.walletapp.wallet.data.local.entity.ShoppingListEntity
import dev.samandar.walletapp.wallet.data.local.entity.TransactionEntity
import dev.samandar.walletapp.wallet.data.local.entity.debt.DebtTransactionEntity


@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        AccountEntity::class,
        BudgetEntity::class,
        ShoppingListEntity::class,
        ShoppingItemEntity::class,
        DebtEntity::class,
        DebtTransactionEntity::class
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
    abstract fun shoppingDao(): ShoppingDao

    abstract fun debtDao(): DebtDao

    companion object {
        const val DATABASE_NAME = "db_wallet_app_V1.0.0"


    }
}