package com.example.walletapp.wallet.domain.usecase.shopping

import com.example.walletapp.wallet.domain.model.ShoppingList
import com.example.walletapp.wallet.domain.repository.ShoppingRepository
import javax.inject.Inject

class GetShoppingListByIdUseCase @Inject constructor(
    private val repository: ShoppingRepository
) {
    suspend operator fun invoke(listId: String): ShoppingList? {
        return repository.getListsByListId(listId)
    }
}