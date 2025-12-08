package com.example.walletapp.wallet.domain.usecase.shopping

import com.example.walletapp.wallet.domain.repository.ShoppingRepository
import javax.inject.Inject

class GetAllListsUseCase@Inject constructor(
    private val repository: ShoppingRepository
) {
    operator fun invoke() = repository.getAllLists()
}
