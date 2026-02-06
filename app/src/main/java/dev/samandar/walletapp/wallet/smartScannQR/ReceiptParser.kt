package dev.samandar.walletapp.wallet.smartScannQR


data class ParsedReceipt(
    val merchantName: String?,
    val merchantAddress: String? = null,
    val date: Long,
    val items: List<ParsedItem>,
    val totalAmount: Double,
    val paymentMethod: String,
    val suggestedCategory: String,
    val rawText: String
)

data class ParsedItem(
    val name: String,
    val price: Double,
    val quantity: Double = 1.0
)