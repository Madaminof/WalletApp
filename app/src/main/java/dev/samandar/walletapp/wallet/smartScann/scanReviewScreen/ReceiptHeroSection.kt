package dev.samandar.walletapp.wallet.smartScann.scanReviewScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.wallet.domain.model.smartScannModel.Receipt
import java.util.Locale


@Composable
fun ReceiptHeroSection(receipt: Receipt) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.ReceiptLong, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(30.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = receipt.merchantName ?: "Xarid cheki",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium, color = Color.Gray)
        )
        Text(
            text = String.format(Locale.US, "%,.0f UZS", receipt.totalAmount),
            style = MaterialTheme.typography.displayMedium.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = (-1.5).sp,
                color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                fontSize = 32.sp
            )
        )
    }
}
