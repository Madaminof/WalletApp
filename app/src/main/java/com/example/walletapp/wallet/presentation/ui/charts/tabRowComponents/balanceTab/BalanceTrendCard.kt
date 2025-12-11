package com.example.walletapp.wallet.presentation.ui.charts.tabRowComponents.balanceTab

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.walletapp.ui.theme.expenseColor
import com.example.walletapp.ui.theme.incomeColor
import com.example.walletapp.wallet.presentation.ui.charts.tabRowComponents.BalancePoint
import com.example.walletapp.wallet.presentation.ui.charts.tabRowComponents.BalanceTabViewModel
import com.example.walletapp.wallet.presentation.ui.charts.tabRowComponents.CircularIconButton
import com.example.walletapp.wallet.presentation.ui.charts.tabRowComponents.EmptyChartView
import com.example.walletapp.wallet.presentation.ui.charts.tabRowComponents.primaryAccent
import com.example.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencyManager
import com.example.walletapp.wallet.presentation.utils.formatAmountWithCurrency
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max

val primaryAccent = Color(0xFF4759C1)

@Composable
fun TimeFilterRow(selected: String, onFilterChange: (String) -> Unit) {
    val options = listOf("Day", "Week", "Month", "Year", "All")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(0.2f))
            .height(30.dp)
            .padding(2.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEach { option ->
            val isSelected = option == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) primaryAccent else Color.Transparent)
                    .clickable { onFilterChange(option) }
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    fontSize = 10.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (isSelected) Color.White else Color.DarkGray.copy(alpha = 0.8f)
                )
            }
        }
    }
}


@Composable
fun BalanceTrendLineChart(
    data: List<BalancePoint>,
    modifier: Modifier = Modifier,
    lineColor: Color,
    selectedFilter: String
) {
    if (data.isEmpty()) {
        EmptyChartView(modifier = modifier.height(120.dp))
        return
    }

    val values = data.map { it.amount }
    val minVal = values.minOrNull() ?: 0.0
    val maxVal = values.maxOrNull() ?: 0.0

    val range = (maxVal - minVal).takeIf { it > 0 } ?: 1.0

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
                else 0f

                val norm = ((point.amount - minVal) / range).toFloat()
                val y = h - (norm * h)
                Offset(x, y)
            }

            if (points.size < 2) return@Canvas

            // Smooth curve (Bezier)
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

            // AREA fill (gradient)
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

            // Curve stroke
            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        Spacer(Modifier.height(6.dp))

        // X-axis labels (safe)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val sdf = SimpleDateFormat(
                when (selectedFilter) {
                    "Day" -> "HH:mm"
                    "Week" -> "EEE"
                    "Month" -> "dd"
                    "Year" -> "MMM"
                    else -> "dd/MM"
                },
                Locale.getDefault()
            )

            val labelCount = 4
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



@Composable
fun BalanceTrendCard(
    viewModel: BalanceTabViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.balanceState.collectAsStateWithLifecycle()
    val formatter = remember { DecimalFormat("#,###.##") }

    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow), label = "CardScale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .pointerInput(Unit) {
                detectTapGestures(onPress = { isPressed = true; tryAwaitRelease(); isPressed = false })
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.6f)),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = primaryAccent, modifier = Modifier.size(32.dp))
                }
                return@Card
            }
            if (state.error != null) {
                Text("Xato: ${state.error}", color = MaterialTheme.colorScheme.error)
                return@Card
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Balance Trend",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.8f)
                )
                CircularIconButton(
                    onClick = { /* Filter Dialog */ },
                    icon = Icons.Default.FilterList,
                    contentDescription = "Filter",
                    tint = primaryAccent,
                    backgroundColor = primaryAccent.copy(alpha = 0.1f),
                    size = 32.dp
                )
            }

            Spacer(Modifier.height(16.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = formatAmountWithCurrency(state.totalBalance),
                    color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.8f),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(8.dp))

                val trendPercentage = state.trendPercentage
                val balanceColor = if (trendPercentage < 0) expenseColor else incomeColor
                val trendText = if (trendPercentage >= 0) {
                    "+${String.format("%.2f", trendPercentage)}"
                } else {
                    "${String.format("%.2f", trendPercentage)}"
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(balanceColor.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$trendText %",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = balanceColor,
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = state.periodLabel,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            BalanceTrendLineChart(
                data = state.trendData,
                lineColor = primaryAccent,
                selectedFilter = state.selectedFilter
            )

            Spacer(Modifier.height(16.dp))

            TimeFilterRow(state.selectedFilter, onFilterChange = viewModel::onFilterChange)
        }
    }
}