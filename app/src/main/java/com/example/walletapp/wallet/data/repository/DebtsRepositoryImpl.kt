package com.example.walletapp.wallet.data.repository

import com.example.walletapp.wallet.data.local.dao.DebtDao
import com.example.walletapp.wallet.data.mapper.toDomain
import com.example.walletapp.wallet.data.mapper.toEntity
import com.example.walletapp.wallet.domain.model.Debt
import com.example.walletapp.wallet.domain.repository.DebtsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DebtsRepositoryImpl @Inject constructor(
    private val dao: DebtDao
) : DebtsRepository {

    override fun getAllDebts(): Flow<List<Debt>> {
        return dao.getAllDebts().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertDebt(debt: Debt) {
        dao.insertDebt(debt.toEntity())
    }

    override suspend fun updateDebt(debt: Debt) {
        dao.updateDebt(debt.toEntity())
    }

    override suspend fun deleteDebtById(debtId: String) {
        dao.deleteDebtById(debtId)
    }
}