package dev.samandar.walletapp.wallet.presentation.ui.account.accountScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import dev.samandar.walletapp.utils.Strings

@Composable
fun WalletCardAdd(canAddMore: Boolean, onClick: () -> Unit) {
    val strokeColor = if (canAddMore) MaterialTheme.colorScheme.primary.copy(0.4f)
    else MaterialTheme.colorScheme.onTertiary.copy(0.1f)

    val dashEffect = remember { PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .drawBehind {
                drawRoundRect(
                    color = strokeColor,
                    style = Stroke(width = 2.dp.toPx(), pathEffect = dashEffect),
                    cornerRadius = CornerRadius(24.dp.toPx())
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .background(strokeColor.copy(alpha = 0.05f), CircleShape)
                    .padding(8.dp),
                tint = if (canAddMore) MaterialTheme.colorScheme.primary else Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (canAddMore) stringResource(Strings.add_account_title) else stringResource(Strings.add_account_limit),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = if (canAddMore) MaterialTheme.colorScheme.primary else Color.Gray
                )
            )
        }
    }
}