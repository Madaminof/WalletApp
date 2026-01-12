package dev.samandar.walletapp.wallet.presentation.ui.home.cardStatistics

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.ui.theme.expenseColor
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.presentation.viewmodel.CategoryData
import java.text.DecimalFormat

@Composable
fun PremiumDoughnutChart(
    data: List<CategoryData>,
    totalAmount: Double,
    modifier: Modifier = Modifier,
    chartThickness: Dp = 18.dp
) {
    val density = LocalDensity.current
    val totalLabel = stringResource(Strings.total_expense).uppercase()
    val formatter = remember { DecimalFormat("#,###") }
    val formattedTotal = formatter.format(totalAmount)

    val dynamicFontSize = when {
        formattedTotal.length > 12 -> 13.sp
        formattedTotal.length > 9 -> 15.sp
        else -> 16.sp
    }

    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(data, totalAmount) {
        animatedProgress.animateTo(1f, spring(0.75f, Spring.StiffnessLow))
    }

    val colorAmount = expenseColor.toArgb()
    val colorLabel = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.5f).toArgb()

    Box(
        modifier = modifier.aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            val canvasSize = size
            val thicknessPx = chartThickness.toPx()
            val diameter = minOf(canvasSize.width, canvasSize.height)
            val rectSize = Size(diameter - thicknessPx, diameter - thicknessPx)
            val topLeft = Offset((canvasSize.width - rectSize.width) / 2, (canvasSize.height - rectSize.height) / 2)

            drawCircle(
                color = Color.Gray.copy(alpha = 0.08f),
                radius = rectSize.width / 2,
                center = center,
                style = Stroke(width = thicknessPx)
            )

            var startAngle = -90f
            data.forEach { item ->
                val sweepAngle = (item.amount / totalAmount).toFloat() * 360f
                drawArc(
                    color = item.color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle * animatedProgress.value,
                    useCenter = false,
                    topLeft = topLeft,
                    size = rectSize,
                    style = Stroke(width = thicknessPx, cap = StrokeCap.Butt)
                )
                startAngle += sweepAngle
            }

            drawIntoCanvas { canvas ->
                val paintAmount = Paint().apply {
                    color = colorAmount
                    textSize = with(density) { dynamicFontSize.toPx() }
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    textAlign = Paint.Align.CENTER
                    isAntiAlias = true
                }

                val paintLabel = Paint().apply {
                    color = colorLabel
                    textSize = with(density) { 9.sp.toPx() }
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    textAlign = Paint.Align.CENTER
                    isAntiAlias = true
                }

                val amountMetrics = paintAmount.fontMetrics
                val labelMetrics = paintLabel.fontMetrics

                val spacing = with(density) { 2.dp.toPx() }

                val totalBlockHeight = (labelMetrics.descent - labelMetrics.ascent) + spacing + (amountMetrics.descent - amountMetrics.ascent)

                val startY = center.y - (totalBlockHeight / 2)

                val labelY = startY - labelMetrics.ascent
                canvas.nativeCanvas.drawText(totalLabel, center.x, labelY, paintLabel)

                val amountY = labelY + labelMetrics.descent + spacing - amountMetrics.ascent
                canvas.nativeCanvas.drawText(formattedTotal, center.x, amountY, paintAmount)
            }
        }
    }
}