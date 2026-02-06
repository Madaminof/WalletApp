package dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.kalkulator

// Matematik amallarni bajarish uchun yordamchi funksiya
fun evaluateExpression(expression: String): Double {
    return try {
        // Oddiyroq loyihalar uchun 'exp4j' kutubxonasi tavsiya etiladi,
        // lekin biz bu yerda mantiqiy yondashuvni quramiz.
        val sanitized = expression.replace("×", "*").replace("÷", "/")
        // Real loyihada ushbu qatorda matematik parser bo'ladi
        0.0 // Natija qaytariladi
    } catch (e: Exception) {
        0.0
    }
}