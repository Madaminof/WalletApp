package com.example.walletapp.wallet.domain.model

data class ShoppingList(
    val id: String,
    val title: String,
    val createdAt: Long
)

data class ShoppingItem(
    val id: String,
    val listId: String,
    val name: String,
    val price: Double,
    val isChecked: Boolean
)
