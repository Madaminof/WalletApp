package com.example.walletapp.wallet.domain.usecase.transaction

import com.example.walletapp.wallet.domain.model.Transaction
import com.example.walletapp.wallet.domain.repository.TransactionRepository
import java.util.UUID
import javax.inject.Inject

class SaveTransaction @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction): Result<Unit> {
        if (transaction.amount <= 0) {
            return Result.failure(IllegalArgumentException("Tranzaksiya summasi musbat bo'lishi kerak."))
        }
        val transactionToSave = if (transaction.id.isBlank()) {
            transaction.copy(id = UUID.randomUUID().toString())
        } else {
            transaction
        }
        return transactionRepository.saveTransaction(transactionToSave)
    }
}