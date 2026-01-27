package dev.samandar.walletapp.wallet.presentation.ui.home.diogramCharts

import android.annotation.SuppressLint
import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.categories.getTranslatedName
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencyManager
import dev.samandar.walletapp.wallet.presentation.utils.formatAmountWithCurrency
import dev.samandar.walletapp.wallet.presentation.viewmodel.chartViewmodel.CategoryData
import kotlin.math.roundToInt

val activeCurrency by CurrencyManager.currentCurrency
@Composable
fun EmptyDataChart(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.no_data_available),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.add_transactions_to_see_chart),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.4f),
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun DoughnutChart(
    data: List<CategoryData>,
    totalAmount: Double,
    chartSize: Dp = 140.dp,
    chartCategoryCard: Dp = 100.dp,
    strokeWidthFraction: Float = 0.16f
) {
    val density = LocalDensity.current
    val safeTotal = if (totalAmount <= 0.0) 1.0 else totalAmount

    val animatedProgress by animateFloatAsState(
        targetValue = if (data.isNotEmpty() && totalAmount > 0) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessLow),
        label = "DoughnutChartAnimation"
    )

    // Ranglar va Matnlar
    val amountColor = MaterialTheme.colorScheme.primary.toArgb()
    val labelColor = MaterialTheme.colorScheme.onTertiary.copy(0.5f).toArgb()
    val formattedAmount = formatAmountWithCurrency(totalAmount)
    val totalLabel = stringResource(R.string.total_label).uppercase()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (data.isEmpty() || totalAmount <= 0.0) {
            EmptyDataChart()
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(chartSize)
            ) {
                Canvas(modifier = Modifier.fillMaxSize().padding(4.dp)) {
                    val diameter = size.minDimension
                    val strokePx = diameter * strokeWidthFraction
                    val arcSize = Size(diameter - strokePx, diameter - strokePx)
                    val topLeft = Offset(strokePx / 2, strokePx / 2)

                    // Track background
                    drawCircle(
                        color = Color.Gray.copy(alpha = 0.05f),
                        radius = arcSize.width / 2,
                        center = center,
                        style = Stroke(strokePx)
                    )

                    var startAngle = -90f // 12 dan boshlash
                    data.forEach { item ->
                        val sweep = ((item.amount / safeTotal).toFloat() * 360f) * animatedProgress
                        drawArc(
                            color = item.color,
                            startAngle = startAngle,
                            sweepAngle = sweep,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokePx, cap = StrokeCap.Butt) // Yumaloq uchlar
                        )
                        startAngle += sweep
                    }

                    // MARKAZIY MATNNI ANIQ JOYLASHTIRISH
                    drawIntoCanvas { canvas ->
                        val paintAmount = Paint().apply {
                            color = amountColor
                            textSize = with(density) {
                                when {
                                    formattedAmount.length > 12 -> 10.sp.toPx()
                                    formattedAmount.length > 9 -> 12.sp.toPx()
                                    else -> 14.sp.toPx()
                                }.toFloat()
                            }
                            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                            textAlign = Paint.Align.CENTER
                            isAntiAlias = true
                        }

                        val paintLabel = Paint().apply {
                            color = labelColor
                            textSize = with(density) { 8.sp.toPx() }
                            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                            textAlign = Paint.Align.CENTER
                            isAntiAlias = true
                        }

                        val amountMetrics = paintAmount.fontMetrics
                        val labelMetrics = paintLabel.fontMetrics
                        val space = with(density) { 2.dp.toPx() }

                        // Umumiy balandlikni hisoblash
                        val totalH = (labelMetrics.descent - labelMetrics.ascent) + space + (amountMetrics.descent - amountMetrics.ascent)
                        val startY = center.y - (totalH / 2)

                        // Label chizish
                        canvas.nativeCanvas.drawText(totalLabel, center.x, startY - labelMetrics.ascent, paintLabel)
                        // Summa chizish
                        canvas.nativeCanvas.drawText(formattedAmount, center.x, startY - labelMetrics.ascent + labelMetrics.descent + space - amountMetrics.ascent, paintAmount)
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                modifier = Modifier
                    .weight(1f)
                    .heightIn(max = chartCategoryCard),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(data.sortedByDescending { it.amount }) { item ->
                    LegendItemCompact(item = item, safeTotal = safeTotal)
                }
            }
        }
    }
}

@Composable
fun LegendItemCompact(item: CategoryData, safeTotal: Double) {
    val percentage = (item.amount / safeTotal) * 100

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(item.color)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = getTranslatedName(item.categoryName).toString(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${percentage.roundToInt()}%",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                )
            )
        }
        Spacer(Modifier.height(2.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onTertiary.copy(0.05f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((percentage / 100).toFloat())
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(item.color)
            )
        }
    }
}