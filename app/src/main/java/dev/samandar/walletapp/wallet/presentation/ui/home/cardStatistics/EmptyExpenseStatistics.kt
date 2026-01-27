package dev.samandar.walletapp.wallet.presentation.ui.home.cardStatistics
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PieChartOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.model.content.CircleShape
import dev.samandar.walletapp.R


@Composable
fun EmptyExpenseStatistics(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "EmptyStatAnim")

    // Aylanma harakat uchun
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "rotation"
    )

    // Pulsatsiya effekti
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "pulse"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .graphicsLayer {
                    scaleX = pulseScale
                    scaleY = pulseScale
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { rotationZ = rotation }
            ) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color.Gray.copy(0.05f),
                            Color.Gray.copy(0.2f),
                            Color.Gray.copy(0.05f)
                        )
                    ),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(
                        width = 3.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f),
                        cap = StrokeCap.Round
                    )
                )

                drawCircle(
                    color = Color.Gray.copy(0.3f),
                    radius = 4.dp.toPx(),
                    center = androidx.compose.ui.geometry.Offset(size.width, size.height / 2)
                )
            }

            Surface(
                modifier = Modifier.size(50.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                border = BorderStroke(1.dp, Color.Gray.copy(0.1f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(R.drawable.chart_ic),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(dev.samandar.walletapp.utils.Strings.empty_chart_view),
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp,
                letterSpacing = 0.5.sp
            ),
            color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Text(
            text = stringResource(R.string.empty_chart_hint),
            style = MaterialTheme.typography.bodySmall.copy(
                lineHeight = 14.sp
            ),
            color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.4f),
            fontSize = 11.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp, start = 32.dp, end = 32.dp)
        )
    }
}