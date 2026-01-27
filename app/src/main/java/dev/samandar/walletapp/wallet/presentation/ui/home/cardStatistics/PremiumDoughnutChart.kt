package dev.samandar.walletapp.wallet.presentation.ui.home.cardStatistics

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.presentation.viewmodel.chartViewmodel.CategoryData
import kotlinx.coroutines.launch

@Composable
fun PremiumDoughnutChart(
    data: List<CategoryData>,
    totalAmount: Double,
    selectedFilter: String,
    modifier: Modifier = Modifier,
    chartThickness: Dp = 22.dp
) {
    val animatedProgress = remember { Animatable(0f) }
    val scaleAlpha = remember { Animatable(0.8f) }

    LaunchedEffect(data, selectedFilter) {
        launch {
            animatedProgress.snapTo(0f)
            animatedProgress.animateTo(
                targetValue = 1f,
                animationSpec = spring(Spring.DampingRatioLowBouncy, Spring.StiffnessLow)
            )
        }
        launch {
            scaleAlpha.snapTo(0.8f)
            scaleAlpha.animateTo(1f, spring(Spring.DampingRatioMediumBouncy))
        }
    }

    val filterDisplay = remember(selectedFilter) {
        selectedFilter.replaceFirstChar { it.uppercase() }
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .graphicsLayer {
                scaleX = scaleAlpha.value
                scaleY = scaleAlpha.value
                alpha = scaleAlpha.value
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val thicknessPx = chartThickness.toPx()
            val diameter = minOf(size.width, size.height)
            val rectSize = Size(diameter - thicknessPx, diameter - thicknessPx)
            val topLeft = Offset(
                (size.width - rectSize.width) / 2,
                (size.height - rectSize.height) / 2
            )

            drawCircle(
                color = Color.Gray.copy(alpha = 0.05f),
                radius = rectSize.width / 2,
                center = center,
                style = Stroke(width = thicknessPx)
            )

            var startAngle = -90f
            data.forEach { item ->
                val sweepAngle = (item.amount / totalAmount).toFloat() * 360f

                drawArc(
                    color = item.color.copy(alpha = 0.15f),
                    startAngle = startAngle,
                    sweepAngle = sweepAngle * animatedProgress.value,
                    useCenter = false,
                    topLeft = topLeft,
                    size = rectSize,
                    style = Stroke(width = thicknessPx + 4.dp.toPx(), cap = StrokeCap.Round)
                )

                drawArc(
                    color = item.color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle * animatedProgress.value,
                    useCenter = false,
                    topLeft = topLeft,
                    size = rectSize,
                    style = Stroke(width = thicknessPx, cap = StrokeCap.Round)
                )
                startAngle += sweepAngle
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = filterDisplay,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                ),
                color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.6f)
            )
            Text(
                text = stringResource(Strings.statistics),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 8.sp,
                    fontWeight = FontWeight.ExtraBold
                ),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
            )
        }
    }
}