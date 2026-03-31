package dev.samandar.walletapp.wallet.data.mapper.account

import dev.samandar.walletapp.wallet.data.local.entity.account.AccountEntity
import dev.samandar.walletapp.wallet.data.local.entity.account.AccountType
import dev.samandar.walletapp.wallet.domain.model.account.Account

fun Account.toAccountEntity(): AccountEntity {
    return AccountEntity(
        id = this.id,
        name = this.name,
        type = this.type, // Enum: CASH yoki CARD
        balance = this.balance,
        currencyCode = this.currencyCode,
        colorHex = this.colorHex,
        iconResId = this.iconResId,
        isDefault = this.isDefault,

        // Agar Account Card bo'lsa, uning maxsus maydonlarini olamiz
        cardNumber = if (this is Account.Card) this.cardNumber else null,
        cardProvider = if (this is Account.Card) this.cardProvider else null,

        // Kelajakda BankName qo'shilsa shu yerga yoziladi
        bankName = null,
        createdAt = System.currentTimeMillis(),
        amountCurrencyKonverter = this.amountCurrencyKonverter
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
            amountCurrencyKonverter = this.amountCurrencyKonverter
        )
        AccountType.CARD -> Account.Card(
            id = this.id,
            name = this.name,
            balance = this.balance,
            currencyCode = this.currencyCode,
            colorHex = this.colorHex,
            iconResId = this.iconResId,
            cardNumber = this.cardNumber,
            cardProvider = this.cardProvider,
            isDefault = this.isDefault,
            amountCurrencyKonverter = this.amountCurrencyKonverter
        )
        // Yangi tur qo'shilsa (masalan BANK), shunchaki yangi branch qo'shiladi
    }
}