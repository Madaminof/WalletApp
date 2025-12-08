package com.example.walletapp.wallet.domain.usecase.shopping

import com.example.walletapp.wallet.domain.model.ShoppingItem
import com.example.walletapp.wallet.domain.repository.ShoppingRepository
import javax.inject.Inject

class AddItemUseCase @Inject constructor(
    private val repository: ShoppingRepository
) {
    suspend operator fun invoke(item: ShoppingItem) {
        repository.addItem(item)
    }
}
