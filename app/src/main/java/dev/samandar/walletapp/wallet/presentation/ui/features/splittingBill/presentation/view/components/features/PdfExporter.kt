package dev.samandar.walletapp.wallet.presentation.ui.features.splittingBill.presentation.view.components.features

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Wallet Analyst - Minimalist & Jiddiy Premium PDF Exporter.
 * Dizayn: Minimalist, Corporate style, Apple-inspired whitespace.
 */
object PdfExporter {

    private const val TAG = "PdfExporter"
    private const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=dev.samandar.walletapp"

    fun exportToPdf(context: Context, summary: Any?, billDate: Long) {
        val activity = findActivity(context) ?: return
        if (summary == null) return

        val pdfDocument = PdfDocument()
        val s = CorporateStyles()

        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        try {
            val margin = 60f
            val pageWidth = 595f
            var currentY = 80f
            val locale = Locale.US

            // --- 1. HEADER (JIDDIY VA TARTIBLI) ---
            canvas.drawText("WALLET ANALYST", margin, currentY, s.brandPrimary)

            val dateStr = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date(billDate))
            canvas.drawText(dateStr, pageWidth - margin - s.dateText.measureText(dateStr), currentY, s.dateText)

            currentY += 15f
            canvas.drawLine(margin, currentY, pageWidth - margin, currentY, s.lineThin)

            // --- 2. DOCUMENT TITLE ---
            currentY += 60f
            val title = extractStringField(summary, "title")?.ifBlank { "XARAJATLAR HISOBOTI" } ?: "XARAJATLAR HISOBOTI"
            canvas.drawText(title.uppercase(), margin, currentY, s.h1)

            // --- 3. DATA TABLE (MINIMALIST) ---
            currentY += 40f
            // Table Header Labels
            canvas.drawText("ISHTIROKCHI", margin, currentY, s.tableLabel)
            val amountLabel = "TO'LOV MIQDORI"
            canvas.drawText(amountLabel, pageWidth - margin - s.tableLabel.measureText(amountLabel), currentY, s.tableLabel)

            currentY += 12f
            canvas.drawLine(margin, currentY, pageWidth - margin, currentY, s.lineHeavy)

            val results = extractListField(summary, "individualResults")
            results.forEachIndexed { index, item ->
                currentY += 35f
                if (currentY > 680f) return@forEachIndexed

                val name = extractStringField(item, "participantName") ?: "Ishtirokchi"
                val amount = extractDoubleField(item, "totalToPay") ?: 0.0

                // No Zebra, just clean lines if needed or pure whitespace
                canvas.drawText("${index + 1}. $name", margin, currentY, s.bodyText)

                val amountStr = String.format(locale, "%,.0f UZS", amount)
                canvas.drawText(amountStr, pageWidth - margin - s.bodyBold.measureText(amountStr), currentY, s.bodyBold)

                // Har bir qatordan keyin o'ta och chiziq (tartib uchun)
                currentY += 10f
                canvas.drawLine(margin, currentY, pageWidth - margin, currentY, s.lineUltraThin)
            }

            // --- 4. CALCULATION SUMMARY (RIGHT ALIGNED) ---
            currentY = 710f
            val summaryStartX = pageWidth * 0.55f

            // Subtotal
            val subTotal = results.sumOf { extractDoubleField(it, "itemsSum") ?: 0.0 }
            drawSummaryRow(canvas, "Oraliq jami:", subTotal, currentY, margin, pageWidth, summaryStartX, s, false)

            // Service
            val sPercent = extractDoubleField(summary, "serviceChargePercent") ?: 0.0
            if (sPercent > 0) {
                currentY += 22f
                val sAmount = results.sumOf { extractDoubleField(it, "serviceCharge") ?: 0.0 }
                drawSummaryRow(canvas, "Xizmat haqi (${sPercent.toInt()}%):", sAmount, currentY, margin, pageWidth, summaryStartX, s, false)
            }

            // Tax
            val tPercent = extractDoubleField(summary, "taxPercent") ?: 0.0
            if (tPercent > 0) {
                currentY += 22f
                val tAmount = results.sumOf { extractDoubleField(it, "tax") ?: 0.0 }
                drawSummaryRow(canvas, "Soliq (${tPercent.toInt()}%):", tAmount, currentY, margin, pageWidth, summaryStartX, s, false)
            }

            // TOTAL (BOLD & CLEAN)
            currentY += 35f
            canvas.drawLine(summaryStartX, currentY - 20f, pageWidth - margin, currentY - 20f, s.lineThin)

            val total = extractDoubleField(summary, "totalAmount") ?: 0.0
            drawSummaryRow(canvas, "UMUMIY JAMI:", total, currentY, margin, pageWidth, summaryStartX, s, true)

            // --- 5. FOOTER & LINK ---
            val footerY = 800f
            canvas.drawLine(margin, footerY - 20f, pageWidth - margin, footerY - 20f, s.lineUltraThin)

            val footerMsg = "Ushbu hisobot Wallet Analyst ilovasida shakllantirildi"
            canvas.drawText(footerMsg, margin, footerY, s.footerText)

            val linkText = "Ilovani yuklab olish: bit.ly/wallet-analyst"
            canvas.drawText(linkText, margin, footerY + 15f, s.footerLink)

            pdfDocument.finishPage(page)
            saveAndSharePdf(context, pdfDocument)

        } catch (e: Exception) {
            Log.e(TAG, "PDF Error: ${e.message}")
        } finally {
            pdfDocument.close()
        }
    }

    private fun drawSummaryRow(canvas: Canvas, label: String, value: Double, y: Float, margin: Float, pageWidth: Float, startX: Float, s: CorporateStyles, isTotal: Boolean) {
        val paintLabel = if (isTotal) s.bodyBold else s.bodySecondary
        val paintValue = if (isTotal) s.bodyTotal else s.bodyBold

        canvas.drawText(label, startX, y, paintLabel)
        val valStr = String.format(Locale.US, "%,.0f UZS", value)
        canvas.drawText(valStr, pageWidth - margin - paintValue.measureText(valStr), y, paintValue)
    }

    private fun saveAndSharePdf(context: Context, pdfDocument: PdfDocument) {
        try {
            val file = File(context.cacheDir, "Wallet_Analyst_Report.pdf")
            FileOutputStream(file).use { pdfDocument.writeTo(it) }
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Hisobotni ulashish"))
        } catch (e: Exception) { e.printStackTrace() }
    }

    // --- Helpers ---
    private fun extractListField(obj: Any?, n: String) = try {
        val f = obj?.javaClass?.getDeclaredField(n)?.apply { isAccessible = true }
        f?.get(obj) as? List<*> ?: emptyList<Any>()
    } catch (e: Exception) { emptyList<Any>() }

    private fun extractStringField(obj: Any?, n: String) = try {
        val f = obj?.javaClass?.getDeclaredField(n)?.apply { isAccessible = true }
        f?.get(obj) as? String
    } catch (e: Exception) { null }

    private fun extractDoubleField(obj: Any?, n: String) = try {
        val f = obj?.javaClass?.getDeclaredField(n)?.apply { isAccessible = true }
        (f?.get(obj) as? Number)?.toDouble()
    } catch (e: Exception) { null }

    private fun findActivity(context: Context): Activity? {
        var c = context
        while (c is ContextWrapper) { if (c is Activity) return c; c = c.baseContext }
        return null
    }

    private class CorporateStyles {
        val black = Color.BLACK
        val darkGray = Color.parseColor("#333333")
        val lightGray = Color.parseColor("#777777")
        val silver = Color.parseColor("#E0E0E0")
        val ultraLight = Color.parseColor("#F2F2F2")

        val brandPrimary = Paint().apply { textSize = 16f; color = black; typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD); isAntiAlias = true }
        val dateText = Paint().apply { textSize = 10f; color = lightGray; typeface = Typeface.SANS_SERIF; isAntiAlias = true }

        val h1 = Paint().apply { textSize = 22f; color = black; typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD); isAntiAlias = true }
        val tableLabel = Paint().apply { textSize = 10f; color = lightGray; typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD); isAntiAlias = true }

        val bodyText = Paint().apply { textSize = 12f; color = darkGray; typeface = Typeface.SANS_SERIF; isAntiAlias = true }
        val bodySecondary = Paint().apply { textSize = 12f; color = lightGray; isAntiAlias = true }
        val bodyBold = Paint().apply { textSize = 12f; color = black; typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD); isAntiAlias = true }
        val bodyTotal = Paint().apply { textSize = 14f; color = black; typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD); isAntiAlias = true }

        val footerText = Paint().apply { textSize = 9f; color = lightGray; isAntiAlias = true }
        val footerLink = Paint().apply { textSize = 10f; color = Color.parseColor("#007AFF"); typeface = Typeface.DEFAULT_BOLD; isAntiAlias = true }

        val lineHeavy = Paint().apply { color = black; strokeWidth = 1.5f }
        val lineThin = Paint().apply { color = silver; strokeWidth = 1f }
        val lineUltraThin = Paint().apply { color = ultraLight; strokeWidth = 0.5f }
    }
}