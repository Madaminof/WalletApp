package dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.exportImpl.pdf

import android.graphics.Color

object PdfTheme {
    // Ilovaning asosiy rangi: 0xFF4759C1
    val primaryColor = Color.parseColor("#4759C1")
    val primaryLight = Color.parseColor("#EEF0FF") // Kartochkalar uchun juda och ko'k

    val incomeColor = Color.parseColor("#2BB673")  // Modern yashil
    val expenseColor = Color.parseColor("#EF4444") // Modern qizil

    val textMain = Color.parseColor("#1E293B")      // To'q kulrang (deyarli qora)
    val textSecondary = Color.parseColor("#64748B") // Yordamchi matn uchun
    val borderLight = Color.parseColor("#E2E8F0")   // Yupqa chiziqlar uchun

    const val PAGE_WIDTH = 595
    const val PAGE_HEIGHT = 842

    const val STORE_URL = "https://play.google.com/store/apps/details?id=dev.samandar.walletapp"
    const val APP_SLOGAN = "Aqlli moliya nazorati — Wallet Analyst"
}