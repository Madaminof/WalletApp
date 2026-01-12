package dev.samandar.walletapp.wallet.data.repository.debtRepository

import dev.samandar.walletapp.wallet.data.local.dao.debtDao.DebtDao
import dev.samandar.walletapp.wallet.data.mapper.toDomain
import dev.samandar.walletapp.wallet.data.mapper.toEntity
import dev.samandar.walletapp.wallet.domain.model.debt.Debt
import dev.samandar.walletapp.wallet.domain.model.debt.DebtTransaction
import dev.samandar.walletapp.wallet.domain.model.debt.DebtWithTransactions
import dev.samandar.walletapp.wallet.domain.repository.debtRepository.DebtsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DebtsRepositoryImpl @Inject constructor(
    private val dao: DebtDao
) : DebtsRepository {

    override fun getAllDebts(): Flow<List<Debt>> =
        dao.getAllDebts().map { entities -> entities.map { it.toDomain() } }

    override fun getActiveDebts(): Flow<List<Debt>> =
        dao.getActiveDebts().map { entities -> entities.map { it.toDomain() } }


    // DebtsRepositoryImpl.kt ichida
    override fun getDebtWithTransactions(debtId: String): Flow<DebtWithTransactions?> =
        dao.getDebtWithTransactions(debtId).map { relation ->
            // RELATION NULL BO'LSA XATO BERMASDAN NULL QAYTARISH
            if (relation == null) return@map null

            // Faqat relation mavjud bo'lganda mapping qilish
            DebtWithTransactions(
                debt = relation.debt.toDomain(),
                transactions = relation.transactions.map { it.toDomain() }
            )
        }

    override suspend fun getDebtById(debtId: String): Debt? {
        return dao.getDebtById(debtId)?.toDomain()
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

    override suspend fun addDebtPayment(transaction: DebtTransaction) {
        dao.addPaymentToDebt(transaction.toEntity())
    }

    override fun getTotalLentAmount(): Flow<Double> =
        dao.getTotalLentAmount().map { it ?: 0.0 }

    override fun getTotalBorrowedAmount(): Flow<Double> =
        dao.getTotalBorrowedAmount().map { it ?: 0.0 }
}