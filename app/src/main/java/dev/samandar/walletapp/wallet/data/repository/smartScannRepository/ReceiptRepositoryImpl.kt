package dev.samandar.walletapp.wallet.data.repository.smartScannRepository

import dev.samandar.walletapp.wallet.data.local.dao.smartScannDao.ReceiptDao
import dev.samandar.walletapp.wallet.data.mapper.toDomain
import dev.samandar.walletapp.wallet.data.mapper.toEntity
import dev.samandar.walletapp.wallet.domain.model.smartScannModel.Receipt
import dev.samandar.walletapp.wallet.domain.repository.smartScannRepository.ReceiptRepository
import javax.inject.Inject

class ReceiptRepositoryImpl @Inject constructor(
    private val dao: ReceiptDao
) : ReceiptRepository {

    override suspend fun saveReceipt(receipt: Receipt) {
        // Bitta tranzaksiya ichida saqlash muhim
        dao.insertReceipt(receipt.toEntity())
        dao.insertReceiptItems(receipt.items.map { it.toEntity(receipt.id) })
    }

    override suspend fun getReceiptByTransactionId(transactionId: String): Receipt? {
        return dao.getReceiptWithItems(transactionId)?.let {
            it.receipt.toDomain(it.items)
        }
    }

    override suspend fun deleteReceipt(receiptId: String) {
    }
}