package dev.samandar.walletapp.wallet.presentation.ui.home.totalBalanceCardPremium

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.ui.theme.expenseColor
import dev.samandar.walletapp.ui.theme.incomeColor
import dev.samandar.walletapp.wallet.presentation.ui.home.totalBalanceCard.ChartPoint
import dev.samandar.walletapp.wallet.presentation.utils.FormatAmount
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun BalanceLineChartPremium(
    data: List<ChartPoint>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return

    val haptic = LocalHapticFeedback.current
    val animationProgress = remember { Animatable(0f) }
    var selectedIndex by remember(data) { mutableIntStateOf(-1) }
    val textMeasurer = rememberTextMeasurer()

    val labelStyle = MaterialTheme.typography.labelSmall.copy(
        fontSize = 9.sp,
        color = MaterialTheme.colorScheme.onTertiary.copy(0.4f),
        fontWeight = FontWeight.Bold
    )
    val color =  MaterialTheme.colorScheme.onTertiary

    LaunchedEffect(data) {
        animationProgress.animateTo(1f, tween(1200, easing = FastOutSlowInEasing))
    }

    // Matematik hisob-kitoblar
    val rawMin = data.minOf { it.value }
    val rawMax = data.maxOf { it.value }
    val maxVal = if (rawMax > 0) rawMax * 1.1 else 0.0
    val minVal = if (rawMin < 0) rawMin * 1.1 else 0.0
    val range = (maxVal - minVal).coerceAtLeast(1.0).toFloat()

    Box(modifier = modifier.fillMaxWidth().height(160.dp).padding(top = 8.dp)) {
        Canvas(modifier = Modifier
            .fillMaxSize()
            .pointerInput(data) {
                detectDragGestures(
                    onDragStart = { offset ->
                        selectedIndex = (offset.x / (size.width / (data.size - 1).coerceAtLeast(1))).roundToInt().coerceIn(0, data.size - 1)
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    onDragEnd = { selectedIndex = -1 },
                    onDrag = { change, _ ->
                        val idx = (change.position.x / (size.width / (data.size - 1).coerceAtLeast(1))).roundToInt().coerceIn(0, data.size - 1)
                        if (idx != selectedIndex) {
                            selectedIndex = idx
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    }
                )
            }
        ) {
            val width = size.width
            val height = size.height
            val spacing = width / (data.size - 1).coerceAtLeast(1)

            // Nol chizig'ining Y koordinatasi
            val zeroY = height - ((0.0 - minVal).toFloat() / range * height)

            val points = data.mapIndexed { i, d ->
                Offset(i * spacing, height - ((d.value - minVal).toFloat() / range * height * animationProgress.value))
            }

            // 1. Nol chizig'i (Dash Line)
            drawLine(
                color = color.copy(0.1f),
                start = Offset(0f, zeroY),
                end = Offset(width, zeroY),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
            )

            // 2. Chiziqni chizish (Smooth Curve)
            if (points.size >= 2) {
                val smoothPath = Path().apply {
                    moveTo(points[0].x, points[0].y)
                    for (i in 1 until points.size) {
                        val prev = points[i - 1]
                        val curr = points[i]
                        cubicTo((prev.x + curr.x) / 2, prev.y, (prev.x + curr.x) / 2, curr.y, curr.x, curr.y)
                    }
                }

                // INCOME QISMI (Yashil)
                clipRect(top = 0f, bottom = zeroY) {
                    drawPath(smoothPath, incomeColor, style = Stroke(2.dp.toPx(), cap = StrokeCap.Round))
                    // Gradient to'ldirish
                    val fillPath = android.graphics.Path(smoothPath.asAndroidPath()).apply {
                        lineTo(width, zeroY); lineTo(0f, zeroY); close()
                    }.asComposePath()
                    drawPath(fillPath, Brush.verticalGradient(listOf(incomeColor.copy(0.2f), Color.Transparent)))
                }

                // EXPENSE QISMI (Qizil)
                clipRect(top = zeroY, bottom = height) {
                    drawPath(smoothPath, expenseColor, style = Stroke(2.dp.toPx(), cap = StrokeCap.Round))
                    // Gradient to'ldirish
                    val fillPath = android.graphics.Path(smoothPath.asAndroidPath()).apply {
                        lineTo(width, zeroY); lineTo(0f, zeroY); close()
                    }.asComposePath()
                    drawPath(fillPath, Brush.verticalGradient(listOf(Color.Transparent, expenseColor.copy(0.2f))))
                }
            }

            // 3. Selection Indicator
            if (selectedIndex != -1) {
                val p = points[selectedIndex]
                val pColor = if (data[selectedIndex].value >= 0) incomeColor else expenseColor

                drawLine(pColor.copy(0.3f), Offset(p.x, 0f), Offset(p.x, height), 1.dp.toPx())
                drawCircle(pColor, 6.dp.toPx(), p)
                drawCircle(Color.White, 3.dp.toPx(), p)
            }

            // 4. Labels
            drawGridLabels(height, width, maxVal, minVal, textMeasurer, labelStyle)
        }

        if (selectedIndex != -1) {
            ChartTooltip(data[selectedIndex], selectedIndex, data.size)
        }
    }
}

@Composable
private fun BoxScope.ChartTooltip(point: ChartPoint, index: Int, total: Int) {
    val alignment = when {
        index < 2 -> Alignment.TopStart
        index > total - 3 -> Alignment.TopEnd
        else -> Alignment.TopCenter
    }

    Surface(
        modifier = Modifier
            .align(alignment)
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
        tonalElevation = 4.dp
    ) {
        Column(Modifier.padding(8.dp, 4.dp)) {
            Text(point.label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            Text(FormatAmount(point.value), fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onTertiary.copy(0.7f))
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawGridLabels(
    height: Float,
    width: Float,
    max: Double,
    min: Double,
    measurer: androidx.compose.ui.text.TextMeasurer,
    style: androidx.compose.ui.text.TextStyle
) {
    val gridValues = listOf(max, 0.0, min).distinct()
    val range = (max - min).coerceAtLeast(1.0)

    gridValues.forEach { value ->
        val ratio = ((value - min) / range).toFloat()
        val yPos = height - (ratio * height)

        // Qiymat matnini o'lchash
        val textLayout = measurer.measure(
            text = formatAmountShort(value),
            style = style
        )

        // Matnni chizish (ozgina chapga surib, markazlashtirib)
        drawText(
            textLayoutResult = textLayout,
            color = style.color,
            topLeft = Offset(
                x = 4.dp.toPx(),
                y = (yPos - textLayout.size.height / 2).coerceIn(0f, height - textLayout.size.height)
            )
        )

        // Yupqa chiziq (grid line)
        drawLine(
            color = Color.Gray.copy(alpha = 0.05f),
            start = Offset(40.dp.toPx(), yPos), // Matndan keyin boshlanadi
            end = Offset(width, yPos),
            strokeWidth = 1.dp.toPx()
        )
    }
}

fun formatAmountShort(value: Double): String {
    val absVal = abs(value)
    return when {
        absVal >= 1_000_000 -> String.format("%.1fM", value / 1_000_000).replace(".0", "")
        absVal >= 1_000 -> String.format("%.0fK", value / 1_000)
        else -> value.toInt().toString()
    }
}