package dev.samandar.walletapp.wallet.domain.usecase.shopping

import dev.samandar.walletapp.wallet.domain.repository.ShoppingRepository
import javax.inject.Inject

class GetItemsUseCase@Inject constructor(
    private val repository: ShoppingRepository
) {
    operator fun invoke(listId: String) = repository.getItemsByListId(listId)
}
