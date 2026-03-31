package dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.repository

import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.dao.SplitBillDao
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity.BillEntity
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity.BillItemEntity
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity.ItemAssignmentEntity
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.entity.ParticipantEntity
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.local.relation.BillWithDetails
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.data.mappers.toDomain
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.domain.repository.SplitBillRepository
import dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.domain.model.SplitBill
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SplitBillRepositoryImpl @Inject constructor(
    private val dao: SplitBillDao
) : SplitBillRepository {

    // Endi Flow<SplitBill?> interfeysga mos keladi
    override fun getBillWithDetails(billId: String): Flow<BillWithDetails?> {
        return dao.getBillWithDetails(billId)
    }

    override fun getAllBills(): Flow<List<SplitBill>> {
        return dao.getAllBillsWithDetails().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun saveBill(
        bill: BillEntity,
        participants: List<ParticipantEntity>,
        items: List<BillItemEntity>,
        assignments: List<ItemAssignmentEntity>
    ): Result<Unit> {
        return runCatching {
            dao.saveFullBill(bill, participants, items, assignments)
        }
    }

    override suspend fun deleteBill(billId: String): Result<Unit> {
        return runCatching {
            dao.deleteBillById(billId)
        }
    }

    override suspend fun isDuplicate(fiscalSign: String): Boolean {
        return dao.isBillExistsByFiscalSign(fiscalSign)
    }
}