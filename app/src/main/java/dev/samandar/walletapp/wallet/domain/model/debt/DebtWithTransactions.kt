package dev.samandar.walletapp.wallet.domain.model.debt

data class DebtWithTransactions(
    val debt: Debt,
    val transactions: List<DebtTransaction>
)