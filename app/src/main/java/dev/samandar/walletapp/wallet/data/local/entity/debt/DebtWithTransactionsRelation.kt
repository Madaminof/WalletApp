package dev.samandar.walletapp.wallet.data.local.entity.debt

import androidx.room.Embedded
import androidx.room.Relation


data class DebtWithTransactionsRelation(
    @Embedded val debt: DebtEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "debtId"
    )
    val transactions: List<DebtTransactionEntity>
)
