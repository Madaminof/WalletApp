package dev.samandar.walletapp.wallet.smartScannQR.soliqParser

 fun extractAddress(doc: org.jsoup.nodes.Document): String {
    // 1. Eng aniq selectorlar (Soliq tizimidagi yangi va eski classlar)
    val prioritySelectors = listOf(
        ".merchant-address", ".address", "#address",
        "div.info-row:contains(Manzil)", "div.info-row:contains(Адрес)"
    )

    for (selector in prioritySelectors) {
        val element = doc.select(selector).firstOrNull()
        val text = element?.text()?.trim() ?: ""
        if (isValidHtmlAddress(text)) return formatSoliqAddress(text)
    }

    // 2. Mantiqiy blok bo'yicha qidiruv (Smart Search)
    // Soliq cheklarida manzil odatda "г. ", "ул. ", "sh. ", "tuman" kabi belgilarga ega
    val addressMarkers = Regex("(?i)г\\.|ул\\.|sh\\.|ko'ch|rayon|tuman|viloyat|toshkent|tashkent")

    // Barcha matnli elementlarni filtrlaymiz
    val elements = doc.select("p, div, span, td, li")
    val candidates = elements.map { it.text().trim() }
        .filter { it.length in 10..200 }
        .filter { addressMarkers.containsMatchIn(it) }
        .filter { isValidHtmlAddress(it) }

    if (candidates.isNotEmpty()) {
        // Eng uzun va manzilga o'xshashini olamiz (odatda eng to'lig'i shu bo'ladi)
        return formatSoliqAddress(candidates.maxByOrNull { it.length } ?: "")
    }

    // 3. Ierarxik Fallback (Vizual joylashuv bo'yicha)
    // Do'kon nomi va STIR orasidagi elementni topish
    val merchantHeader = doc.select("h1, h2, h3, .merchant-name, .name").firstOrNull()
    var current = merchantHeader?.nextElementSibling()

    // Keyingi 3 ta elementni tekshiramiz (chunki orada bo'sh divlar bo'lishi mumkin)
    repeat(3) {
        val text = current?.text()?.trim() ?: ""
        if (isValidHtmlAddress(text) && addressMarkers.containsMatchIn(text)) {
            return formatSoliqAddress(text)
        }
        current = current?.nextElementSibling()
    }

    return ""
}

private fun isValidHtmlAddress(text: String): Boolean {
    if (text.isBlank()) return false
    val lower = text.lowercase()

    // Texnik va moliyaviy ma'lumotlarni qat'iy filtrlaymiz
    val blackList = listOf(
        "stir", "inn", "kassa", "nkm", "terminal", "fiskal",
        "chek №", "sana", "vaqt", "summa", "uzs", "nds", "qvs"
    )

    // Agar qatorda faqat raqamlar bo'lsa (telefon yoki STIR bo'lishi mumkin)
    val digitCount = text.count { it.isDigit() }
    val isTooNumeric = digitCount > text.length * 0.5

    return text.length >= 10 && blackList.none { lower.contains(it) } && !isTooNumeric
}

private fun formatSoliqAddress(text: String): String {
    return text
        // "Manzil: ", "Адрес: ", "Location: " kabi prefikslarni tozalash
        .replace(Regex("(?i)^(manzil|адрес|address|location|локация)\\s*[:;\\-]*\\s*"), "")
        // HTML entity'larni tozalash (masalan &nbsp;)
        .replace("\u00A0", " ")
        // Ortiqcha probellar va yangi qatorlarni bittaga keltirish
        .replace(Regex("\\s+"), " ")
        .trim()
}