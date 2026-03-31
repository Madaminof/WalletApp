package dev.samandar.walletapp.wallet.data.local.entity.account

import androidx.room.TypeConverter

class AccountTypeConverter {
    @TypeConverter
    fun fromAccountType(type: AccountType): String = type.name

    @TypeConverter
    fun toAccountType(value: String): AccountType = AccountType.valueOf(value)
}