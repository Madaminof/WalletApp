package dev.samandar.walletapp.wallet.domain.repository.account

import dev.samandar.walletapp.wallet.domain.model.account.Account
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun getAllAccounts(): Flow<List<Account>>
    suspend fun updateAccountBalance(accountId: String, amountChange: Double): Result<Unit>
    suspend fun addAccount(account: Account): Result<Unit>
    suspend fun getAccountById(accountId: String): Result<Account>
    suspend fun deleteAccount(account: Account): Result<Unit>

    suspend fun getAllAccountsOnce(): List<Account>
    suspend fun updateAccounts(accounts: List<Account>): Result<Unit>

    suspend fun updateAccountBalances(id: String, newBalance: Double, newKonverter: Double)

}