package com.example.walletapp.wallet.data.mapper

import com.example.walletapp.wallet.data.local.entity.DebtEntity
import com.example.walletapp.wallet.domain.model.Debt

fun DebtEntity.toDomain(): Debt {
    return Debt(
        id = id,
        person = person,
        amount = amount,
        isLent = isLent,
        date = date,
        isSettled = isSettled,
    )
}

fun Debt.toEntity(): DebtEntity {
    return DebtEntity(
        id = id,
        person = person,
        amount = amount,
        isLent = isLent,
        date = date,
        isSettled = isSettled,
    )
}