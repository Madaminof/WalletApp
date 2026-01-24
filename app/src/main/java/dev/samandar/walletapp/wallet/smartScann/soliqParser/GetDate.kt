package dev.samandar.walletapp.wallet.smartScann.soliqParser

fun extractDate(doc: org.jsoup.nodes.Document): Long {
    return try {
        val bodyText = doc.text()
        // "21.01.2026, 19:05" formatini qidiramiz
        val dateRegex = "(\\d{2}\\.\\d{2}\\.\\d{4},\\s\\d{2}:\\d{2})".toRegex()
        val matchResult = dateRegex.find(bodyText)

        if (matchResult != null) {
            val dateString = matchResult.value
            val sdf = java.text.SimpleDateFormat("dd.MM.yyyy, HH:mm", java.util.Locale.getDefault())
            sdf.parse(dateString)?.time ?: System.currentTimeMillis()
        } else {
            System.currentTimeMillis()
        }
    } catch (e: Exception) {
        System.currentTimeMillis()
    }
}