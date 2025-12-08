package com.example.walletapp.wallet.domain.repository


import com.example.walletapp.wallet.domain.model.Debt
import kotlinx.coroutines.flow.Flow

interface DebtsRepository {
    fun getAllDebts(): Flow<List<Debt>>
    suspend fun insertDebt(debt: Debt)
    suspend fun updateDebt(debt: Debt)
    suspend fun deleteDebtById(debtId: String)
}