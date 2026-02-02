package dev.samandar.walletapp.wallet.data.repository.impl

import dev.samandar.walletapp.wallet.data.local.dao.TransactionDao
import dev.samandar.walletapp.wallet.data.local.entity.TransactionEntity
import dev.samandar.walletapp.wallet.data.mapper.toDomain
import dev.samandar.walletapp.wallet.data.mapper.toEntity
import dev.samandar.walletapp.wallet.domain.model.Transaction
import dev.samandar.walletapp.wallet.domain.model.TransactionType
import dev.samandar.walletapp.wallet.domain.repository.AccountRepository
import dev.samandar.walletapp.wallet.domain.repository.CategoryRepository
import dev.samandar.walletapp.wallet.domain.repository.TransactionRepository
import kotlinx.coroutines.Dispatchers // Dispatchers for IO thread
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn // Function to change Flow dispatcher
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val categoryRepository: CategoryRepository,
    private val accountRepository: AccountRepository,
) : TransactionRepository {

    override suspend fun saveTransaction(transaction: Transaction): Result<Unit> = runCatching {
        val entity = transaction.toEntity()
        transactionDao.insertTransaction(entity)
        val amountChange = if (transaction.type == TransactionType.INCOME) {
            transaction.amount
        } else {
            -transaction.amount
        }
        accountRepository.updateAccountBalance(transaction.account.id, amountChange).getOrThrow()
    }

    override fun getAllTransactions(type: TransactionType?): Flow<List<Transaction>> {
        val entityFlow: Flow<List<TransactionEntity>> = if (type == null) {
            transactionDao.getAllTransactions()
        } else {
            val typeString = type.name
            transactionDao.getTransactionsByType(typeString)
        }
        return entityFlow.map { entities ->
            entities.map { entity ->

                val categoryResult = categoryRepository.getCategoryById(entity.categoryId)
                val accountResult = accountRepository.getAccountById(entity.accountId)

                val category = categoryResult.getOrThrow()
                val account = accountResult.getOrThrow()

                entity.toDomain(category, account)
            }
        }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun deleteTransaction(id: String): Result<Unit> = runCatching {
        val transaction = getTransactionById(id).getOrThrow()

        val amountReverseChange = if (transaction.type == TransactionType.INCOME) {
            -transaction.amount
        } else {
            transaction.amount
        }

        accountRepository.updateAccountBalance(transaction.account.id, amountReverseChange)
            .getOrThrow()

        transactionDao.deleteTransactionById(id)
    }

    override suspend fun updateTransaction(transaction: Transaction): Result<Unit> = runCatching {
        val oldTransaction = getTransactionById(transaction.id).getOrThrow()

        val oldAmountReverse = if (oldTransaction.type == TransactionType.INCOME) {
            -oldTransaction.amount
        } else {
            oldTransaction.amount
        }
        accountRepository.updateAccountBalance(oldTransaction.account.id, oldAmountReverse)
            .getOrThrow()

        val newAmountChange = if (transaction.type == TransactionType.INCOME) {
            transaction.amount
        } else {
            -transaction.amount
        }
        accountRepository.updateAccountBalance(transaction.account.id, newAmountChange).getOrThrow()
        val entity = transaction.toEntity()
        transactionDao.updateTransaction(entity)
    }

    override fun getTransactionsByDateRange(
        startDate: Long,
        endDate: Long,
    ): Flow<List<Transaction>> {
        TODO("TransactionDao da Date Range metodini yaratish kerak")
    }

    override suspend fun countTransactionsByDateRange(
        startDateMillis: Long,
        endDateMillis: Long,
    ): Int {
        return transactionDao.countTransactionsByDateRange(startDateMillis, endDateMillis)
    }

    override suspend fun getTransactionById(id: String): Result<Transaction> = runCatching {
        val entity = transactionDao.getTransactionEntityById(id)
            ?: throw IOException("Tranzaksiya topilmadi: ID = $id")

        val category = categoryRepository.getCategoryById(entity.categoryId).getOrThrow()
        val account = accountRepository.getAccountById(entity.accountId).getOrThrow()

        entity.toDomain(category, account)
    }
}