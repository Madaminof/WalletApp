package com.example.walletapp.wallet.domain.usecase.account

import com.example.walletapp.wallet.domain.model.Account
import com.example.walletapp.wallet.domain.repository.AccountRepository
import javax.inject.Inject

class DeleteAccount @Inject constructor(
    private val repository: AccountRepository,
) {
    suspend operator fun invoke(account: Account): Result<Unit> {
        return repository.deleteAccount(account)
    }
}