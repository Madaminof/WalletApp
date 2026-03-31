package dev.samandar.walletapp.wallet.data.mapper

import dev.samandar.walletapp.wallet.data.local.entity.account.AccountEntity
import dev.samandar.walletapp.wallet.data.local.entity.CategoryEntity
import dev.samandar.walletapp.wallet.data.local.entity.TransactionEntity
import dev.samandar.walletapp.wallet.data.local.entity.account.AccountType
import dev.samandar.walletapp.wallet.domain.model.account.Account
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
        originalUrl = this.originalUrl,
        originalAmount = originalAmount,
        originalCurrency = originalCurrency,
        exchangeRate = exchangeRate,
        amountInBase = amountInBase
    )
}
fun TransactionEntity.toDomain(
    category: Category,
    account: Account
): Transaction {
    return Transaction(
        id = this.id,
        amount = this.amount,
        type = TransactionType.valueOf(this.type),
        category = category,
        account = account,
        note = this.note,
        date = this.date,
        originalUrl = originalUrl,
        originalAmount = originalAmount,
        originalCurrency = originalCurrency,
        exchangeRate = exchangeRate,
        amountInBase = amountInBase
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
        balance = this.balance,
        colorHex = this.colorHex ?: "#4CAF50",
        iconResId = this.iconResId,
        // Account turini aniqlash
        type = when (this) {
            is Account.Cash -> AccountType.CASH
            is Account.Card -> AccountType.CARD
        },
        currencyCode = this.currencyCode,
        // Faqat Card bo'lsa ma'lumotlarni yozamiz, bo'lmasa null
        cardNumber = if (this is Account.Card) this.cardNumber else null,
        cardProvider = if (this is Account.Card) this.cardProvider else null,
        bankName = null, // Agar modelda bo'lsa qo'shishing mumkin
        isDefault = this.isDefault,
        createdAt = System.currentTimeMillis(),
        amountCurrencyKonverter = this.amountCurrencyKonverter // Yangi yaratilayotganda hozirgi vaqt
    )
}

fun AccountEntity.toAccount(): Account {
    return when (this.type) {
        AccountType.CASH -> Account.Cash(
            id = this.id,
            name = this.name,
            balance = this.balance,
            currencyCode = this.currencyCode,
            colorHex = this.colorHex,
            iconResId = this.iconResId,
            isDefault = this.isDefault,
            amountCurrencyKonverter = amountCurrencyKonverter
        )
        AccountType.CARD -> Account.Card(
            id = this.id,
            name = this.name,
            balance = this.balance,
            currencyCode = this.currencyCode,
            colorHex = this.colorHex,
            iconResId = this.iconResId,
            cardNumber = this.cardNumber ?: "",
            cardProvider = this.cardProvider ?: "UZCARD",
            isDefault = this.isDefault,
            amountCurrencyKonverter = amountCurrencyKonverter

        )
    }
}