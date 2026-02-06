package dev.samandar.walletapp.wallet.smartScannQR.soliqParser

import android.util.Log
import dev.samandar.walletapp.wallet.smartScannQR.ParsedItem
import dev.samandar.walletapp.wallet.smartScannQR.ParsedReceipt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class SoliqParser @Inject constructor() {

    suspend fun parseFromUrl(url: String): ParsedReceipt? = withContext(Dispatchers.IO) {
        try {
            val uri = android.net.Uri.parse(url)
            val rawSum = uri.getQueryParameter("s")?.toDoubleOrNull()?.let { it / 100.0 } ?: 0.0

            val doc = Jsoup.connect(url)
                .timeout(15000)
                .userAgent("Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36")
                .get()

            val merchantName = extractMerchantName(doc)
            val items = extractAllItems(doc)
            val totalAmount = extractTotalFromHtml(doc) ?: rawSum
            val parsedDate = extractDate(doc)
            val paymentType = extractPaymentMethod(doc)
            val merchantAddress = extractAddress(doc)


            ParsedReceipt(
                merchantName = merchantName,
                merchantAddress = merchantAddress.ifEmpty { "Manzil aniqlanmadi" }, // Default qiymat
                date = parsedDate,
                items = items.ifEmpty { listOf(ParsedItem("Xarid", totalAmount, 1.0)) },
                totalAmount = totalAmount,
                paymentMethod = paymentType,
                suggestedCategory = "Xarid",
                rawText = url,
            )
        } catch (e: Exception) {
            Log.e("SoliqParser", "🔴 Xatolik: ${e.message}")
            null
        }
    }


}