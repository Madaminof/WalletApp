package dev.samandar.walletapp.wallet.smartScannQR.soliqParser

fun extractPaymentMethod(doc: org.jsoup.nodes.Document): String {
    val text = doc.text()

    // Chekda "Bank kartalari" va uning qarshisida summa turadi
    // Agar Bank kartalari summasi 0 dan katta bo'lsa - KARTA
    val cardPattern = Regex("""Bank\s+kartalari\s+([\d\s,.]+)""", RegexOption.IGNORE_CASE)
    val cardMatch = cardPattern.find(text)
    val cardAmount = cardMatch?.groupValues?.get(1)?.let { cleanAmountString(it) } ?: 0.0

    return if (cardAmount > 0) "KARTA" else "NAQD"
}