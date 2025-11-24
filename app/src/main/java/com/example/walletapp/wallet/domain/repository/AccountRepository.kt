package com.example.walletapp.wallet.domain.repository

import com.example.walletapp.wallet.domain.model.Account
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    fun getAllAccounts(): Flow<List<Account>>
    suspend fun updateAccountBalance(accountId: String, amountChange: Double): Result<Unit>
    suspend fun addAccount(account: Account): Result<Unit>
    suspend fun getAccountById(accountId: String): Result<Account>
    suspend fun deleteAccount(account: Account): Result<Unit>

}