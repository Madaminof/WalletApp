package dev.samandar.walletapp.wallet.domain.usecase.account

import dev.samandar.walletapp.wallet.domain.model.account.Account
import dev.samandar.walletapp.wallet.domain.repository.account.AccountRepository
import javax.inject.Inject

class DeleteAccount @Inject constructor(
    private val repository: AccountRepository,
) {
    suspend operator fun invoke(account: Account): Result<Unit> {
        return repository.deleteAccount(account)
    }
}