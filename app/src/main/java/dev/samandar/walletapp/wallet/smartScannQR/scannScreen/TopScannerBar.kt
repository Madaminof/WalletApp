package dev.samandar.walletapp.wallet.smartScannQR.scannScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.utils.Strings


@Composable
fun TopScannerBar(
    onClose: () -> Unit,
    onFlashToggle: (Boolean) -> Unit = {}, // Chiroq funksiyasi uchun
    modifier: Modifier = Modifier
) {
    var isFlashOn by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = 12.dp, start = 16.dp, end = 16.dp)
    ) {
        // 1. Chap tomonda: Yopish tugmasi
        Surface(
            modifier = Modifier.align(Alignment.CenterStart),
            color = Color.Black.copy(alpha = 0.4f),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // 2. Markazda: Ma'lumot matni
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(Strings.scan_qr_code),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                ),
                color = Color.White
            )

            // Premium kichik indicator
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(Color.Green, CircleShape) // "Live" status belgisi
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = stringResource(Strings.tax_receipt_required).uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 9.sp
                    ),
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }

        // 3. O'ng tomonda: Chiroq (Flashlight) tugmasi
        Surface(
            modifier = Modifier.align(Alignment.CenterEnd),
            color = if (isFlashOn) Color.White else Color.Black.copy(alpha = 0.4f),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
        ) {
            IconButton(
                onClick = {
                    isFlashOn = !isFlashOn
                    onFlashToggle(isFlashOn)
                },
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    painter = painterResource(
                        id = if (isFlashOn) R.drawable.flash_on else R.drawable.flash_ff
                    ),
                    contentDescription = "Flash",
                    tint = if (isFlashOn) Color.Black else Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}