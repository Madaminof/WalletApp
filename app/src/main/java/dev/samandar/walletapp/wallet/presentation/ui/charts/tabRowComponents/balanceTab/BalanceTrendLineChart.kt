package dev.samandar.walletapp.wallet.presentation.ui.charts.tabRowComponents.balanceTab

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.wallet.presentation.ui.charts.tabRowComponents.BalancePoint
import dev.samandar.walletapp.wallet.presentation.ui.charts.tabRowComponents.EmptyChartView
import dev.samandar.walletapp.wallet.presentation.ui.home.cardStatistics.TimePeriod
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max


@Composable
fun BalanceTrendLineChart(
    data: List<BalancePoint>,
    modifier: Modifier = Modifier,
    lineColor: Color,
    selectedFilter: TimePeriod
) {
    if (data.isEmpty()) {
        EmptyChartView(modifier = modifier.height(120.dp))
        return
    }

    val values = data.map { it.amount }
    val minVal = values.minOrNull() ?: 0.0
    val maxVal = values.maxOrNull() ?: 0.0
    val range = (maxVal - minVal).takeIf { it > 0 } ?: 1.0
    val density = LocalDensity.current
    val strokeWidthPx = with(density) { 3.dp.toPx() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(horizontal = 10.dp)
    ) {

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {

            val w = size.width
            val h = size.height

            val points = data.mapIndexed { index, point ->
                val x = if (data.size > 1)
                    index * (w / (data.size - 1))
                else w / 2f
                val norm = ((point.amount - minVal) / range).toFloat()
                val y = h - (norm * h)
                Offset(x, y)
            }
            if (points.size < 2) {
                if (points.size == 1) {
                    drawCircle(
                        color = lineColor,
                        radius = strokeWidthPx * 1.5f,
                        center = points.first()
                    )
                }
                return@Canvas
            }
            val path = Path().apply {
                moveTo(points.first().x, points.first().y)

                for (i in 1 until points.size) {
                    val prev = points[i - 1]
                    val curr = points[i]
                    val controlX = (prev.x + curr.x) / 2f
                    cubicTo(
                        controlX, prev.y,
                        controlX, curr.y,
                        curr.x, curr.y
                    )
                }
            }
            val fillPath = Path().apply {
                addPath(path)
                lineTo(points.last().x, h)
                lineTo(points.first().x, h)
                close()
            }

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        lineColor.copy(alpha = 0.35f),
                        lineColor.copy(alpha = 0.05f)
                    )
                )
            )
            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            )
        }

        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val dateFormatPattern = when (selectedFilter) {
                is TimePeriod.Daily -> "HH:mm"
                is TimePeriod.Weekly -> "EEE"
                is TimePeriod.Monthly -> "dd"
                is TimePeriod.Year -> "MMM"
                is TimePeriod.AllTime -> "yy/MM"
                else -> "dd/MM"
            }

            val sdf = SimpleDateFormat(dateFormatPattern, Locale.getDefault())

            val labelCount = when (selectedFilter) {
                is TimePeriod.Daily -> 5
                is TimePeriod.Weekly -> 7
                is TimePeriod.Monthly -> 4
                is TimePeriod.Year -> 4
                else -> 4
            }

            val step = max((data.size - 1) / (labelCount - 1), 1)

            for (i in data.indices step step) {
                Text(
                    text = sdf.format(Date(data[i].date)),
                    fontSize = 8.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}