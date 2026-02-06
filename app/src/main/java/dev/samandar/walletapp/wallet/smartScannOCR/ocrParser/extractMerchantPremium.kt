package dev.samandar.walletapp.wallet.smartScannOCR.ocrParser

fun extractMerchantPremium(lines: List<String>): String {
    val blackList = listOf(
        "humo", "uzcard", "terminal", "cheki", "savdo", "to'lov", "mijoz",
        "kassa", "inn", "stir", "tasdiqlandi", "karta", "card", "cless"
    )

    // Nomzodlarni yig'amiz
    val candidates = lines.take(10).mapNotNull { line ->
        val lower = line.lowercase()

        // Agar qatorda harf bo'lmasa yoki juda qisqa bo'lsa yoki blacklistda bo'lsa - o'tkazib yuboramiz
        if (line.length < 4 ||
            !line.any { it.isLetter() } ||
            blackList.any { lower.contains(it) } ||
            line.contains("bot", true)
        ) return@mapNotNull null

        var score = 0

        // 1. Qator uzunligi uchun ball (Do'kon nomlari odatda uzunroq bo'ladi)
        score += line.length

        // 2. Bir nechta so'zdan iborat bo'lsa, qo'shimcha ball
        if (line.split(" ").size >= 2) score += 20

        // 3. Agar hammasi KATTA HARFLARDA bo'lsa (GOODWILL kabi), ball qo'shamiz
        if (line == line.uppercase()) score += 15

        // 4. Agar qatorda "MCHJ", "OK", "LLC" yoki "SERVICE" kabi so'zlar bo'lsa
        if (lower.contains(Regex("mchj|ok|service|servis|business|bus ness|market"))) score += 30

        line to score
    }

    // Eng yuqori ball to'plagan qatorni qaytaramiz
    return candidates.maxByOrNull { it.second }?.first ?: "Noma'lum do'kon"
}