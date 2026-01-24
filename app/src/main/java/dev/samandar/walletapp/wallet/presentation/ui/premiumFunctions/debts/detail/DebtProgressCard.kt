package dev.samandar.walletapp.wallet.presentation.ui.premiumFunctions.debts.detail

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.ui.theme.incomeColor
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.domain.model.debt.Debt
import dev.samandar.walletapp.wallet.presentation.utils.formatAmountWithCurrency

@Composable
fun DebtProgressCard(debt: Debt, accentColor: Color) {
    val progress = if (debt.totalAmount > 0)
        ((debt.totalAmount - debt.remainingAmount) / debt.totalAmount).toFloat().coerceIn(0f, 1f)
    else 0f

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = accentColor.copy(alpha = 0.04f),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        stringResource(R.string.remaining_amount).uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                        color = MaterialTheme.colorScheme.onTertiary.copy(0.6f)
                    )
                    Text(
                        formatAmountWithCurrency(debt.remainingAmount),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 26.sp
                        ),
                        color = accentColor
                    )
                }

                // Progress Circle/Percentage
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.size(54.dp),
                        color = accentColor.copy(0.1f),
                        strokeWidth = 6.dp,
                        strokeCap = StrokeCap.Round
                    )
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.size(54.dp),
                        color = accentColor,
                        strokeWidth = 6.dp,
                        strokeCap = StrokeCap.Round
                    )
                    Text(
                        "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                        color = MaterialTheme.colorScheme.onTertiary.copy(0.5f)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Sub-info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DetailMiniInfo(stringResource(R.string.total_amount_label), formatAmountWithCurrency(debt.totalAmount))
                if (debt.isSettled) {
                    StatusBadge(stringResource(R.string.debt_status_settled), incomeColor)
                } else {
                    DetailMiniInfo(stringResource(Strings.detail_status_label), stringResource(Strings.status_pending), isStatus = true)
                }
            }
        }
    }
}