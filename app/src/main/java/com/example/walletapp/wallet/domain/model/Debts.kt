package com.example.walletapp.wallet.domain.model

data class Debt(
    val id: String,
    val person: String, // Qarzdor yoki Kreditordan shaxs nomi
    val amount: Double,
    val isLent: Boolean, // True = Qarz berilgan (You lent), False = Qarz olingan (You owe)
    val date: Long,
    val isSettled: Boolean = false,
)