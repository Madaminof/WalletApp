package dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.exportImpl.csv

import android.content.Context
import dev.samandar.walletapp.wallet.data.local.entity.TransactionEntity
import dev.samandar.walletapp.wallet.domain.model.account.Account
import dev.samandar.walletapp.wallet.domain.model.Category
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.data.DataExporter
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.data.ExportConfig
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*

class CsvExporter(private val context: Context) : DataExporter {

    override fun export(
        data: List<TransactionEntity>,
        categories: List<Category>,
        accounts: List<Account>,
        config: ExportConfig
    ): File {
        val fileName = "Wallet_Analyst_Report_${System.currentTimeMillis()}.csv"
        val file = File(context.cacheDir, fileName)

        val catMap = categories.associate { it.id to it.name }
        val accMap = accounts.associate { it.id to it.name }
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

        // Markaziy Osiyo va Yevropa Excel standartlari uchun nuqtali vergul eng yaxshisi
        val delimiter = ";"

        // Faylga oqim (Stream) orqali yozish - bu StringBuilder ga qaraganda xotirani tejaydi
        FileOutputStream(file).use { fos ->
            // 1. UTF-8 BOM qo'shish (Excel o'zbekcha harflarni tanishi uchun)
            fos.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))

            BufferedWriter(OutputStreamWriter(fos, Charsets.UTF_8)).use { writer ->

                // 2. Meta Header (Premium branding)
                writer.write("HISOBOT${delimiter}WALLET ANALYST")
                writer.newLine()
                writer.write("SANA${delimiter}${dateFormat.format(Date())}")
                writer.newLine()
                writer.newLine()

                // 3. Summary (Statistika)
                val income = data.filter { it.type == "INCOME" }.sumOf { it.amount }
                val expense = data.filter { it.type == "EXPENSE" }.sumOf { it.amount }

                writer.write("STATISTIKA")
                writer.newLine()
                writer.write("Jami Kirim${delimiter}${income.toLong()} UZS")
                writer.newLine()
                writer.write("Jami Chiqim${delimiter}${expense.toLong()} UZS")
                writer.newLine()
                writer.write("Sof Balans${delimiter}${(income - expense).toLong()} UZS")
                writer.newLine()
                writer.newLine()

                // 4. Table Header
                val headers = arrayOf("SANA", "KATEGORIYA", "HAMYON", "SUMMA")
                writer.write(headers.joinToString(delimiter))
                writer.newLine()

                // 5. Data Rows
                data.forEach { tx ->
                    val isInc = tx.type == "INCOME"
                    val amount = if (isInc) tx.amount else -tx.amount

                    val row = listOf(
                        dateFormat.format(Date(tx.date)),
                        catMap[tx.categoryId] ?: "Boshqa",
                        accMap[tx.accountId] ?: "Hamyon",
                        amount.toLong().toString()
                    ).joinToString(delimiter) { it.escapeCsv(delimiter) } // Maxsus belgilardan himoya

                    writer.write(row)
                    writer.newLine()
                }

                writer.flush()
            }
        }

        return file
    }

    /**
     * CSV standarti bo'yicha ma'lumotni tozalash.
     * Agar matn ichida separator yoki qo'shtirnoq bo'lsa, uni formatlab beradi.
     */
    private fun String.escapeCsv(delimiter: String): String {
        return if (this.contains(delimiter) || this.contains("\"") || this.contains("\n")) {
            "\"" + this.replace("\"", "\"\"") + "\""
        } else {
            this
        }
    }
}