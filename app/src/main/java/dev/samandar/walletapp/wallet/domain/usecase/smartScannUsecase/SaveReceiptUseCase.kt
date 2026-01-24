package dev.samandar.walletapp.wallet.domain.usecase.smartScannUsecase

import dev.samandar.walletapp.wallet.domain.model.smartScannModel.Receipt
import dev.samandar.walletapp.wallet.domain.repository.smartScannRepository.ReceiptRepository
import javax.inject.Inject

class SaveReceiptUseCase @Inject constructor(
    private val repository: ReceiptRepository
) {
    suspend operator fun invoke(receipt: Receipt) {
        repository.saveReceipt(receipt)
    }
}