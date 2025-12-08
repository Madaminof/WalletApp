package com.example.walletapp.wallet.presentation.ui.home.cardStatistics

import android.graphics.Paint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.ui.theme.expenseColor
import com.example.walletapp.wallet.presentation.viewmodel.CategoryData
import java.text.DecimalFormat
import kotlin.math.min


@Composable
fun PremiumDoughnutChart(
    data: List<CategoryData>,
    totalAmount: Double,
    modifier: Modifier = Modifier,
    chartThickness: Dp = 30.dp,
    animationDuration: Int = 500,
    centerLabel: String = "JAMI XARAJAT"
) {
    val totalAngle = 360f
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(data) {
        animatedProgress.animateTo(
            1f,
            animationSpec = tween(animationDuration, easing = FastOutSlowInEasing)
        )
    }

    val formatter = remember { DecimalFormat("#,###") }
    val density = LocalDensity.current
    val formattedTotal = formatter.format(totalAmount)

    val balanceTextColor = expenseColor.toArgb()
    val labelTextColor = MaterialTheme.colorScheme.onTertiary.copy(0.7f).toArgb()

    Box(modifier = modifier.aspectRatio(1f), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val size = this.size
            val thicknessPx = chartThickness.toPx()
            val chartSize = min(size.width, size.height)
            val center = Offset(size.width / 2f, size.height / 2f)
            val chartRadius = chartSize / 2f - thicknessPx / 2

            val rect = Rect(
                topLeft = Offset(thicknessPx / 2, thicknessPx / 2),
                bottomRight = Offset(chartSize - thicknessPx / 2, chartSize - thicknessPx / 2)
            )

            var startAngle = 270f

            data.forEach { item ->
                val sweepAngle = (item.amount / totalAmount).toFloat() * totalAngle
                val animatedSweep = sweepAngle * animatedProgress.value

                drawArc(
                    color = item.color,
                    startAngle = startAngle,
                    sweepAngle = animatedSweep,
                    useCenter = false,
                    topLeft = rect.topLeft,
                    size = rect.size,
                    style = Stroke(width = thicknessPx, cap = StrokeCap.Butt)
                )
                startAngle += sweepAngle
            }

            drawIntoCanvas {

                val balanceTextPaint = Paint().apply {
                    isAntiAlias = true
                    textAlign = Paint.Align.CENTER
                    color = balanceTextColor
                }

                val maxTextWidth = chartRadius * 1.5f
                var currentTextSizeSpValue = 20f
                val minTextSizeSpValue = 16f
                var textSizePx = with(density) { currentTextSizeSpValue.sp.toPx() }

                while (currentTextSizeSpValue > minTextSizeSpValue) {
                    balanceTextPaint.textSize = textSizePx
                    val measuredWidth = balanceTextPaint.measureText(formattedTotal)
                    if (measuredWidth < maxTextWidth) { break }
                    currentTextSizeSpValue -= 2f
                    textSizePx = with(density) { currentTextSizeSpValue.sp.toPx() }
                }

                it.nativeCanvas.drawText(
                    formattedTotal,
                    center.x,
                    center.y - with(density) { 0.dp.toPx() },
                    balanceTextPaint
                )

                val labelTextPaint = Paint().apply {
                    isAntiAlias = true
                    textAlign = Paint.Align.CENTER
                    color = labelTextColor
                    textSize = with(density) { 12.sp.toPx() }
                }
                it.nativeCanvas.drawText(
                    centerLabel,
                    center.x,
                    center.y + with(density) { 18.dp.toPx() },
                    labelTextPaint
                )
            }
        }
    }
}
