package com.example.walletapp.wallet.domain.usecase.transaction

import com.example.walletapp.wallet.domain.repository.TransactionRepository
import javax.inject.Inject

class DeleteTransaction @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transactionId: String) {
        repository.deleteTransaction(transactionId)
    }
}

