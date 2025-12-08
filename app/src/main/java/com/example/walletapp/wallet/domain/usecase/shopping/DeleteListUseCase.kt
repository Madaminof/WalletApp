package com.example.walletapp.wallet.domain.usecase.shopping

import com.example.walletapp.wallet.domain.repository.ShoppingRepository
import javax.inject.Inject

class DeleteListUseCase@Inject constructor(
    private val repository: ShoppingRepository
) {
    suspend operator fun invoke(id: String) {
        repository.deleteList(id)
    }
}
