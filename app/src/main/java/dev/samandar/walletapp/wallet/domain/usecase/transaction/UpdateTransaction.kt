package dev.samandar.walletapp.wallet.domain.usecase.transaction

import dev.samandar.walletapp.wallet.domain.model.Transaction
import dev.samandar.walletapp.wallet.domain.repository.TransactionRepository
import javax.inject.Inject

class UpdateTransaction @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke(transaction: Transaction): Result<Unit> {
        return repository.updateTransaction(transaction)
    }
}
