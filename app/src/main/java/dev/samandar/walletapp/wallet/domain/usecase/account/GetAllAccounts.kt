package dev.samandar.walletapp.wallet.domain.usecase.account


import dev.samandar.walletapp.wallet.domain.model.Account
import dev.samandar.walletapp.wallet.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllAccounts @Inject constructor(
    private val accountRepository: AccountRepository
) {
    operator fun invoke(): Flow<List<Account>> {
        return accountRepository.getAllAccounts()
    }
}