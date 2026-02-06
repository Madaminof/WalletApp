package dev.samandar.walletapp.wallet.presentation.ui.charts.categoryStatistic.CategoryDetail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.wallet.domain.model.Transaction
import dev.samandar.walletapp.wallet.presentation.utils.formatAmountWithCurrency
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PremiumNativeChart(
    transactions: List<Transaction>,
    lineColor: Color,
    modifier: Modifier = Modifier,
) {
    val haptic = LocalHapticFeedback.current
    val zoneId = ZoneId.systemDefault()

    val axisTextColor = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.5f).toArgb()
    val labelFontSizePx = with(androidx.compose.ui.platform.LocalDensity.current) { 8.sp.toPx() }
    val typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.NORMAL )

    // 1. Data Processing
    val chartData = remember(transactions) {
        if (transactions.isEmpty()) return@remember null

        val labelFmt = DateTimeFormatter.ofPattern("dd MMM", Locale.getDefault())
        val groupedMap = transactions.groupBy {
            Instant.ofEpochMilli(it.date).atZone(zoneId).toLocalDate()
        }.mapValues { it.value.sumOf { tx -> tx.amount }.toFloat() }

        val startDate = groupedMap.keys.minOrNull() ?: LocalDate.now()
        val endDate = groupedMap.keys.maxOrNull() ?: LocalDate.now()
        val daysCount = ChronoUnit.DAYS.between(startDate, endDate).toInt().coerceAtLeast(1)

        val labels = mutableListOf<String>()
        val values = mutableListOf<Float>()

        for (i in 0..daysCount) {
            val date = startDate.plusDays(i.toLong())
            labels.add(date.format(labelFmt))
            values.add(groupedMap[date] ?: 0f)
        }
        Pair(labels, values)
    }

    val labels = chartData?.first ?: emptyList()
    val dataPoints = chartData?.second ?: emptyList()
    if (dataPoints.isEmpty()) return

    var selectedIndex by remember { mutableIntStateOf(-1) }
    val transitionProgress = remember { Animatable(0f) }

    LaunchedEffect(dataPoints) {
        transitionProgress.animateTo(1f, animationSpec = tween(1000, easing = FastOutSlowInEasing))
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(0.5f))
            .padding(16.dp)
    ) {
        ChartHeader(selectedIndex, labels, dataPoints)

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) {
            val maxAmount = (dataPoints.maxOrNull() ?: 1f).coerceAtLeast(1f)

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(dataPoints) {
                        detectDragGestures(
                            onDragEnd = { selectedIndex = -1 },
                            onDragCancel = { selectedIndex = -1 },
                            onDrag = { change, _ ->
                                val x = change.position.x
                                val usableWidth =
                                    size.width - 120f
                                val relativeX = (x - 100f).coerceIn(0f, usableWidth)
                                val spaceX = usableWidth / (dataPoints.size - 1).coerceAtLeast(1)
                                val index = (relativeX / spaceX)
                                    .roundToInt()
                                    .coerceIn(0, dataPoints.size - 1)

                                if (index != selectedIndex) {
                                    selectedIndex = index
                                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                }
                            }
                        )
                    }
            ) {
                val paddingLeft = 100f  // Y-o'qi summalari uchun joy
                val paddingBottom = 50f // X-o'qi sanalari uchun joy
                val chartWidth = size.width - paddingLeft
                val chartHeight = size.height - paddingBottom

                val textPaint = android.graphics.Paint().apply {
                    color = axisTextColor
                    textSize = labelFontSizePx
                    isAntiAlias = true
                    this.typeface = typeface
                }

                val spaceX = chartWidth / (dataPoints.size - 1).coerceAtLeast(1)

                // 2. Y-O'QI SUMMALARINI CHIZISH (Left Axis)
                val gridLines = 4
                for (i in 0..gridLines) {
                    val y = chartHeight - (chartHeight / gridLines * i)
                    val amountLabel = (maxAmount / gridLines * i)

                    drawLine(
                        color = Color.Gray.copy(0.08f),
                        start = Offset(paddingLeft, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1.dp.toPx()
                    )

                    drawContext.canvas.nativeCanvas.drawText(
                        formatShortAmount(amountLabel.toDouble()),
                        10f,
                        y + (labelFontSizePx / 3), // Matnni chiziqqa nisbatan markazlash
                        textPaint
                    )
                }

                // 3. GRAFIK CHIZIG'I (Points)
                val points = dataPoints.mapIndexed { i, valAt ->
                    Offset(
                        x = paddingLeft + (i * spaceX),
                        y = chartHeight - ((valAt / maxAmount) * chartHeight * transitionProgress.value)
                    )
                }

                if (points.size > 1) {
                    val strokePath = Path().apply {
                        moveTo(points[0].x, points[0].y)
                        for (i in 0 until points.size - 1) {
                            val p0 = points[i]
                            val p1 = points[i + 1]
                            cubicTo((p0.x + p1.x) / 2, p0.y, (p0.x + p1.x) / 2, p1.y, p1.x, p1.y)
                        }
                    }
                    drawPath(strokePath, lineColor, style = Stroke(3.dp.toPx(), cap = StrokeCap.Round))

                    val fillPath = android.graphics.Path(strokePath.asAndroidPath()).asComposePath().apply {
                        lineTo(points.last().x, chartHeight)
                        lineTo(paddingLeft, chartHeight)
                        close()
                    }
                    drawPath(fillPath, Brush.verticalGradient(listOf(lineColor.copy(0.2f), Color.Transparent)))
                }

                // 4. X-O'QI SANALARI (Bottom Axis)
                val labelStep = (labels.size / 3).coerceAtLeast(1)
                labels.forEachIndexed { i, label ->
                    if (i % labelStep == 0 || i == labels.size - 1) {
                        val xPos = paddingLeft + (i * spaceX)
                        val textWidth = textPaint.measureText(label)

                        drawContext.canvas.nativeCanvas.drawText(
                            label,
                            (xPos - textWidth / 2).coerceIn(paddingLeft, size.width - textWidth),
                            size.height - 5f,
                            textPaint
                        )
                    }
                }

                // 5. SELECTION OVERLAY
                if (selectedIndex != -1) {
                    val p = points[selectedIndex]
                    drawCircle(lineColor, 6.dp.toPx(), p)
                    drawCircle(Color.White, 3.dp.toPx(), p)
                    drawLine(
                        lineColor.copy(0.4f),
                        Offset(p.x, 0f),
                        Offset(p.x, chartHeight),
                        1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                    )
                }
            }
        }
    }
}

fun formatShortAmount(amount: Double): String {
    return when {
        amount >= 1_000_000 -> String.format("%.1fM", amount / 1_000_000)
        amount >= 1_000 -> String.format("%.0fk", amount / 1_000)
        else -> amount.toInt().toString()
    }
}


@Composable
fun ChartHeader(
    selectedIndex: Int,
    labels: List<String>,
    dataPoints: List<Float>,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        contentAlignment = Alignment.Center
    ) {
        if (selectedIndex != -1 && selectedIndex < dataPoints.size) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = labels[selectedIndex],
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )
                Text(
                    text = formatAmountWithCurrency(dataPoints[selectedIndex].toDouble()),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                )
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(dev.samandar.walletapp.utils.Strings.chart_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}