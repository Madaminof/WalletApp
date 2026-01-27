package dev.samandar.walletapp.wallet.presentation.ui.budjets

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.dp

@Composable
fun PremiumCustomLinearProgressIndicator(
    progressFloat: Float,
    progressColor: Color,
    trackColor: Color,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(10.dp),
) {
    val isOverLimit = progressFloat >= 1.0f
    val displayProgress = progressFloat.coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = displayProgress,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "ProgressAnim"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "Pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseAlpha"
    )

    val shimmerTranslate by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2000f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Shimmer"
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val radius = CornerRadius(height / 2, height / 2)

        drawRoundRect(
            color = trackColor.copy(alpha = 0.05f),
            size = size,
            cornerRadius = radius
        )

        clipRect(right = width * animatedProgress) {

            val mainGradient = Brush.linearGradient(
                colors = if (isOverLimit) {
                    listOf(Color(0xFFFF5252), Color(0xFFFF1744))
                } else {
                    listOf(progressColor.copy(alpha = 0.8f), progressColor)
                },
                start = Offset(0f, 0f),
                end = Offset(width, 0f)
            )

            drawRoundRect(
                brush = mainGradient,
                size = size,
                cornerRadius = radius,
                alpha = if (isOverLimit) pulseAlpha else 1.0f
            )

            val shimmerBrush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0f),
                    Color.White.copy(alpha = 0.3f),
                    Color.White.copy(alpha = 0f)
                ),
                start = Offset(shimmerTranslate - 500f, 0f),
                end = Offset(shimmerTranslate, 0f)
            )

            drawRoundRect(
                brush = shimmerBrush,
                size = size,
                cornerRadius = radius
            )
        }

        if (isOverLimit) {
            drawRoundRect(
                color = Color.Red.copy(alpha = 0.2f * pulseAlpha),
                size = size,
                cornerRadius = radius,
                style = Stroke(width = 4.dp.toPx())
            )
        }
    }
}