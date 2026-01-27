package dev.samandar.walletapp.wallet.presentation.ui.charts.categoryStatistic.CategoryDetail

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.domain.model.Transaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt


@Composable
fun PremiumNativeChart(
    transactions: List<Transaction>,
    lineColor: Color,
    modifier: Modifier = Modifier,
) {
    val chartData = remember(transactions) {
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val labelFmt = SimpleDateFormat("dd MMM", Locale.getDefault())

        val points = transactions
            .groupBy { sdf.format(Date(it.date)) }
            .mapValues { it.value.sumOf { tx -> tx.amount }.toFloat() }
            .toSortedMap()
            .toList()

        val labels = points.map {
            sdf.parse(it.first)?.let { date -> labelFmt.format(date) } ?: ""
        }
        val values = points.map { it.second }

        Triple(points, labels, values)
    }

    val labels = chartData.second
    val dataPoints = chartData.third

    if (dataPoints.isEmpty()) return

    var selectedIndex by remember { mutableIntStateOf(-1) }
    val transitionProgress = remember { Animatable(0f) }
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current

    LaunchedEffect(dataPoints) {
        transitionProgress.animateTo(1f, animationSpec = tween(1200, easing = FastOutSlowInEasing))
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(0.8f))
            .padding(16.dp),
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(60.dp), contentAlignment = Alignment.Center) {
            if (selectedIndex != -1) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = labels[selectedIndex],
                        color = Color.Gray,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = String.format("%,.0f so'm", dataPoints[selectedIndex]),
                        color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Text(
                    text = stringResource(Strings.chart_hint),
                    color = Color.Gray.copy(0.5f),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        // --- ASOSIY CANVAS ---
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .pointerInput(dataPoints) {
                    detectDragGestures(
                        onDragEnd = { selectedIndex = -1 },
                        onDragCancel = { selectedIndex = -1 },
                        onDrag = { change, _ ->
                            val x = change.position.x
                            val partWidth = size.width / (dataPoints.size - 1).coerceAtLeast(1)
                            val newIndex =
                                (x / partWidth)
                                    .roundToInt()
                                    .coerceIn(0, dataPoints.size - 1)
                            if (newIndex != selectedIndex) {
                                selectedIndex = newIndex
                                haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                            }
                        }
                    )
                }
        ) {
            val width = size.width
            val height = size.height
            val maxAmount = dataPoints.maxOrNull() ?: 1f
            val minAmount = dataPoints.minOfOrNull { it } ?: 0f
            val range = (maxAmount - minAmount).coerceAtLeast(1f)
            val spacePerPoint = width / (dataPoints.size - 1).coerceAtLeast(1)

            val points = dataPoints.mapIndexed { index, amount ->
                Offset(
                    x = index * spacePerPoint,
                    y = height - ((amount - minAmount) / range) * height * transitionProgress.value
                )
            }

            // --- Y O'QI (Horizontal Yordamchi Chiziqlar) ---
            val gridLines = 4
            for (i in 0..gridLines) {
                val y = height / gridLines * i
                drawLine(
                    color = Color.White.copy(alpha = 0.03f),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // --- GRADIENT FILL ---
            if (points.size > 1) {
                val fillPath = Path().apply {
                    moveTo(points[0].x, height)
                    points.forEach { lineTo(it.x, it.y) }
                    lineTo(points.last().x, height)
                    close()
                }
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(lineColor.copy(alpha = 0.4f), Color.Transparent)
                    )
                )

                // --- SMOOTH LINE ---
                val strokePath = Path().apply {
                    moveTo(points[0].x, points[0].y)
                    for (i in 0 until points.size - 1) {
                        val p0 = points[i]
                        val p1 = points[i + 1]
                        cubicTo(
                            (p0.x + p1.x) / 2, p0.y,
                            (p0.x + p1.x) / 2, p1.y,
                            p1.x, p1.y
                        )
                    }
                }
                drawPath(
                    path = strokePath,
                    color = lineColor,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            // --- SELECTION OVERLAY ---
            if (selectedIndex != -1 && selectedIndex < points.size) {
                val p = points[selectedIndex]

                // Neon Glowing Dot
                drawCircle(lineColor.copy(0.2f), radius = 12.dp.toPx(), center = p)
                drawCircle(Color.White, radius = 6.dp.toPx(), center = p)
                drawCircle(lineColor, radius = 3.dp.toPx(), center = p)

                // Dash Line
                drawLine(
                    color = lineColor.copy(0.5f),
                    start = Offset(p.x, 0f),
                    end = Offset(p.x, height),
                    strokeWidth = 1.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            labels.filterIndexed { index, _ ->
                index == 0 || index == labels.size / 2 || index == labels.size - 1
            }.forEach { label ->
                Text(text = label, color = Color.Gray, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
