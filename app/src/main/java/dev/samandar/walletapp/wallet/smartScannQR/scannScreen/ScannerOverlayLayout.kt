package dev.samandar.walletapp.wallet.smartScannQR.scannScreen

import ScannerLaserAnimation
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.utils.Strings

@Composable
fun ScannerOverlayLayout(isProcessing: Boolean) {
    Box(modifier = Modifier.fillMaxSize()) {

        // 1. Markaziy skanerlash maydoni
        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.Center)
        ) {
            // Burchaklar (isProcessing bo'lganda rang o'zgarishi silliq bo'ladi)
            ScannerCorners(
                color = if (isProcessing) Color.Gray else MaterialTheme.colorScheme.primary
            )

            // Lazer faqat skanerlash paytida ko'rinadi
            if (!isProcessing) {
                ScannerLaserAnimation(frameHeight = 280f)
            }
        }

        // 2. Optimallashtirilgan Scrim (Atrofdagi qorong'ulash)
        // Path() obyektini remember ichiga olamiz, har safar yangi yaratilmasligi uchun
        val scrimColor = Color.Black.copy(alpha = 0.6f)

        Canvas(modifier = Modifier.fillMaxSize()) {
            val rectSize = 280.dp.toPx()
            val left = (size.width - rectSize) / 2
            val top = (size.height - rectSize) / 2

            val holePath = Path().apply {
                addRoundRect(
                    RoundRect(
                        rect = Rect(left, top, left + rectSize, top + rectSize),
                        cornerRadius = CornerRadius(24.dp.toPx())
                    )
                )
            }

            // Markazni kesib tashlab, qolgan joyni bo'yash (Clip Difference)
            clipPath(holePath, clipOp = ClipOp.Difference) {
                drawRect(scrimColor)
            }
        }
    }
}

@Composable
fun ProcessingOverlay() {
    // Shisha effekti uchun animatsiya
    val infiniteTransition = rememberInfiniteTransition(label = "processing")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Reverse
        ), label = "alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f * alpha)) // Yarim shaffof fon
            .blur(12.dp), // Premium Blur effekti (Android 12+)
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(32.dp)
        ) {
            // Chiroyli silliq indikator
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp,
                modifier = Modifier.size(56.dp),
                strokeCap = StrokeCap.Round // Yumaloq uchli indikator
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(Strings.fetching_data),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                ),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(Strings.please_wait),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}