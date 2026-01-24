package dev.samandar.walletapp.wallet.domain.repository.smartScannRepository

import dev.samandar.walletapp.wallet.domain.model.smartScannModel.Receipt

interface ReceiptRepository {
    suspend fun saveReceipt(receipt: Receipt)
    suspend fun getReceiptByTransactionId(transactionId: String): Receipt?
    suspend fun deleteReceipt(receiptId: String)
}