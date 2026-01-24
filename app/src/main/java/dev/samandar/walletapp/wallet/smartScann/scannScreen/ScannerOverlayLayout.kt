package dev.samandar.walletapp.wallet.smartScann.scannScreen

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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import dev.samandar.walletapp.utils.Strings

@Composable
fun ScannerOverlayLayout(isProcessing: Boolean) {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .size(280.dp)
                .align(Alignment.Center)
        ) {
            ScannerCorners(color = if (isProcessing) Color.Gray else MaterialTheme.colorScheme.primary)

            if (!isProcessing) {
                ScannerLaserAnimation()
            }
        }

        // Atrofdagi qorong'ulash (Scrim)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val circlePath = Path().apply {
                addRoundRect(
                    RoundRect(
                        rect = Rect(center, 280.dp.toPx() / 2),
                        cornerRadius = CornerRadius(24.dp.toPx())
                    )
                )
            }
            clipPath(circlePath, clipOp = ClipOp.Difference) {
                drawRect(Color.Black.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
fun ProcessingOverlay() {
    // Kamera ustini xira qilish (Blur effekt)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Chiroyli loading (Lottie ishlatsangiz ham bo'ladi)
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp,
                modifier = Modifier.size(50.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(Strings.fetching_data),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = stringResource(Strings.please_wait),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}