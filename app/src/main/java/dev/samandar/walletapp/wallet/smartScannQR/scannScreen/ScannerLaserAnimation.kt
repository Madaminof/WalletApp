import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

@Composable
fun ScannerLaserAnimation(
    modifier: Modifier = Modifier,
    laserColor: Color = Color(0xFF00F2FF), // Neon Cyan
    frameHeight: Float = 300f // Ramka bo'yi dp da
) {
    val infiniteTransition = rememberInfiniteTransition(label = "laser_transition")

    // 1. Progress: 0f dan 1f gacha silliq harakat
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "laser_progress"
    )

    // 2. Alpha: Lazerning "nafas olish" effekti
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "laser_alpha"
    )

    // Brush va ranglarni 'remember' ichiga olish optimallikni oshiradi
    val laserBrushColors = remember(laserColor, alpha) {
        listOf(
            Color.Transparent,
            laserColor.copy(alpha = alpha),
            laserColor,
            laserColor.copy(alpha = alpha),
            Color.Transparent
        )
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(frameHeight.dp)
    ) {
        val width = size.width
        val height = size.height
        val yOffset = height * progress

        // --- 1. Lazerning orqasidagi nuri (Glow Trail) ---
        // drawRect ichida gradient yaratish GPU uchun yengil
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    laserColor.copy(alpha = 0.15f * alpha),
                    Color.Transparent
                ),
                startY = yOffset - 50.dp.toPx(),
                endY = yOffset + 50.dp.toPx()
            ),
            topLeft = Offset(0f, yOffset - 50.dp.toPx()),
            size = Size(width, 100.dp.toPx())
        )

        // --- 2. Asosiy gorizontal lazer chizig'i ---
        drawLine(
            brush = Brush.horizontalGradient(laserBrushColors),
            start = Offset(0f, yOffset),
            end = Offset(width, yOffset),
            strokeWidth = 2.5.dp.toPx(),
            cap = StrokeCap.Round
        )

        // --- 3. Chetdagi nuqtalar (End-points glow) ---
        // Chap nuqta
        drawCircle(
            color = laserColor,
            radius = 3.dp.toPx(),
            center = Offset(0f, yOffset),
            alpha = alpha
        )
        // O'ng nuqta
        drawCircle(
            color = laserColor,
            radius = 3.dp.toPx(),
            center = Offset(width, yOffset),
            alpha = alpha
        )
    }
}