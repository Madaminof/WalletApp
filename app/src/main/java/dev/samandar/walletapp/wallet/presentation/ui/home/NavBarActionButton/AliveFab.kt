package dev.samandar.walletapp.wallet.presentation.ui.home.NavBarActionButton

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp


@Composable
fun AliveFab(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.88f else 1f)

    val infiniteTransition = rememberInfiniteTransition(label = "FabAnim")
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -6f,
        animationSpec = infiniteRepeatable(tween(1500, easing = EaseInOutSine), RepeatMode.Reverse)
    )

    Box(
        modifier = Modifier
            .graphicsLayer {
                translationY = floatAnim
                scaleX = scale
                scaleY = scale
            }
            .size(58.dp),
        contentAlignment = Alignment.Center
    ) {
        // Aura Effect
        val auraScale by infiniteTransition.animateFloat(
            initialValue = 1f, targetValue = 1.25f,
            animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { scaleX = auraScale; scaleY = auraScale; alpha = 0.15f }
                .background(color, CircleShape)
        )

        Surface(
            modifier = Modifier.fillMaxSize().clickable(interactionSource, null) { onClick() },
            shape = CircleShape,
            color = color,
            border = BorderStroke(1.dp, Color.White.copy(0.2f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = Color.White, modifier = Modifier.size(28.dp))
            }
        }
    }
}