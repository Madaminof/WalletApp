package dev.samandar.walletapp.wallet.domain.usecase.account

import dev.samandar.walletapp.wallet.domain.model.account.Account
import dev.samandar.walletapp.wallet.domain.repository.account.AccountRepository
import java.util.UUID
import javax.inject.Inject

class AddAccount @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(account: Account): Result<Unit> {
        // 1. Validatsiya
        if (account.name.isBlank()) {
            return Result.failure(Exception("Hisob nomi bo'sh bo'lishi mumkin emas."))
        }
        val accountToSave = if (account.id.isBlank()) {
            val newId = UUID.randomUUID().toString()
            when (account) {
                is Account.Cash -> account.copy(id = newId)
                is Account.Card -> account.copy(id = newId)
            }
        } else {
            account
        }

        return try {
            accountRepository.addAccount(accountToSave)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}