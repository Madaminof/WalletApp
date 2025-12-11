package com.example.walletapp.wallet.presentation.ui.home.diogramCharts

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencyManager
import com.example.walletapp.wallet.presentation.utils.formatAmountWithCurrency
import com.example.walletapp.wallet.presentation.viewmodel.CategoryData
import java.text.DecimalFormat
val activeCurrency by CurrencyManager.currentCurrency

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun DoughnutChart(
    data: List<CategoryData>,
    totalAmount: Double,
    chartSize: Dp = 140.dp,
    chartCategoryCard: Dp = 100.dp,
    strokeWidthFraction: Float = 0.18f
) {
    val safeTotal = if (totalAmount == 0.0) 1.0 else totalAmount

    val animatedProgress by animateFloatAsState(
        targetValue = if (data.isNotEmpty() && totalAmount > 0) 1f else 0f,
        animationSpec = tween(800),
        label = "DoughnutChartAnimation"
    )

    val formatter = remember { DecimalFormat("#,###.##") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (data.isEmpty() || totalAmount == 0.0) {
            Box(
                modifier = Modifier.size(chartSize),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Ma'lumotlar mavjud emas",
                    color = MaterialTheme.colorScheme.onTertiary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            BoxWithConstraints(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(chartSize)
                    .padding(4.dp)
            ) {
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val diameter = size.minDimension
                    val currentStrokeWidth = diameter * strokeWidthFraction
                    val topLeft = Offset(currentStrokeWidth / 2, currentStrokeWidth / 2)
                    val arcSize = Size(diameter - currentStrokeWidth, diameter - currentStrokeWidth)

                    var startAngle = 0f

                    drawCircle(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        radius = (diameter - currentStrokeWidth) / 2,
                        center = center,
                        style = Stroke(currentStrokeWidth)
                    )

                    data.forEach { item ->
                        val percent = item.amount.toDouble() / safeTotal
                        val sweep = (percent * 360f) * animatedProgress
                        drawArc(
                            color = item.color,
                            startAngle = startAngle,
                            sweepAngle = sweep.toFloat(),
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = currentStrokeWidth, cap = StrokeCap.Butt)
                        )
                        startAngle += sweep.toFloat()
                    }
                }

                val totalText = formatter.format(totalAmount)
                val textLength = totalText.length

                var currentFontSize = 16.sp
                when {
                    textLength > 12 -> currentFontSize = 7.sp
                    textLength > 9 -> currentFontSize = 8.sp
                    textLength > 7 -> currentFontSize = 9.sp
                    else -> currentFontSize = 10.sp
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Total",
                        color = MaterialTheme.colorScheme.onTertiary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal
                    )
                    Text(
                        text = formatAmountWithCurrency(totalAmount),
                        fontSize = currentFontSize,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = chartCategoryCard, max = chartCategoryCard),
                verticalArrangement = Arrangement.spacedBy(0.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                items(data) { item ->
                    LegendItemCompact(item = item, safeTotal = safeTotal)
                }
            }
        }
    }
}

@Composable
fun LegendItemCompact(item: CategoryData, safeTotal: Double) {
    val percentage = (item.amount.toDouble() / safeTotal) * 100
    val percentText = "${percentage.toInt()}%"

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(item.color, CircleShape)
        )

        Spacer(Modifier.width(6.dp))

        Text(
            text = item.categoryName,
            fontSize = 8.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onTertiary.copy(0.7f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = percentText,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            color = item.color,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}