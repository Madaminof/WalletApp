package dev.samandar.walletapp.wallet.smartScann.scannScreen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.dp
@Composable
fun ScannerLaserAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val translateY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 280f, // Ramka bo'yi
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(2.dp)
            .offset(y = translateY.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Cyan, Color.Transparent)
                )
            )
    )
}