package com.example.walletapp.wallet.domain.usecase.transaction

import com.example.walletapp.wallet.domain.model.Transaction
import com.example.walletapp.wallet.domain.repository.TransactionRepository
import javax.inject.Inject

class UpdateTransaction @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction): Result<Unit> {
        return repository.updateTransaction(transaction)
    }
}
