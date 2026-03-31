package dev.samandar.walletapp.wallet.domain.model.account

import dev.samandar.walletapp.wallet.data.local.entity.account.AccountType

sealed class Account(
    open val id: String,
    open val name: String,
    open val balance: Double, // Foydalanuvchi ko'radigan balans (UZS kiritilsa UZS)
    open val amountCurrencyKonverter: Double, // Haqiqiy moliya balansi (USD/RUB)
    open val currencyCode: String,
    open val colorHex: String,
    open val iconResId: Int?,
    open val isDefault: Boolean,
    open val type: AccountType
) {
    // Naqd pul hisobi
    data class Cash(
        override val id: String,
        override val name: String,
        override val balance: Double,
        override val amountCurrencyKonverter: Double, // Konstruktorga qo'shildi
        override val currencyCode: String,
        override val colorHex: String,
        override val iconResId: Int?,
        override val isDefault: Boolean = false
    ) : Account(id, name, balance, amountCurrencyKonverter, currencyCode, colorHex, iconResId, isDefault, AccountType.CASH)

    // Plastik karta hisobi
    data class Card(
        override val id: String,
        override val name: String,
        override val balance: Double,
        override val amountCurrencyKonverter: Double, // Konstruktorga qo'shildi
        override val currencyCode: String,
        override val colorHex: String,
        override val iconResId: Int?,
        val cardNumber: String? = null,
        val cardProvider: String? = null,
        override val isDefault: Boolean = false
    ) : Account(id, name, balance, amountCurrencyKonverter, currencyCode, colorHex, iconResId, isDefault, AccountType.CARD)
}