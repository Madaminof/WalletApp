package com.example.walletapp.domain.parser

import com.example.walletapp.wallet.domain.model.*
import com.example.walletapp.wallet.presentation.ui.home.addTransaction.addVoiceTransaction.ParsedTransaction
import java.util.*

/**
 * Ovozdan kelgan matnni tahlil qilib, ParsedTransaction obyektini yaratadi.
 */
object VoiceInputParser {

    fun parse(
        text: String,
        allCategories: List<Category>,
        allAccounts: List<Account>
    ): ParsedTransaction {
        val lowerText = text.lowercase(Locale.ROOT)

        var parsedAmount = 0.0
        var parsedCategory: Category? = null
        var parsedAccount: Account? = null
        var parsedType: TransactionType? = null
        var remainingNote = lowerText

        // 1. Summani aniqlash
        val amountMatch = Regex("""\d+([.,]\d+)?""").find(lowerText)
        if (amountMatch != null) {
            parsedAmount = amountMatch.value.replace(',', '.').toDoubleOrNull() ?: 0.0
            remainingNote = remainingNote.replace(amountMatch.value, "").trim()
        }

        // 2. Turini aniqlash (Xarajat/Daromad)
        if (lowerText.contains("daromad") || lowerText.contains("tushum")) {
            parsedType = TransactionType.INCOME
        } else if (lowerText.contains("xarajat") || lowerText.contains("sarf")) {
            parsedType = TransactionType.EXPENSE
        }

        // 3. Kategoriyani aniqlash
        parsedCategory = allCategories.firstOrNull { category ->
            lowerText.contains(category.name.lowercase(Locale.ROOT))
        }
        if (parsedCategory != null) {
            remainingNote = remainingNote.replace(parsedCategory.name.lowercase(), "").trim()

            // Agar tur aniqlanmagan bo'lsa, uni kategoriyadan olish
            if (parsedType == null) {
                parsedType = parsedCategory.type
            }
        }

        // 4. Hisobni aniqlash
        parsedAccount = allAccounts.firstOrNull { account ->
            lowerText.contains(account.name.lowercase(Locale.ROOT))
        }
        if (parsedAccount != null) {
            remainingNote = remainingNote.replace(parsedAccount.name.lowercase(), "").trim()
        }

        // 5. Eslatma (Note)
        val note = remainingNote
            .replace(Regex("""so[']?m|uzs|sum|ming|million"""), "")
            .trim()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }

        return ParsedTransaction(
            amount = parsedAmount,
            type = parsedType,
            category = parsedCategory,
            account = parsedAccount,
            note = note.takeIf { it.isNotEmpty() }
        )
    }
}