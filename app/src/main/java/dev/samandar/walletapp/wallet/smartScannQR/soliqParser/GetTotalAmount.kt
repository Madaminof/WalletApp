package dev.samandar.walletapp.wallet.smartScannQR.soliqParser

fun extractTotalFromHtml(doc: org.jsoup.nodes.Document): Double? {
    return try {
        val element = doc.getElementsContainingOwnText("Jami to`lov").firstOrNull()
        val textToSearch = element?.parent()?.text() ?: doc.text()
        val pattern = Regex("""Jami\s+to`lov:?\s*([\d\s,.]+)""", RegexOption.IGNORE_CASE)
        val match = pattern.find(textToSearch)

        match?.groupValues?.get(1)?.let { rawValue ->
            cleanAmountString(rawValue)
        }
    } catch (e: Exception) {
        null
    }
}
fun cleanAmountString(raw: String): Double? {
    return raw.trim()
        .replace(" ", "")    // Bo'shliqlarni olib tashlaymiz (22 094 -> 22094)
        .replace(",", ".")   // Vergulni nuqtaga almashtiramiz (22094,54 -> 22094.54)
        // Agar nuqtalar birdan ko'p bo'lsa (masalan 22.094.54), oxirgisidan boshqasini o'chiramiz
        .let { s ->
            val parts = s.split(".")
            if (parts.size > 2) {
                val integerPart = parts.dropLast(1).joinToString("")
                "$integerPart.${parts.last()}"
            } else s
        }.toDoubleOrNull()
}