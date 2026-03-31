    package dev.samandar.walletapp.wallet.data.repository.account

import dev.samandar.walletapp.wallet.data.local.dao.account.AccountDao
import dev.samandar.walletapp.wallet.data.mapper.account.toAccount
import dev.samandar.walletapp.wallet.data.mapper.account.toAccountEntity
import dev.samandar.walletapp.wallet.domain.model.account.Account
import dev.samandar.walletapp.wallet.domain.repository.account.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val accountDao: AccountDao,
) : AccountRepository {

    override fun getAllAccounts(): Flow<List<Account>> {
        return accountDao.getAllAccounts().map { entities ->
            entities.map { it.toAccount() }
        }
    }

    // 'account: Account' endi Interface dagi 'Account' bilan bir xil paketda
    override suspend fun addAccount(account: Account): Result<Unit> = runCatching {
        val entity = account.toAccountEntity()

        // 'isDefault' xatosini tuzatish: Account sealed class-da bu maydon borligini tekshir
        if (account.isDefault) {
            val allEntities = accountDao.getAllAccountsOnce()
            // copy(isDefault = false) - Entity-da bu maydon borligiga ishonch hosil qil
            val updatedEntities = allEntities.map { it.copy(isDefault = false) }
            accountDao.updateAccounts(updatedEntities)
        }

        accountDao.upsertAccount(entity)
    }

    override suspend fun getAccountById(accountId: String): Result<Account> = runCatching {
        val entity = accountDao.getAccountEntityById(accountId)
            ?: throw IOException("Hisob topilmadi: ID = $accountId")

        entity.toAccount()
    }

    override suspend fun updateAccountBalance(
        accountId: String,
        amountChange: Double,
    ): Result<Unit> = runCatching {
        val currentAccountEntity = accountDao.getAccountEntityById(accountId)
            ?: throw IOException("Balansni yangilash uchun hisob topilmadi: ID = $accountId")

        val newBalance = currentAccountEntity.balance + amountChange
        val updatedEntity = currentAccountEntity.copy(balance = newBalance)
        accountDao.updateAccount(updatedEntity)
    }

    // deleteAccount argumentidagi 'Account' paketini to'g'riladik
    override suspend fun deleteAccount(account: Account): Result<Unit> = runCatching {
        val entity = account.toAccountEntity()
        accountDao.deleteAccount(entity)
    }

    override suspend fun getAllAccountsOnce(): List<Account> {
        val entities = accountDao.getAllAccountsOnce()
        return entities.map { it.toAccount() }
    }

    override suspend fun updateAccounts(accounts: List<Account>): Result<Unit> = runCatching {
        val entities = accounts.map { it.toAccountEntity() }
        accountDao.updateAccounts(entities)
    }

    override suspend fun updateAccountBalances(id: String, newBalance: Double, newKonverter: Double) {
        accountDao.updateAccountBalances(id, newBalance, newKonverter)
    }
}