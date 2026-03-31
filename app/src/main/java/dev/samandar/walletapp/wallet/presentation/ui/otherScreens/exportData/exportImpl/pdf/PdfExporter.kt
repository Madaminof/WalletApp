package dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.exportImpl.pdf

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.data.local.entity.TransactionEntity
import dev.samandar.walletapp.wallet.domain.model.account.Account
import dev.samandar.walletapp.wallet.domain.model.Category
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.data.DataExporter
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.data.ExportConfig
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.exportImpl.pdf.PdfTheme
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PdfExporter(private val context: Context) : DataExporter {
    private val rowPaint = Paint()

    override fun export(data: List<TransactionEntity>, categories: List<Category>, accounts: List<Account>, config: ExportConfig): File {
        val file = File(context.cacheDir, "Wallet_Analyst_Report.pdf")
        val pdfDocument = PdfDocument()
        val drawer = PdfContentDrawer()

        val appIcon = BitmapFactory.decodeResource(context.resources, R.drawable.ic_wallet_2)

        val categoryMap = categories.associate { it.id to it.name }
        val accountMap = accounts.associate { it.id to it.name }

        // ANIQ KOORDINATALAR (X-axis)
        val cols = floatArrayOf(55f, 135f, 315f, 540f)

        try {
            var pageNum = 1
            var page = startNewPage(pdfDocument, pageNum)
            var yPos = 70f

            // 1-sahifa Header & Summary
            yPos = drawer.drawMainHeader(page.canvas,appIcon, yPos)
            val income = data.filter { it.type == "INCOME" }.sumOf { it.amount }
            val expense = data.filter { it.type == "EXPENSE" }.sumOf { it.amount }
            yPos = drawer.drawSummaryCards(page.canvas, income, expense, yPos)

            drawer.drawTableHeader(page.canvas, cols, yPos)
            yPos += 40f

            data.forEach { tx ->
                if (yPos > 770f) {
                    finalizePage(page, drawer, pageNum, pdfDocument)
                    pageNum++
                    page = startNewPage(pdfDocument, pageNum)
                    yPos = 50f
                    drawer.drawTableHeader(page.canvas, cols, yPos)
                    yPos += 40f
                }

                drawTransactionRow(page.canvas, tx, categoryMap, accountMap, cols, yPos)
                yPos += 30f
            }

            finalizePage(page, drawer, pageNum, pdfDocument)
            pdfDocument.writeTo(FileOutputStream(file))
        } finally {
            pdfDocument.close()
        }
        return file
    }

    private fun startNewPage(pdfDocument: PdfDocument, pageNum: Int): PdfDocument.Page {
        val pageInfo = PdfDocument.PageInfo.Builder(PdfTheme.PAGE_WIDTH, PdfTheme.PAGE_HEIGHT, pageNum).create()
        return pdfDocument.startPage(pageInfo)
    }

    private fun finalizePage(
        page: PdfDocument.Page,
        drawer: PdfContentDrawer,
        pageNum: Int,
        pdfDocument: PdfDocument
    ) {
        drawer.drawFooterWithLink(page.canvas, pageNum)

        pdfDocument.finishPage(page)
    }

    private fun drawTransactionRow(canvas: Canvas, tx: TransactionEntity, catMap: Map<String, String>, accMap: Map<String, String>, cols: FloatArray, y: Float) {
        rowPaint.reset()
        rowPaint.isAntiAlias = true
        rowPaint.textSize = 9f

        // 1. Sana
        rowPaint.color = PdfTheme.textSecondary
        canvas.drawText(SimpleDateFormat("dd.MM.yy", Locale.getDefault()).format(Date(tx.date)), cols[0], y, rowPaint)

        // 2. Kategoriya
        rowPaint.color = PdfTheme.textMain
        canvas.drawText(catMap[tx.categoryId]?.take(20) ?: "Boshqa", cols[1], y, rowPaint)

        // 3. Hamyon
        rowPaint.color = PdfTheme.textSecondary
        canvas.drawText(accMap[tx.accountId]?.take(15) ?: "Hamyon", cols[2], y, rowPaint)

        // 4. Summa (Align.RIGHT orqali cols[3] nuqtasiga mahkamlanadi)
        val isInc = tx.type == "INCOME"
        rowPaint.color = if (isInc) PdfTheme.incomeColor else PdfTheme.expenseColor
        rowPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        rowPaint.textAlign = Paint.Align.RIGHT
        canvas.drawText("${if (isInc) "+" else "-"}%,.0f".format(tx.amount), cols[3], y, rowPaint)

        rowPaint.textAlign = Paint.Align.LEFT

        // Separator Line
        rowPaint.reset()
        rowPaint.color = PdfTheme.borderLight
        rowPaint.strokeWidth = 0.5f
        canvas.drawLine(45f, y + 10f, 550f, y + 10f, rowPaint)
    }
}