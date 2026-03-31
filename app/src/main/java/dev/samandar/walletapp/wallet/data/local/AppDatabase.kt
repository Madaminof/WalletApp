package dev.samandar.walletapp.wallet.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.samandar.walletapp.wallet.data.currencyManagerApi.dao.CurrencyDao
import dev.samandar.walletapp.wallet.data.currencyManagerApi.entities.CurrencyRateEntity
import dev.samandar.walletapp.wallet.data.local.dao.CategoryDao
import dev.samandar.walletapp.wallet.data.local.dao.ShoppingDao
import dev.samandar.walletapp.wallet.data.local.dao.TransactionDao
import dev.samandar.walletapp.wallet.data.local.dao.account.AccountDao
import dev.samandar.walletapp.wallet.data.local.dao.budjetDao.BudgetDao
import dev.samandar.walletapp.wallet.data.local.dao.budjetDao.BudjetTransactionDao
import dev.samandar.walletapp.wallet.data.local.dao.debtDao.DebtDao
import dev.samandar.walletapp.wallet.data.local.dao.smartScannDao.ReceiptDao
import dev.samandar.walletapp.wallet.data.local.entity.BudgetEntity
import dev.samandar.walletapp.wallet.data.local.entity.CategoryEntity
import dev.samandar.walletapp.wallet.data.local.entity.ShoppingItemEntity
import dev.samandar.walletapp.wallet.data.local.entity.ShoppingListEntity
import dev.samandar.walletapp.wallet.data.local.entity.TransactionEntity
import dev.samandar.walletapp.wallet.data.local.entity.account.AccountEntity
import dev.samandar.walletapp.wallet.data.local.entity.debt.DebtEntity
import dev.samandar.walletapp.wallet.data.local.entity.debt.DebtTransactionEntity
import dev.samandar.walletapp.wallet.data.local.entity.smartScannEntity.ReceiptEntity
import dev.samandar.walletapp.wallet.data.local.entity.smartScannEntity.ReceiptItemEntity
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.dao.SplitBillDao
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity.BillEntity
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity.BillItemEntity
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity.ItemAssignmentEntity
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity.ParticipantEntity


@Database(
    entities = [
        TransactionEntity::class,
        CategoryEntity::class,
        AccountEntity::class,
        BudgetEntity::class,
        ShoppingListEntity::class,
        ShoppingItemEntity::class,
        DebtEntity::class,
        DebtTransactionEntity::class,
        ReceiptEntity::class,
        ReceiptItemEntity::class,
        CurrencyRateEntity::class,

        // --- SplitBill Entitylari qo'shildi ---
        BillEntity::class,
        ParticipantEntity::class,
        BillItemEntity::class,
        ItemAssignmentEntity::class
    ],
    version = 7,
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
    abstract fun receiptDao(): ReceiptDao

    abstract fun currencyDao(): CurrencyDao

    // --- SplitBill Dao qo'shildi ---
    abstract fun splitBillDao(): SplitBillDao

    companion object {
        const val DATABASE_NAME = "wallet_app_db"


    }
}