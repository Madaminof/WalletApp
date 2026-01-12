package dev.samandar.walletapp.wallet.presentation.ui.premiumFunctions.debts.debtScreen

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.ui.theme.expenseColor
import dev.samandar.walletapp.ui.theme.incomeColor
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.presentation.utils.formatAmountWithCurrency

@Composable
fun DebtsSummaryCard(
    totalLent: Double,
    totalBorrowed: Double
) {
    val totalBalance = totalLent - totalBorrowed
    val totalVolume = totalLent + totalBorrowed
    val progressByLent = if (totalVolume > 0) (totalLent / totalVolume).toFloat() else 0.5f

    val animatedProgress by animateFloatAsState(
        targetValue = progressByLent,
        animationSpec = tween(1200, easing = FastOutSlowInEasing),
        label = "DebtProgress"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(32.dp),
        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.5f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)),
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = stringResource(Strings.debt_net_balance),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatAmountWithCurrency(totalBalance),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-1).sp,
                            fontSize = 24.sp
                        ),
                        color = if (totalBalance >= 0) incomeColor else expenseColor
                    )
                }
                Surface(
                    shape = CircleShape,
                    color = (if (totalBalance >= 0) incomeColor else expenseColor).copy(alpha = 0.1f)
                ) {
                    Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Text(
                            text = if (totalBalance >= 0) stringResource(Strings.debt_status_creditor) else stringResource(Strings.debt_status_debtor),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (totalBalance >= 0) incomeColor else expenseColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text =  stringResource(Strings.debt_lent),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.6f)
                    )
                    Text(
                        text = formatAmountWithCurrency(totalLent),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = incomeColor.copy(0.8f)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(Strings.debt_borrowed),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.6f)
                    )
                    Text(
                        text = formatAmountWithCurrency(totalBorrowed),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = expenseColor.copy(0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(expenseColor.copy(alpha = 0.4f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(incomeColor, incomeColor.copy(alpha = 0.7f))
                            )
                        )
                )
            }
        }
    }
}
