package dev.samandar.walletapp.wallet.presentation.ui.account.accountScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.utils.Strings

@Composable
fun WalletCardAdd(canAddMore: Boolean, onClick: () -> Unit) {
    val contentColor = if (canAddMore) {
        MaterialTheme.colorScheme.primary
    } else {
        Color.Gray.copy(0.5f)
    }

    val backgroundColor = if (canAddMore) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.03f)
    } else {
        MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.01f)
    }

    val dashEffect = remember { PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(backgroundColor)
            .clickable(enabled = canAddMore) { onClick() }
            .drawBehind {
                drawRoundRect(
                    color = contentColor.copy(alpha = 0.4f),
                    style = Stroke(
                        width = 1.5.dp.toPx(),
                        pathEffect = dashEffect
                    ),
                    cornerRadius = CornerRadius(28.dp.toPx())
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(contentColor.copy(alpha = 0.1f), CircleShape)
                    .border(0.5.dp, contentColor.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = contentColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (canAddMore)
                    stringResource(R.string.add_account_title)
                else
                    stringResource(R.string.add_account_limit),
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                    letterSpacing = 0.5.sp
                )
            )
        }
    }
}

