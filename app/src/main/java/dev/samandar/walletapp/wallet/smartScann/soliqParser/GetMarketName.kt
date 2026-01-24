package dev.samandar.walletapp.wallet.smartScann.soliqParser


fun extractMerchantName(doc: org.jsoup.nodes.Document): String {
    return try {
        val merchantElement = doc.select("p, h4, h5, b, span")
            .asIterable()
            .firstOrNull { element ->
                val text = element.text()
                text.contains("\"") &&
                        (text.contains("MCHJ", true) ||
                                text.contains("XK", true) ||
                                text.contains("ООО", true))
            }

        if (merchantElement != null) {
            val text = merchantElement.text()
            val match = Regex("""".*?"""").find(text)
            if (match != null) {
                return match.value.replace("\"", "").trim()
            }
        }
        val pattern = java.util.regex.Pattern.compile("MCHJ|XK|OK|ООО|СП", java.util.regex.Pattern.CASE_INSENSITIVE)
        val legalNameElement = doc.getElementsMatchingOwnText(pattern).firstOrNull()

        if (legalNameElement != null) {
            return legalNameElement.text()
                .replace("Savdo cheki/Sotuv", "", ignoreCase = true)
                .replace(Regex("""\d{10,13}"""), "") // STIR yoki boshqa uzun raqamlarni tozalash
                .trim()
        }

        "Soliq Cheki"
    } catch (e: Exception) {
        "Soliq Cheki"
    }
}