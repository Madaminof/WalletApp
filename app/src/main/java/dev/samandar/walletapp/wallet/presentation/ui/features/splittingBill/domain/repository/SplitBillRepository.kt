package dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.domain.repository

import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity.BillEntity
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity.BillItemEntity
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity.ItemAssignmentEntity
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity.ParticipantEntity
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.relation.BillWithDetails
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.domain.model.SplitBill
import kotlinx.coroutines.flow.Flow

interface SplitBillRepository {
    // Flow<BillWithDetails?> emas, Flow<SplitBill?> bo'lishi shart!
    fun getBillWithDetails(billId: String): Flow<BillWithDetails?>
    fun getAllBills(): Flow<List<SplitBill>>

    suspend fun saveBill(
        bill: BillEntity,
        participants: List<ParticipantEntity>,
        items: List<BillItemEntity>,
        assignments: List<ItemAssignmentEntity>
    ): Result<Unit>

    suspend fun deleteBill(billId: String): Result<Unit>
    suspend fun isDuplicate(fiscalSign: String): Boolean
}