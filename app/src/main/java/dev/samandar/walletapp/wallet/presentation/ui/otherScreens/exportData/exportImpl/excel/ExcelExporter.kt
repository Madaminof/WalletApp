package dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.exportImpl.excel

import android.content.Context
import dev.samandar.walletapp.wallet.data.local.entity.TransactionEntity
import dev.samandar.walletapp.wallet.domain.model.account.Account
import dev.samandar.walletapp.wallet.domain.model.Category
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.data.DataExporter
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.data.ExportConfig
import jxl.Workbook
import jxl.WorkbookSettings
import jxl.format.Alignment
import jxl.format.Border
import jxl.format.BorderLineStyle
import jxl.format.Colour
import jxl.format.VerticalAlignment
import jxl.write.*
import jxl.write.Number
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class ExcelExporter(private val context: Context) : DataExporter {

    override fun export(
        data: List<TransactionEntity>,
        categories: List<Category>,
        accounts: List<Account>,
        config: ExportConfig
    ): File {
        val file = File(context.cacheDir, "Wallet_Analyst_Report.xls")
        val wbSettings = WorkbookSettings().apply { locale = Locale("uz", "UZ") }

        val workbook = Workbook.createWorkbook(file, wbSettings)
        val sheet = workbook.createSheet("Hisobot", 0)

        val catMap = categories.associate { it.id to it.name }
        val accMap = accounts.associate { it.id to it.name }
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

        try {
            val styles = ExcelStyles()

            // 1. Brand Header
            sheet.addCell(Label(0, 0, "WALLET ANALYST REPORT", styles.titleFormat))
            sheet.mergeCells(0, 0, 3, 0) // Faqat 4 ta ustun qoldi
            sheet.setRowView(0, 600)

            // 2. Statistics (Summary)
            val income = data.filter { it.type == "INCOME" }.sumOf { it.amount }
            val expense = data.filter { it.type == "EXPENSE" }.sumOf { it.amount }

            drawSummary(sheet, income, expense, styles)

            // 3. Table Header (Turi va Izoh olib tashlandi)
            val headers = arrayOf("SANA", "KATEGORIYA", "HAMYON", "SUMMA")
            headers.forEachIndexed { i, title ->
                sheet.addCell(Label(i, 5, title, styles.headerFormat))
                sheet.setColumnView(i, 22) // Professional kenglik
            }

            // 4. Data Rows
            data.forEachIndexed { index, tx ->
                val row = index + 6
                val isInc = tx.type == "INCOME"

                // Ma'lumotlarni yozish
                sheet.addCell(Label(0, row, dateFormat.format(Date(tx.date)), styles.normalFormat))
                sheet.addCell(Label(1, row, catMap[tx.categoryId] ?: "Boshqa", styles.normalFormat))
                sheet.addCell(Label(2, row, accMap[tx.accountId] ?: "Hamyon", styles.normalFormat))

                val amountValue = if (isInc) tx.amount else -tx.amount
                val amountStyle = if (isInc) styles.incomeFormat else styles.expenseFormat
                sheet.addCell(Number(3, row, amountValue, amountStyle))

                sheet.setRowView(row, 350) // Qatorlar orasida "havo" qoldirish
            }

            workbook.write()
        } finally {
            workbook.close()
        }

        return file
    }

    private fun drawSummary(sheet: WritableSheet, income: Double, expense: Double, styles: ExcelStyles) {
        // Stats Label
        sheet.addCell(Label(0, 2, "UMUMIY KIRIM", styles.normalBold))
        sheet.addCell(Number(1, 2, income, styles.incomeFormat))

        sheet.addCell(Label(0, 3, "UMUMIY CHIQIM", styles.normalBold))
        sheet.addCell(Number(1, 3, expense, styles.expenseFormat))

        sheet.setRowView(2, 400)
        sheet.setRowView(3, 400)
    }

    private inner class ExcelStyles {
        // Asosiy brend rangi 0xFF4759C1 ga eng yaqin: INDIGO
        private val brandColor = Colour.INDIGO

        // Fonts
        private val titleFont = WritableFont(WritableFont.ARIAL, 16, WritableFont.BOLD).apply { colour = brandColor }
        private val headerFont = WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD).apply { colour = Colour.WHITE }
        private val normalFont = WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD)
        private val boldFont = WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD)

        private val incomeFont = WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD).apply { colour = Colour.GREEN }
        private val expenseFont = WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD).apply { colour = Colour.RED }

        private val numFormat = NumberFormat("#,##0 UZS")

        // Formats
        val titleFormat = WritableCellFormat(titleFont).apply {
            setAlignment(Alignment.CENTRE)
            setVerticalAlignment(VerticalAlignment.CENTRE)
        }

        val headerFormat = WritableCellFormat(headerFont).apply {
            setBackground(brandColor) // Asosiy rang fonda
            setAlignment(Alignment.CENTRE)
            setVerticalAlignment(VerticalAlignment.CENTRE)
            setBorder(Border.ALL, BorderLineStyle.THIN, Colour.INDIGO)
        }

        val normalFormat = WritableCellFormat(normalFont).apply {
            setAlignment(Alignment.CENTRE)
            setVerticalAlignment(VerticalAlignment.CENTRE)
            setBorder(Border.BOTTOM, BorderLineStyle.THIN, Colour.GRAY_25)
        }

        val normalBold = WritableCellFormat(boldFont).apply {
            setAlignment(Alignment.LEFT)
            setVerticalAlignment(VerticalAlignment.CENTRE)
        }

        val incomeFormat = WritableCellFormat(incomeFont, numFormat).apply {
            setAlignment(Alignment.RIGHT)
            setVerticalAlignment(VerticalAlignment.CENTRE)
        }

        val expenseFormat = WritableCellFormat(expenseFont, numFormat).apply {
            setAlignment(Alignment.RIGHT)
            setVerticalAlignment(VerticalAlignment.CENTRE)
        }

        val balanceFormat = WritableCellFormat(boldFont, numFormat).apply {
            setAlignment(Alignment.RIGHT)
            setVerticalAlignment(VerticalAlignment.CENTRE)
            setBackground(Colour.VERY_LIGHT_YELLOW)
        }
    }
}