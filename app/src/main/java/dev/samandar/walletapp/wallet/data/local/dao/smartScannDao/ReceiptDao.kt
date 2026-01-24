package dev.samandar.walletapp.wallet.data.local.dao.smartScannDao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import dev.samandar.walletapp.wallet.data.local.entity.smartScannEntity.ReceiptEntity
import dev.samandar.walletapp.wallet.data.local.entity.smartScannEntity.ReceiptItemEntity
import dev.samandar.walletapp.wallet.data.local.entity.smartScannEntity.ReceiptWithItems

@Dao
interface ReceiptDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceipt(receipt: ReceiptEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceiptItems(items: List<ReceiptItemEntity>)

    @Transaction
    @Query("SELECT * FROM scanned_receipts WHERE transactionId = :transactionId")
    suspend fun getReceiptWithItems(transactionId: String): ReceiptWithItems?
}