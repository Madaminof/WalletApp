package dev.samandar.walletapp.wallet.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import dev.samandar.walletapp.wallet.data.local.entity.account.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAccount(account: AccountEntity)

    @Update
    suspend fun updateAccount(account: AccountEntity)

    @Transaction
    suspend fun upsertAccount(account: AccountEntity) {
        val existingAccount = getAccountEntityById(account.id)
        if (existingAccount == null) {
            insertAccount(account)
        } else {
            updateAccount(account)
        }
    }

    @Delete
    suspend fun deleteAccount(account: AccountEntity)

    @Query("SELECT * FROM accounts")
    fun getAllAccounts(): Flow<List<AccountEntity>>

    @Query("SELECT * FROM accounts WHERE id = :id")
    suspend fun getAccountEntityById(id: String): AccountEntity?


    @Query("SELECT * FROM accounts")
    suspend fun getAllAccountsOnce(): List<AccountEntity>

    // 2. Ommaviy yangilash (Batch Update)
    // Room hamma accountlarni ID-si bo'yicha topib, yangi balanslarni yozib chiqadi
    @Update
    suspend fun updateAccounts(accounts: List<AccountEntity>)
}