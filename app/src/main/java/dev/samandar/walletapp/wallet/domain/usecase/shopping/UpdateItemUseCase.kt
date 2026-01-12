package dev.samandar.walletapp.wallet.domain.usecase.shopping

import dev.samandar.walletapp.wallet.domain.model.ShoppingItem
import dev.samandar.walletapp.wallet.domain.repository.ShoppingRepository
import javax.inject.Inject

class UpdateItemUseCase@Inject constructor(
    private val repository: ShoppingRepository
) {
    suspend operator fun invoke(item: ShoppingItem) {
        repository.updateItem(item)
    }
}
