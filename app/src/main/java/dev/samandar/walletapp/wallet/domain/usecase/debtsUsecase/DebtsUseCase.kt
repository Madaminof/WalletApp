package dev.samandar.walletapp.wallet.domain.usecase.debtsUsecase

import dev.samandar.walletapp.wallet.domain.model.debt.Debt
import dev.samandar.walletapp.wallet.domain.repository.debtRepository.DebtsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetAllDebts @Inject constructor(
    private val repository: DebtsRepository
) {
    operator fun invoke(): Flow<List<Debt>> {
        return repository.getAllDebts()
    }
}


class DeleteDebt @Inject constructor(
    private val repository: DebtsRepository
) {
    suspend operator fun invoke(debtId: String) {
        repository.deleteDebtById(debtId)
    }
}