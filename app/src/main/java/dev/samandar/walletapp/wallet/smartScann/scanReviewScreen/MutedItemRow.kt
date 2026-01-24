package dev.samandar.walletapp.wallet.smartScann.scanReviewScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.wallet.domain.model.smartScannModel.ReceiptItem
import java.util.Locale


@Composable
fun MutedItemRow(
    item: ReceiptItem,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onTertiary.copy(0.6f)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${item.quantity.toInt()} × ${String.format(Locale.US, "%,.0f", item.unitPrice)} UZS",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onTertiary.copy(0.4f)

            )
        }

        Text(
            text = String.format(Locale.US, "%,.0f UZS", item.totalPrice),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onTertiary.copy(0.6f)

            ),
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}