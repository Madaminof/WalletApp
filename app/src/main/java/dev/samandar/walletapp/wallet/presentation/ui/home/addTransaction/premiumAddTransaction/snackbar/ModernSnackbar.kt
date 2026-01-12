package dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.snackbar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.ui.theme.expenseColor
import dev.samandar.walletapp.ui.theme.incomeColor

data class MySnackbarVisuals(
    override val message: String,
    override val actionLabel: String? = null,
    override val duration: SnackbarDuration = SnackbarDuration.Short,
    override val withDismissAction: Boolean = false,
    val isError: Boolean = false
) : SnackbarVisuals

@Composable
fun ModernSnackbar(snackbarData: SnackbarData) {
    val view = androidx.compose.ui.platform.LocalView.current

    val customVisuals = snackbarData.visuals as? MySnackbarVisuals
    val isError = customVisuals?.isError ?: false

    val mainColor = if (isError) expenseColor else incomeColor
    val icon = if (isError) Icons.Rounded.Info else Icons.Rounded.CheckCircle

    LaunchedEffect(Unit) {
        val feedbackType = if (isError) {
            android.view.HapticFeedbackConstants.REJECT
        } else {
            android.view.HapticFeedbackConstants.CONFIRM
        }
        view.performHapticFeedback(feedbackType)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Surface(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .graphicsLayer {
                    shadowElevation = 32f
                    shape = CircleShape
                },
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            shape = CircleShape,
        ) {
            Row(
                modifier = Modifier
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(mainColor.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = mainColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                Text(
                    text = snackbarData.visuals.message,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onTertiary.copy(0.8f) // Matn rangi doim ochiq bo'lishi uchun
                    ),
                    maxLines = 1,
                    modifier = Modifier.weight(1f, fill = false) // Uzun matn sig'masa qisqaradi
                )

                // ACTION BUTTON (Agar bo'lsa)
                snackbarData.visuals.actionLabel?.let { label ->
                    Spacer(Modifier.width(12.dp))
                    VerticalDivider(
                        modifier = Modifier.height(16.dp),
                        thickness = 0.5.dp,
                        color = Color.Gray.copy(0.4f)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = label,
                        color = mainColor,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        ),
                        modifier = Modifier.clickable { snackbarData.performAction() }
                    )
                }
            }
        }
    }
}