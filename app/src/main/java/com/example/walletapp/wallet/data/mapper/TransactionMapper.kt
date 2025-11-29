package com.example.walletapp.wallet.data.mapper

import com.example.walletapp.wallet.data.local.entity.AccountEntity
import com.example.walletapp.wallet.data.local.entity.CategoryEntity
import com.example.walletapp.wallet.data.local.entity.TransactionEntity
import com.example.walletapp.wallet.domain.model.Account
import com.example.walletapp.wallet.domain.model.Category
import com.example.walletapp.wallet.domain.model.Transaction
import com.example.walletapp.wallet.domain.model.TransactionType

fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = this.id,
        amount = this.amount,
        type = this.type.name,
        categoryId = this.category.id,
        accountId = this.account.id,
        note = this.note,
        date = this.date,
    )
}
fun TransactionEntity.toDomain(category: Category, account: Account): Transaction {
    return Transaction(
        id = this.id,
        amount = this.amount,
        type = TransactionType.valueOf(this.type),
        category = category,
        account = account,
        note = this.note,
        date = this.date
    )
}
fun Category.toCategoryEntity(): CategoryEntity {
    return CategoryEntity(
        id = this.id,
        name = this.name,
        type = this.type.name,
        iconResId = this.iconResId,
        colorArgb = this.colorArgb
    )
}
fun CategoryEntity.toCategory(): Category {
    return Category(
        id = this.id,
        name = this.name,
        type = TransactionType.valueOf(this.type),
        iconResId = this.iconResId,
        colorArgb = this.colorArgb

    )
}

fun Account.toAccountEntity(): AccountEntity {
    return AccountEntity(
        id = this.id,
        name = this.name,
        balance = this.initialBalance,
        colorHex = this.colorHex,
        iconResId = iconResId

    )
}
fun AccountEntity.toAccount(): Account {
    return Account(
        id = this.id,
        name = this.name,
        initialBalance = this.balance,
        colorHex = this.colorHex,
        iconResId = this.iconResId
    )
}