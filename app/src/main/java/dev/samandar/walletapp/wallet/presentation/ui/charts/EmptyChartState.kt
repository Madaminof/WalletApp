package dev.samandar.walletapp.wallet.presentation.ui.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.ui.res.painterResource
import dev.samandar.walletapp.R


enum class ChartTab(val titleResId: Int) {
    BALANCE(R.string.tab_balance),
    EXPENSE(R.string.tab_expense),
    INCOME(R.string.tab_income),
    REPORTS(R.string.tab_reports)
}

@Composable
fun EmptyChartState(
    currentTab: ChartTab,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "empty_shimmer")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(100.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawArc(
                    color = Color.Gray.copy(alpha = 0.15f),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(
                        width = 8f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                )
            }
            Icon(
                painter = painterResource(R.drawable.chart_ic),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                modifier = Modifier.size(60.dp)
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = if (currentTab == ChartTab.EXPENSE)
                stringResource(R.string.empty_expenses)
            else
                stringResource(R.string.empty_incomes),
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            ),
            color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(R.string.add_transactions_to_see_chart),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            ),
            color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.4f),
            modifier = Modifier.padding(top = 4.dp),
            textAlign = TextAlign.Center
        )
    }
}