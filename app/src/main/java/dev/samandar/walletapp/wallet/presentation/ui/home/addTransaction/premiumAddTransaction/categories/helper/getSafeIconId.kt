package dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.categories.helper

import android.content.Context

fun getSafeIconId(context: Context, iconResId: Int): Int {
    val defaultIcon = dev.samandar.walletapp.R.drawable.default_category

    // Agar ID 0 yoki manfiy bo'lsa, tekshirib o'tirmaymiz
    if (iconResId <= 0) return defaultIcon

    return try {
        // 1. Avval bu ID hozirgi versiyada mavjudmi, tekshiramiz
        context.resources.getResourceName(iconResId)

        // Agar xato bermasa, demak ID to'g'ri, lekin ehtiyot shart
        // yangi ID sini nomi orqali qayta tekshirib olamiz (eng optimali)
        val resourceName = context.resources.getResourceEntryName(iconResId)
        val currentId = context.resources.getIdentifier(
            resourceName,
            "drawable",
            context.packageName
        )

        if (currentId != 0) currentId else defaultIcon
    } catch (e: Exception) {
        // Har qanday NotFoundException yoki boshqa xatoda defaultga qaytamiz
        defaultIcon
    }
}