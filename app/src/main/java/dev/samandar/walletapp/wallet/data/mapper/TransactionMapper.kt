package dev.samandar.walletapp.wallet.data.mapper

import dev.samandar.walletapp.wallet.data.local.entity.AccountEntity
import dev.samandar.walletapp.wallet.data.local.entity.CategoryEntity
import dev.samandar.walletapp.wallet.data.local.entity.TransactionEntity
import dev.samandar.walletapp.wallet.domain.model.Account
import dev.samandar.walletapp.wallet.domain.model.Category
import dev.samandar.walletapp.wallet.domain.model.Transaction
import dev.samandar.walletapp.wallet.domain.model.TransactionType

fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = this.id,
        amount = this.amount,
        type = this.type.name,
        categoryId = this.category.id,
        accountId = this.account.id,
        note = this.note,
        date = this.date,
        originalUrl = this.originalUrl
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
        date = this.date,
        originalUrl = originalUrl
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