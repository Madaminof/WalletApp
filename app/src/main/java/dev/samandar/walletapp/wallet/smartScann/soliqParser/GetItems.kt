package dev.samandar.walletapp.wallet.smartScann.soliqParser

import dev.samandar.walletapp.wallet.smartScann.ParsedItem

fun extractAllItems(doc: org.jsoup.nodes.Document): List<ParsedItem> {
    val items = mutableListOf<ParsedItem>()
    val rows = doc.select("tr")

    for (row in rows) {
        val cells = row.select("td")

        if (cells.size >= 3) {
            val rawName = cells[0].text().trim()

            if (shouldSkipRow(rawName)) continue

            val cleanName = rawName.split("\n").first().trim()

            // 1. Soni (Miqdori)
            val qty = cells[1].text()
                .replace(",", ".")
                .replace(" ", "")
                .toDoubleOrNull() ?: 1.0

            // 2. Jami Summa (Soliq saytida 3-ustun jami summani anglatadi)
            val rowTotal = cleanAmountString(cells[2].text()) ?: 0.0

            // 3. MUHIM: Dona narxini hisoblab olamiz
            // Agar 2 ta non 8600 bo'lsa, bittasi 4300 bo'lishi kerak.
            val unitPrice = if (qty > 0) rowTotal / qty else rowTotal

            if (rowTotal > 0 && cleanName.length > 2) {
                items.add(
                    ParsedItem(
                        name = cleanName,
                        price = unitPrice, // Endi bu yerda haqiqiy dona narxi (4300.0) ketadi
                        quantity = qty     // Soni (2.0)
                    )
                )
            }
        }
    }
    return items
}


private fun shouldSkipRow(name: String): Boolean {
    val skipKeywords = listOf(
        "Nomi", "Наименование",           // Sarlavhalar
        "QQS", "НДС",                      // Soliq ma'lumotlari
        "Shtrix", "Штрих",                 // Kodlar
        "MXIK", "ИКПУ",                    // Tasniflagichlar
        "Chegirma", "Скидка",              // Chegirmalar
        "Markirovka", "Маркировка",        // Markirovka
        "Komitent", "Комитент",            // Vositachilar
        "O'lchov birligi", "Ед. изм."      // Birliklar
    )

    return name.isEmpty() || skipKeywords.any { keyword -> name.contains(keyword, ignoreCase = true) }
}