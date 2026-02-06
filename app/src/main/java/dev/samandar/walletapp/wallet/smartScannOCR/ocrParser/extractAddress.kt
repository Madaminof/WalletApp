package dev.samandar.walletapp.wallet.smartScannOCR.ocrParser

fun extractAddress(lines: List<String>): String {
    // Manzilni aniqlash uchun kengaytirilgan kalit so'zlar
    val addressStartKeys = listOf(
        "tashkent", "toshkent", "olmazor", "sh.", "ko'ch", "tuman",
        "m-f-y", "mfy", "mahalla", "viloyati"
    )

    // Manzil qismlarini yig'ish uchun
    val addressParts = mutableListOf<String>()
    var startFoundIndex = -1

    // 1. Manzil boshlanadigan qatorni topamiz
    for (index in lines.indices) {
        val line = lines[index]
        if (addressStartKeys.any { line.contains(it, true) }) {
            startFoundIndex = index
            addressParts.add(line)
            break
        }
    }

    // 2. Agar boshlang'ich qator topilsa, undan keyingi 1 ta qatorni ham tekshiramiz
    // (Chunki manzil ko'pincha 2 qatorga bo'linadi)
    if (startFoundIndex != -1 && startFoundIndex + 1 < lines.size) {
        val nextLine = lines[startFoundIndex + 1]

        // Agar keyingi qatorda raqamlar (Terminal ID kabi) yoki ajratuvchi chiziqlar bo'lmasa,
        // uni manzilning davomi deb hisoblaymiz
        if (!nextLine.contains(Regex("ID|Merch|---|Smena|Chek", RegexOption.IGNORE_CASE)) &&
            nextLine.any { it.isLetter() }) {
            addressParts.add(nextLine)
        }
    }

    // 3. Tozalash va birlashtirish
    return addressParts.joinToString(", ")
        .replace(Regex(",\\s*,"), ",") // Dublikat vergullarni tozalash
        .trim()
}