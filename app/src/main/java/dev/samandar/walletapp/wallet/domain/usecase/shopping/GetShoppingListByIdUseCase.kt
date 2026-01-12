package dev.samandar.walletapp.wallet.domain.usecase.shopping

import dev.samandar.walletapp.wallet.domain.model.ShoppingList
import dev.samandar.walletapp.wallet.domain.repository.ShoppingRepository
import javax.inject.Inject

class GetShoppingListByIdUseCase @Inject constructor(
    private val repository: ShoppingRepository
) {
    suspend operator fun invoke(listId: String): ShoppingList? {
        return repository.getListsByListId(listId)
    }
}