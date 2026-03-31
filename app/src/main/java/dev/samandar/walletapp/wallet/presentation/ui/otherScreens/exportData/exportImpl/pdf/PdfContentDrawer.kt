package dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.exportImpl.pdf

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.exportImpl.pdf.PdfTheme

class PdfContentDrawer {
    private val paint = Paint()

    /**
     * Header: Icon + App Name + Slogan
     */
    fun drawMainHeader(canvas: Canvas, icon: Bitmap?, yPos: Float): Float {
        paint.reset()
        paint.isAntiAlias = true

        var currentX = 45f

        // 1. Ilova ikonkasini chizish
        icon?.let {
            val iconSize = 35f
            val rect = RectF(currentX, yPos - 25f, currentX + iconSize, yPos + 10f)
            canvas.drawBitmap(it, null, rect, paint)
            currentX += iconSize + 12f // Ikonkadan keyin matn uchun joy tashlash
        }

        // 2. Ilova nomi
        paint.textSize = 22f
        paint.typeface = Typeface.create("sans-serif", Typeface.BOLD)
        paint.color = PdfTheme.primaryColor
        canvas.drawText("WALLET ANALYST", currentX, yPos, paint)

        // 3. Slogan
        paint.color = PdfTheme.textSecondary
        paint.textSize = 8.5f
        paint.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
        canvas.drawText(PdfTheme.APP_SLOGAN.uppercase(), currentX, yPos + 14f, paint)

        // 4. O'ng tomonda dekorativ brend chizig'i
        paint.color = PdfTheme.primaryColor
        canvas.drawRect(545f, yPos - 30f, 550f, yPos + 15f, paint)

        return yPos + 75f
    }

    /**
     * Summary Cards: Kirim va Chiqim bloklari
     */
    fun drawSummaryCards(canvas: Canvas, income: Double, expense: Double, yPos: Float): Float {
        val w = 248f
        drawCard(canvas, 45f, yPos, w, "JAMI KIRIM", income, PdfTheme.incomeColor)
        drawCard(canvas, 302f, yPos, w, "JAMI CHIQIM", expense, PdfTheme.expenseColor)
        return yPos + 100f
    }

    private fun drawCard(canvas: Canvas, x: Float, y: Float, w: Float, label: String, amount: Double, color: Int) {
        val rect = RectF(x, y, x + w, y + 65f)
        paint.reset()
        paint.isAntiAlias = true
        paint.color = PdfTheme.primaryLight
        canvas.drawRoundRect(rect, 12f, 12f, paint)

        paint.color = PdfTheme.textSecondary
        paint.textSize = 8f
        canvas.drawText(label, x + 15f, y + 22f, paint)

        paint.color = color
        paint.textSize = 15f
        paint.typeface = Typeface.create("sans-serif", Typeface.BOLD)
        canvas.drawText("%,.0f UZS".format(amount), x + 15f, y + 48f, paint)
    }

    /**
     * Jadval Sarlavhasi: Ustunlar bilan 100% mos
     */
    fun drawTableHeader(canvas: Canvas, cols: FloatArray, yPos: Float) {
        paint.reset()
        paint.isAntiAlias = true
        paint.color = PdfTheme.primaryColor
        canvas.drawRoundRect(RectF(45f, yPos - 20f, 550f, yPos + 10f), 4f, 4f, paint)

        paint.color = Color.WHITE
        paint.textSize = 10f
        paint.typeface = Typeface.create("sans-serif", Typeface.BOLD)

        canvas.drawText("SANA", cols[0], yPos, paint)
        canvas.drawText("KATEGORIYA", cols[1], yPos, paint)
        canvas.drawText("HAMYON", cols[2], yPos, paint)

        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("SUMMA", cols[3], yPos, paint)
        paint.textAlign = Paint.Align.LEFT
    }

    /**
     * Footer: Bosiladigan Play Market Linki
     */
    fun drawFooterWithLink(canvas: Canvas, pageNum: Int) {
        val y = PdfTheme.PAGE_HEIGHT - 60f
        paint.reset()
        paint.isAntiAlias = true

        // Ajratuvchi chiziq
        paint.color = PdfTheme.borderLight
        canvas.drawLine(45f, y, 550f, y, paint)

        // Label
        paint.color = PdfTheme.textSecondary
        paint.textSize = 8f
        val label = "Ilovani yuklab olish: "
        canvas.drawText(label, 45f, y + 25f, paint)

        // Haqiqiy URL
        val startX = 45f + paint.measureText(label)
        paint.color = Color.parseColor("#1A73E8") // Google Blue
        paint.isUnderlineText = true
        canvas.drawText(PdfTheme.STORE_URL, startX, y + 25f, paint)

        // Sahifa raqami
        paint.isUnderlineText = false
        paint.color = PdfTheme.textSecondary
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("Sahifa $pageNum", 550f, y + 25f, paint)
        paint.textAlign = Paint.Align.LEFT
    }
}