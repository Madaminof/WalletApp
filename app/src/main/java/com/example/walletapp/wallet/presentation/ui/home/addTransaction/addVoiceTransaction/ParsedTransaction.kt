package com.example.walletapp.wallet.presentation.ui.home.addTransaction.addVoiceTransaction

import com.example.walletapp.wallet.domain.model.Account
import com.example.walletapp.wallet.domain.model.Category
import com.example.walletapp.wallet.domain.model.TransactionType

data class ParsedTransaction(
    val amount: Double = 0.0,
    val type: TransactionType? = null,
    val category: Category? = null,
    val account: Account? = null,
    val note: String? = null
)
