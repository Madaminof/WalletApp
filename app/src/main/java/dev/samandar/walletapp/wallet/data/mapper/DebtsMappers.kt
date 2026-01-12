package dev.samandar.walletapp.wallet.data.mapper

import dev.samandar.walletapp.wallet.data.local.entity.debt.DebtEntity
import dev.samandar.walletapp.wallet.data.local.entity.debt.DebtTransactionEntity
import dev.samandar.walletapp.wallet.domain.model.debt.Debt
import dev.samandar.walletapp.wallet.domain.model.debt.DebtTransaction
import dev.samandar.walletapp.wallet.domain.model.debt.DebtType


fun DebtEntity.toDomain(): Debt {
    return Debt(
        id = id,
        personName = personName,
        totalAmount = totalAmount,
        remainingAmount = remainingAmount,
        type = try {
            DebtType.valueOf(type)
        } catch (e: Exception) {
            DebtType.LENT
        },
        dueDate = dueDate,
        isSettled = isSettled,
        description = description,
        accountId = accountId,
        startDate = startDate,
    )
}

fun Debt.toEntity(): DebtEntity {
    return DebtEntity(
        id = id,
        personName = personName,
        totalAmount = totalAmount,
        remainingAmount = remainingAmount,
        type = type.name,
        startDate = startDate,  // 6-o'rin (Klassda qayerda bo'lsa, shu yerda bo'lishi kerak)
        dueDate = dueDate,      // 7-o'rin
        isSettled = isSettled,  // 9-o'rin
        description = description,
        accountId = accountId,
        createdAt = createdAt
    )
}

fun DebtTransactionEntity.toDomain(): DebtTransaction {
    return DebtTransaction(
        id = id,
        debtId = debtId,
        amount = amount,
        date = date,
        note = note,
        accountId = accountId
    )
}

fun DebtTransaction.toEntity(): DebtTransactionEntity {
    return DebtTransactionEntity(
        id = id,
        debtId = debtId,
        amount = amount,
        date = date,
        note = note,
        accountId = accountId
    )
}