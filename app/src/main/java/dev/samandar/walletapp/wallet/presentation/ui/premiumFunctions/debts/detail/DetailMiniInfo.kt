package dev.samandar.walletapp.wallet.presentation.ui.premiumFunctions.debts.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DetailMiniInfo(
    label: String,
    value: String,
    isStatus: Boolean = false
) {
    Column {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        if (isStatus) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                )
            }
        } else {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
            )
        }
    }
}