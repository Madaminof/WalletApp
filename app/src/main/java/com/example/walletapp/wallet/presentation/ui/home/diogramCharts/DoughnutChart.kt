package com.example.walletapp.wallet.presentation.ui.home.diogramCharts

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.wallet.presentation.viewmodel.CategoryData
import java.text.DecimalFormat
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.ui.text.style.TextOverflow


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DoughnutChart(
    data: List<CategoryData>,
    totalAmount: Double,
    chartSize: Dp = 130.dp,
    strokeWidthFraction: Float = 0.15f
) {
    val safeTotal = if (totalAmount == 0.0) 1.0 else totalAmount

    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(800),
        label = "DoughnutChartAnimation"
    )

    val formatter = remember { DecimalFormat("#,###.##") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color.Transparent)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        if (data.isEmpty() || totalAmount == 0.0) {
            Box(
                modifier = Modifier
                    .size(chartSize + 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Ma'lumotlar mavjud emas",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            }
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(chartSize)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    val diameter = size.minDimension
                    val currentStrokeWidth = diameter * strokeWidthFraction
                    val topLeft = Offset(currentStrokeWidth / 2, currentStrokeWidth / 2)
                    val arcSize = Size(diameter - currentStrokeWidth, diameter - currentStrokeWidth)

                    var startAngle = 0f

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
                            style = Stroke(
                                width = currentStrokeWidth,
                                cap = StrokeCap.Butt
                            )
                        )
                        startAngle += sweep.toFloat()
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Jami",
                        color = MaterialTheme.colorScheme.onTertiary,
                        fontSize = (chartSize.value / 8).sp,
                        fontWeight = FontWeight.Normal
                    )
                    Text(
                        "${formatter.format(totalAmount)} UZS",
                        fontSize = (chartSize.value / 13).sp,
                        color = MaterialTheme.colorScheme.onTertiary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            FlowRow(
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.spacedBy(0.dp),
                horizontalArrangement = Arrangement.Start,
                maxItemsInEachRow = 2,
                ) {
                data.forEach { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(item.color, CircleShape)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = item.categoryName,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onTertiary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}