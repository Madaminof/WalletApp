package dev.samandar.walletapp.wallet.domain.model

import dev.samandar.walletapp.wallet.domain.model.account.Account

data class Transaction(
    val id: String,
    val amount: Double, // UZS ga aylantirib saqlanadiga amount
    val originalAmount: Double,   // Foydalanuvchi kiritgan (masalan: 15.0) $
    val originalCurrency: String, // Valyuta kodi (masalan: "USD")
    val amountInBase: Double,
    val exchangeRate: Double, // O'sha paytdagi kurs (masalan: 12850.0)
    val type: TransactionType,
    val category: Category,
    val account: Account,
    val note: String? = null,
    val date: Long,
    val originalUrl: String? = null
)
data class Category(
    val id: String,
    val name: String,
    val type: TransactionType,
    val iconResId: Int? = null,
    val colorArgb: Long
)
enum class TransactionType {
    INCOME, EXPENSE
}
