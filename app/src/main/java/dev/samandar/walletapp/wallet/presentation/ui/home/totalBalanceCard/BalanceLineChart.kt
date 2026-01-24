package dev.samandar.walletapp.wallet.presentation.ui.home.totalBalanceCard

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.ui.theme.expenseColor
import dev.samandar.walletapp.ui.theme.incomeColor
import dev.samandar.walletapp.utils.Strings
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt


@Composable
fun BalanceLineChart(
    data: List<BarChartItem>,
    isIncomeMode: Boolean,
    modifier: Modifier = Modifier,
    globalMaxLimit: Double? = null
) {
    val haptic = LocalHapticFeedback.current
    val chartHeight = 160.dp

    val lineColor = if (isIncomeMode) incomeColor else expenseColor
    val labelPrefix = if (isIncomeMode) stringResource(Strings.title_income) else stringResource(Strings.title_expense)

    val isTotalZero = remember(data) { data.all { it.value == 0.0 } }
    if (data.isEmpty() || isTotalZero) {
        EmptyChartView(
            modifier = modifier
        )
        return
    }

    val chartUpperLimit = remember(data, globalMaxLimit) {
        val localMax = data.maxOfOrNull { it.value } ?: 1.0
        maxOf(localMax, globalMaxLimit ?: 0.0).coerceAtLeast(1.0)
    }

    var selectedIndex by remember(data) { mutableIntStateOf(-1) }
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(data, isIncomeMode) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(1f, tween(1000, easing = FastOutSlowInEasing))
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(chartHeight),
        shape = RoundedCornerShape(20.dp),
        color = Color.Transparent,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            lineColor.copy(alpha = 0.07f),
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = 400f
                    )
                )
                .padding(12.dp)
        ){

            Box(modifier = Modifier.fillMaxWidth().height(26.dp), contentAlignment = Alignment.Center) {
                if (selectedIndex != -1 && selectedIndex < data.size) {
                    val item = data[selectedIndex]
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(lineColor.copy(0.2f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "$labelPrefix (${item.label}): ",
                            fontSize = 10.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "%,.0f so'm".format(Locale.US, item.value).replace(",", " "),
                            fontSize = 11.sp,
                            color = lineColor,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                Column(
                    modifier = Modifier.width(35.dp).fillMaxHeight().padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf(chartUpperLimit, chartUpperLimit / 2, 0.0).forEach { valLabel ->
                        Text(
                            text = formatAmountPremium(valLabel),
                            fontSize = 7.sp,
                            color = Color.Gray.copy(0.4f),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Box(modifier = Modifier.weight(1f).fillMaxHeight().padding(start = 6.dp)) {
                    Canvas(modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(data) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    // Barmoq tekkan zahoti birinchi indexni aniqlaymiz va vibratsiya beramiz
                                    val width = size.width
                                    val step = width / (data.size - 1).coerceAtLeast(1)
                                    val index = (offset.x / step).roundToInt().coerceIn(0, data.size - 1)
                                    selectedIndex = index
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                },
                                onDragEnd = { selectedIndex = -1 },
                                onDragCancel = { selectedIndex = -1 },
                                onDrag = { change, _ ->
                                    val width = size.width
                                    val step = width / (data.size - 1).coerceAtLeast(1)
                                    val index = (change.position.x / step).roundToInt().coerceIn(0, data.size - 1)

                                    if (selectedIndex != index) {
                                        // BU YERDA: HapticFeedbackType.LongPress ko'proq qurilmalarda ishlaydi
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        selectedIndex = index
                                    }
                                }
                            )
                        }
                    ) {
                        val width = size.width
                        val height = size.height
                        val usableHeight = height - 15.dp.toPx()
                        val zeroY = height - 5.dp.toPx()
                        val spacing = width / (data.size - 1).coerceAtLeast(1)

                        // HORIZONTAL GRID LINES
                        listOf(0f, 0.5f, 1f).forEach { ratio ->
                            val y = zeroY - (usableHeight * ratio)
                            drawLine(
                                color = Color.Gray.copy(alpha = 0.05f),
                                start = Offset(0f, y),
                                end = Offset(width, y),
                                strokeWidth = 1.dp.toPx()
                            )
                        }

                        val points = data.mapIndexed { i, item ->
                            val ratio = (item.value / chartUpperLimit).toFloat()
                            Offset(i * spacing, zeroY - (usableHeight * ratio * animationProgress.value))
                        }

                        if (points.size >= 2) {
                            val strokePath = Path().apply {
                                moveTo(points.first().x, points.first().y)
                                for (i in 1 until points.size) {
                                    val p1 = points[i]; val p0 = points[i - 1]
                                    cubicTo((p0.x + p1.x) / 2, p0.y, (p0.x + p1.x) / 2, p1.y, p1.x, p1.y)
                                }
                            }

                            // GRADIENT FILL (Yuqorisi rangli, pasti shaffof)
                            val fillPath = android.graphics.Path(strokePath.asAndroidPath()).apply {
                                lineTo(points.last().x, zeroY)
                                lineTo(points.first().x, zeroY)
                                close()
                            }.asComposePath()

                            drawPath(
                                fillPath,
                                Brush.verticalGradient(
                                    colors = listOf(lineColor.copy(0.25f), Color.Transparent),
                                    startY = 0f,
                                    endY = zeroY
                                )
                            )

                            // MAIN BOLD LINE
                            drawPath(
                                path = strokePath,
                                color = lineColor,
                                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                            )

                            // --- 3. SELECTION UI (Vertical line & Dots) ---
                            if (selectedIndex != -1 && selectedIndex < points.size) {
                                val p = points[selectedIndex]

                                // Vertical Line
                                drawLine(
                                    color = lineColor.copy(0.3f),
                                    start = Offset(p.x, 0f),
                                    end = Offset(p.x, zeroY),
                                    strokeWidth = 1.5.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                                )

                                // Indicator Dot
                                drawCircle(lineColor, 6.dp.toPx(), p)
                                drawCircle(Color.White, 2.5.dp.toPx(), p)
                                // Outer Glow
                                drawCircle(lineColor.copy(0.2f), 12.dp.toPx(), p)
                            }
                        }
                    }
                }
            }

            // X-Axis
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 41.dp, top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val step = (data.size / 4).coerceAtLeast(1)
                data.forEachIndexed { index, item ->
                    if (index % step == 0 || index == data.size - 1) {
                        Text(item.label, fontSize = 8.sp, color = Color.Gray.copy(0.5f), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}


fun formatAmountPremium(value: Double): String {
    val absValue = abs(value)
    val sign = if (value < 0) "-" else ""
    val locale = Locale.US

    val result = when {
        absValue >= 1e9 -> "%.1fB".format(locale, absValue / 1e9)
        absValue >= 1e6 -> "%.1fM".format(locale, absValue / 1e6)
        absValue >= 1e3 -> "%.0fK".format(locale, absValue / 1e3)
        else -> absValue.toInt().toString()
    }.replace(".0", "")

    return "$sign$result"
}