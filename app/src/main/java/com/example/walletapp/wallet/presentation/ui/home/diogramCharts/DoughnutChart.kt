package com.example.walletapp.wallet.presentation.ui.home.diogramCharts

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.wallet.presentation.viewmodel.CategoryData
import java.text.DecimalFormat
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.Dp


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DoughnutChart(
    data: List<CategoryData>,
    totalAmount: Double,
    chartSize: Dp = 220.dp // default qiymat
) {

    val safeTotal = if (totalAmount == 0.0) 1.0 else totalAmount

    val animatedProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(600),
        label = ""
    )

    val formatter = remember { DecimalFormat("#,###.##") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer),
        shape = RectangleShape
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (data.isEmpty() || totalAmount == 0.0) {
                Text("Chiqim ma'lumotlari mavjud emas", color = Color.Gray)
            } else {
                Box(contentAlignment = Alignment.Center) {

                    Canvas(
                        modifier = Modifier
                            .size(chartSize)
                            .padding(8.dp)
                    ) {
                        val diameter = size.minDimension
                        val strokeWidth = (chartSize.value / 6).dp.toPx()
                        val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                        val arcSize = Size(diameter - strokeWidth, diameter - strokeWidth)

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
                                    width = strokeWidth,
                                    cap = StrokeCap.Butt
                                )
                            )
                            startAngle += sweep.toFloat()
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val titleFontSize = (chartSize.value / 13).sp      // "Jami" matni
                        val amountFontSize = (chartSize.value / 16).sp     // summa matni

                        Text(
                            "Jami",
                            color = Color.Gray,
                            fontSize = titleFontSize
                        )
                        Text(
                            "${formatter.format(totalAmount)} UZS",
                            fontSize = amountFontSize,
                            color = MaterialTheme.colorScheme.onTertiary
                        )
                    }

                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            FlowRow(
                maxItemsInEachRow = 3,
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.spacedBy(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                data.forEach { item ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(item.color, CircleShape)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = item.categoryName,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}
