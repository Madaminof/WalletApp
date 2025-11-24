package com.example.walletapp.wallet.data.repository

import com.example.walletapp.wallet.data.local.dao.AccountDao
import com.example.walletapp.wallet.data.mapper.toAccount
import com.example.walletapp.wallet.data.mapper.toAccountEntity
import com.example.walletapp.wallet.domain.model.Account
import com.example.walletapp.wallet.domain.repository.AccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val accountDao: AccountDao
) : AccountRepository {

    override fun getAllAccounts(): Flow<List<Account>> {
        return accountDao.getAllAccounts().map { entities ->
            entities.map { it.toAccount() }
        }
    }

    override suspend fun addAccount(account: Account): Result<Unit> = runCatching {
        val entity = account.toAccountEntity()
        accountDao.insertAccount(entity)
    }

    override suspend fun getAccountById(accountId: String): Result<Account> = runCatching {
        val entity = accountDao.getAccountEntityById(accountId)
            ?: throw IOException("Hisob topilmadi: ID = $accountId")

        entity.toAccount()
    }
    override suspend fun updateAccountBalance(accountId: String, amountChange: Double): Result<Unit> = runCatching {
        val currentAccountEntity = accountDao.getAccountEntityById(accountId)
            ?: throw IOException("Balansni yangilash uchun hisob topilmadi: ID = $accountId")
        val newBalance = currentAccountEntity.balance + amountChange
        val updatedEntity = currentAccountEntity.copy(balance = newBalance)
        accountDao.updateAccount(updatedEntity)
    }

    override suspend fun deleteAccount(account: Account): Result<Unit> = runCatching {
        val entity = account.toAccountEntity()
        accountDao.deleteAccount(entity)
    }
}