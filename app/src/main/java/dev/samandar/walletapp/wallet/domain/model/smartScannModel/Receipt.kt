package dev.samandar.walletapp.wallet.domain.model.smartScannModel

data class Receipt(
    val id: String,
    val originalUrl: String,
    val transactionId: String,
    val merchantName: String?,
    val date: Long,
    val totalAmount: Double,
    val fiscalSign: String?,
    val taxAmount: Double? = 0.0,
    val paymentMethod: String? = null,
    val rawText: String? = null,
    val merchantAddress: String? = null,
    val note: String? = null,
    val items: List<ReceiptItem>
)

data class ReceiptItem(
    val id: String,
    val name: String,
    val quantity: Double,
    val unitPrice: Double,
    val totalPrice: Double,
    val categoryId: String?
)