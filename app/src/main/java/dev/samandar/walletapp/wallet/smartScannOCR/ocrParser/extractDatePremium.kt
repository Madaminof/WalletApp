package dev.samandar.walletapp.wallet.smartScannOCR.ocrParser

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Locale

fun extractDatePremium(lines: List<String>): Long {
    // 1. Log orqali ko'rdikki, vaqt orasida bo'shliq bo'lishi mumkin: "17: 54: 27"
    // Shuning uchun regexni o'ta yumshoq qilamiz
    val dateTimeRegex = Regex(
        """(\d{2}[./]\d{2}[./]\d{2,4})[\s:]+([\d\s:]+)""",
        RegexOption.IGNORE_CASE
    )

    for (line in lines) {
        // Line[34]: Sana: 05/02/26 17: 54:27
        val match = dateTimeRegex.find(line)

        if (match != null) {
            try {
                val datePart = match.groupValues[1].replace("/", ".")

                // Vaqt qismidagi barcha bo'shliqlarni olib tashlaymiz: "17: 54: 27" -> "17:54:27"
                val rawTimePart = match.groupValues[2].replace(" ", "")

                // Faqat vaqt formatiga mos qismini qirqib olamiz (masalan: 17:54:27)
                val timePartMatch = Regex("""\d{2}:\d{2}(?::\d{2})?""").find(rawTimePart)
                val timePart = timePartMatch?.value ?: "00:00:00"

                val datePattern = if (datePart.substringAfterLast(".").length == 2) "dd.MM.yy" else "dd.MM.yyyy"
                val timePattern = if (timePart.count { it == ':' } == 2) "HH:mm:ss" else "HH:mm"

                val sdf = SimpleDateFormat("$datePattern $timePattern", Locale.US).apply {
                    isLenient = false
                }

                val finalDateTime = "$datePart $timePart"
                val parsedDate = sdf.parse(finalDateTime)

                if (parsedDate != null) {
                    Log.d("DEBUG_OCR", "✅ TOPILDI: $finalDateTime")
                    return parsedDate.time
                }
            } catch (e: Exception) {
                Log.e("DEBUG_OCR", "❌ Error parsing line: $line", e)
            }
        }
    }

    Log.e("DEBUG_OCR", "⚠️ Sana va vaqt topilmadi, hozirgi vaqt qaytarildi.")
    return System.currentTimeMillis()
}