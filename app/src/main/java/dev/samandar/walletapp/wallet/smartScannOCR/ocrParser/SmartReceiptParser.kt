package dev.samandar.walletapp.wallet.smartScannOCR.ocrParser

import android.util.Log
import dev.samandar.walletapp.wallet.smartScannQR.ParsedItem
import dev.samandar.walletapp.wallet.smartScannQR.ParsedReceipt
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmartReceiptParser @Inject constructor() {

    fun parseOcrText(rawText: String,defaultItemName: String): ParsedReceipt {
        val lines = rawText.lines().map { it.trim() }.filter { it.isNotEmpty() }
        Log.d("DEBUG_OCR", "FULL TEXT: $rawText")


        val totalAmount = extractTotalPremium(lines)
        val merchantName = extractMerchantPremium(lines)
        val dateLong = extractDatePremium(lines)

        val isCard = rawText.contains(Regex("Humo|Uzcard|Karta|Card|Visa|Master", RegexOption.IGNORE_CASE))

        return ParsedReceipt(
            merchantName = merchantName,
            totalAmount = totalAmount,
            paymentMethod = if (isCard) "KARTA" else "NAQD",
            date = dateLong,
            rawText = rawText,
            merchantAddress = extractAddress(lines),
            items = listOf(ParsedItem(defaultItemName, totalAmount, 1.0)),
            suggestedCategory = "Xarid"
        )
    }
}