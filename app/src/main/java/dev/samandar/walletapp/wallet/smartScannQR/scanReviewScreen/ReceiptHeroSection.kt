package dev.samandar.walletapp.wallet.smartScannQR.scanReviewScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.domain.model.smartScannModel.Receipt
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun ReceiptHeroSection(receipt: Receipt) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(72.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(R.drawable.shopp_list_ic),
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = receipt.merchantName ?: stringResource(Strings.unknown_merchant),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black,fontSize = 20.sp),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp),
            color = MaterialTheme.colorScheme.primary.copy(0.8f)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = String.format(Locale.US, "%,.0f UZS", receipt.totalAmount),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = (-1).sp,
                color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                fontSize = 24.sp
            )
        )
    }
}


@Composable
fun ReceiptDetailCard(receipt: Receipt) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            DetailRow(
                icon = Icons.Default.CalendarToday,
                label = stringResource(Strings.date_time),
                value = formatDate(receipt.date),
                isSingleLine = true
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                thickness = 0.5.dp,
                color = Color.LightGray.copy(0.3f)
            )
            DetailRow(
                icon = Icons.Default.LocationOn,
                label = stringResource(Strings.label_address),
                value = receipt.merchantAddress ?: stringResource(Strings.unknown_address),
                isSingleLine = false
            )
        }
    }
}

@Composable
fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    isSingleLine: Boolean = true,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = if (isSingleLine) Alignment.CenterVertically else Alignment.Top
    ) {
        Row(
            modifier = Modifier.width(100.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
            ),
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f),
            maxLines = if (isSingleLine) 1 else 5,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
        )
    }
}


fun formatDate(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("d-MMMM, yyyy HH:mm", Locale("uz"))
        val date = Date(timestamp)
        sdf.format(date)
    } catch (e: Exception) {
        "Noma'lum sana"
    }
}