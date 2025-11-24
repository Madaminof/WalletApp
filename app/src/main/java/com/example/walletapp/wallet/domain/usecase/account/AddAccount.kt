package com.example.walletapp.wallet.domain.usecase.account

import com.example.walletapp.wallet.domain.model.Account
import com.example.walletapp.wallet.domain.repository.AccountRepository
import java.util.UUID
import javax.inject.Inject

class AddAccount @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(account: Account): Result<Unit> {
        if (account.name.isBlank()) {
            return Result.failure(IllegalArgumentException("Hisob nomi bo'sh bo'lishi mumkin emas."))
        }
        val accountToSave = if (account.id.isBlank()) {
            account.copy(id = UUID.randomUUID().toString())
        } else {
            account
        }
        return accountRepository.addAccount(accountToSave)
    }
}