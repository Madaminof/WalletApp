package dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity.BillEntity
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity.BillItemEntity
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity.ItemAssignmentEntity
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity.ParticipantEntity
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.relation.BillWithDetails
import kotlinx.coroutines.flow.Flow


@Dao
interface SplitBillDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBill(bill: BillEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipants(participants: List<ParticipantEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBillItems(items: List<BillItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemAssignments(assignments: List<ItemAssignmentEntity>)

    /**
     * Butun bir chekni barcha detallari bilan bitta tranzaksiyada saqlash.
     * Bu usul juda xavfsiz: yo hamma narsa saqlanadi, yo hech narsa.
     */
    @Transaction
    suspend fun saveFullBill(
        bill: BillEntity,
        participants: List<ParticipantEntity>,
        items: List<BillItemEntity>,
        assignments: List<ItemAssignmentEntity>
    ) {
        insertBill(bill)
        // Eskilarini tozalash (agar update bo'lsa)
        deleteParticipantsByBillId(bill.id)
        deleteItemsByBillId(bill.id)

        insertParticipants(participants)
        insertBillItems(items)
        insertItemAssignments(assignments)
    }

    // --- O'QISH AMALLARI (Flow bilan) ---

    @Transaction
    @Query("SELECT * FROM bills WHERE id = :billId")
    fun getBillWithDetails(billId: String): Flow<BillWithDetails?>

    @Transaction
    @Query("SELECT * FROM bills ORDER BY date DESC")
    fun getAllBillsWithDetails(): Flow<List<BillWithDetails>>

    @Query("SELECT EXISTS(SELECT 1 FROM bills WHERE fiscalSign = :fiscalSign)")
    suspend fun isBillExistsByFiscalSign(fiscalSign: String): Boolean

    // --- O'CHIRISH AMALLARI ---

    @Query("DELETE FROM bills WHERE id = :billId")
    suspend fun deleteBillById(billId: String)

    @Query("DELETE FROM participants WHERE billId = :billId")
    suspend fun deleteParticipantsByBillId(billId: String)

    @Query("DELETE FROM bill_items WHERE billId = :billId")
    suspend fun deleteItemsByBillId(billId: String)
}