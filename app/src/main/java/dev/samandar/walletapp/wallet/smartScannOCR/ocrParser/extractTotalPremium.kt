package dev.samandar.walletapp.wallet.smartScannOCR.ocrParser

fun extractTotalPremium(lines: List<String>): Double {
    // 1. "UZS" yoki "SUM" bilan tugaydigan qatorni qidiramiz (Eng aniq yo'l)
    val currencyRegex = Regex("""([\d\s,.]+)\s*(UZS|SUM|SO'M)""", RegexOption.IGNORE_CASE)

    // Pastdan tepaga qarab qidiramiz, chunki Jami summa pastda bo'ladi
    for (line in lines.reversed()) {
        val match = currencyRegex.find(line)
        if (match != null) {
            val value = cleanAmountString(match.groupValues[1])
            if (value != null && value > 100) return value
        }
    }

    // 2. Agar topilmasa, HIQDOR (MIQDOR) yoki To'lov so'zidan keyingi sonni qidirish
    val keyWords = listOf("miqdor", "hiqdor", "to'lov", "jami", "total")
    lines.forEachIndexed { index, line ->
        if (keyWords.any { line.contains(it, true) }) {
            // Keyingi 2 qatordan raqam qidiramiz
            for (i in 1..2) {
                if (index + i < lines.size) {
                    val nextLine = lines[index + i]
                    val amount = cleanAmountString(nextLine)
                    if (amount != null && amount > 100) return amount
                }
            }
        }
    }

    return 0.0
}

private fun cleanAmountString(raw: String): Double? {
    return try {
        // Faqat raqamlar va nuqta/vergulni qoldiramiz
        val cleaned = raw.replace(Regex("""[^\d.,]"""), "").replace(",", ".")

        if (cleaned.isEmpty()) return null

        // Agar "27800.08" bo'lsa
        val parts = cleaned.split(".")
        if (parts.size > 2) {
            val main = parts.dropLast(1).joinToString("")
            "$main.${parts.last()}".toDoubleOrNull()
        } else {
            cleaned.toDoubleOrNull()
        }
    } catch (e: Exception) { null }
}