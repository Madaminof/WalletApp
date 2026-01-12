package dev.samandar.walletapp.wallet.domain.usecase.debtsUsecase

import dev.samandar.walletapp.wallet.domain.model.debt.DebtWithTransactions
import dev.samandar.walletapp.wallet.domain.repository.debtRepository.DebtsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDebtWithTransactions @Inject constructor(
    private val repository: DebtsRepository
) {
    operator fun invoke(debtId: String): Flow<DebtWithTransactions?> {
        return repository.getDebtWithTransactions(debtId)
    }
}