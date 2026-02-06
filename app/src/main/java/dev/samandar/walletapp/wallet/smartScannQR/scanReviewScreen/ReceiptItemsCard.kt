package dev.samandar.walletapp.wallet.smartScannQR.scanReviewScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.samandar.walletapp.wallet.domain.model.smartScannModel.ReceiptItem
import java.util.*


@Composable
fun ReceiptItemsCard(items: List<ReceiptItem>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                items.forEachIndexed { index, item ->
                    ItemRow(item)
                    if (index < items.lastIndex) {
                        DashedDivider(
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
        ZigZagEdge(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
        )
    }
}

@Composable
private fun ItemRow(item: ReceiptItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (item.quantity > 1) {
                Text(
                    text = "${item.quantity.toInt()} x ${String.format(Locale.US, "%,.0f", item.totalPrice)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }

        Text(
            text = String.format(Locale.US, "%,.0f", item.totalPrice),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
            )
        )
    }
}

@Composable
fun DashedDivider(modifier: Modifier = Modifier) {
    val color = Color.LightGray.copy(alpha = 0.5f)
    androidx.compose.foundation.Canvas(modifier) {
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(size.width, 0f),
            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
            strokeWidth = 2f
        )
    }
}

@Composable
fun ZigZagEdge(modifier: Modifier = Modifier) {
    val color = MaterialTheme.colorScheme.onPrimaryContainer
    androidx.compose.foundation.Canvas(modifier = modifier) {
        val triangleWidth = 20f
        val triangleHeight = 15f
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(0f, 0f)
            var x = 0f
            while (x < size.width) {
                lineTo(x + triangleWidth / 2, triangleHeight)
                lineTo(x + triangleWidth, 0f)
                x += triangleWidth
            }
            lineTo(size.width, 0f)
            close()
        }
        drawPath(path, color)
    }
}