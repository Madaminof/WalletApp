package com.example.walletapp.wallet.domain.usecase.shopping

import com.example.walletapp.wallet.domain.model.ShoppingList
import com.example.walletapp.wallet.domain.repository.ShoppingRepository
import javax.inject.Inject

class UpdateListUseCase@Inject constructor(
    private val repository: ShoppingRepository
) {
    suspend operator fun invoke(list: ShoppingList) {
        repository.updateList(list)
    }
}
