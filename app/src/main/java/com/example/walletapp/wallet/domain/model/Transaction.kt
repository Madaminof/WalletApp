package com.example.walletapp.wallet.domain.model

data class Transaction(
    val id: String,
    val amount: Double,
    val type: TransactionType,
    val category: Category,
    val account: Account,
    val note: String? = null,
    val date: Long
)
data class Category(
    val id: String,
    val name: String,
    val type: TransactionType,
    val iconResId: Int? = null
)
data class Account(
    val id: String,
    val name: String,
    val initialBalance: Double,
    val colorHex: String? = null,
    val iconResId: Int? = null

)



enum class TransactionType {
    INCOME, EXPENSE
}
