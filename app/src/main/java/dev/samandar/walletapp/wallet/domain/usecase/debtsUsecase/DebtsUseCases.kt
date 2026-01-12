package dev.samandar.walletapp.wallet.domain.usecase.debtsUsecase

import javax.inject.Inject

data class DebtsUseCases @Inject constructor(
    val getAllDebts: GetAllDebts,
    val getDebtWithTransactions: GetDebtWithTransactions,
    val addUpdateDebt: AddUpdateDebt,
    val addDebtPayment: AddDebtPayment,
    val deleteDebt: DeleteDebt
)