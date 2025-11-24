package com.example.walletapp.wallet.domain.usecase.transaction


import com.example.walletapp.wallet.domain.model.Transaction
import com.example.walletapp.wallet.domain.model.TransactionType
import com.example.walletapp.wallet.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllTransactions @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(type: TransactionType? = null): Flow<List<Transaction>> {
        return transactionRepository.getAllTransactions(type)
    }
}