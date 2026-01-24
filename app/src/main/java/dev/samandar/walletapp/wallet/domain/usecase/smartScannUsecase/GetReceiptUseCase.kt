package dev.samandar.walletapp.wallet.domain.usecase.smartScannUsecase

import dev.samandar.walletapp.wallet.domain.model.smartScannModel.Receipt
import dev.samandar.walletapp.wallet.domain.repository.smartScannRepository.ReceiptRepository
import javax.inject.Inject

class GetReceiptUseCase @Inject constructor(
    private val repository: ReceiptRepository
) {
    suspend operator fun invoke(transactionId: String): Receipt? {
        return repository.getReceiptByTransactionId(transactionId)
    }
}