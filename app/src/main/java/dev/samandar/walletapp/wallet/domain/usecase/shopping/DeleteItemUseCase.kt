package dev.samandar.walletapp.wallet.domain.usecase.shopping

import dev.samandar.walletapp.wallet.domain.repository.ShoppingRepository
import javax.inject.Inject

class DeleteItemUseCase@Inject constructor(
    private val repository: ShoppingRepository
) {
    suspend operator fun invoke(id: String) {
        repository.deleteItem(id)
    }
}
