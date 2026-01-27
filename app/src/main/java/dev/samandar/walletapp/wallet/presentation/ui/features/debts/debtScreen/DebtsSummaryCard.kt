package dev.samandar.walletapp.wallet.presentation.ui.features.debts.debtScreen

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.ui.theme.expenseColor
import dev.samandar.walletapp.wallet.presentation.utils.formatAmountWithCurrency


@Composable
fun PremiumDebtsSummaryCard(
    totalLent: Double,
    totalBorrowed: Double
) {
    val totalBalance = totalLent - totalBorrowed
    val totalVolume = totalLent + totalBorrowed
    val progressByLent = if (totalVolume > 0) (totalLent / totalVolume).toFloat() else 0.5f

    val animatedProgress by animateFloatAsState(
        targetValue = progressByLent,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = Spring.StiffnessLow
        ),
        label = "Progress"
    )
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    val cardOffset by animateDpAsState(
        targetValue = if (isVisible) 0.dp else 40.dp,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "Entrance"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .offset(y = cardOffset)
            .graphicsLayer {
                shadowElevation = 12f
                shape = RoundedCornerShape(32.dp)
                clip = true
            },
        color = MaterialTheme.colorScheme.onPrimaryContainer,
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            (if (totalBalance >= 0) MaterialTheme.colorScheme.primary else expenseColor).copy(alpha = 0.1f),
                            Color.Transparent
                        ),
                        center = androidx.compose.ui.geometry.Offset(100f, 100f),
                        radius = 600f
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.debt_net_balance).uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 2.sp,
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.7f)
                        )

                        Text(
                            text = formatAmountWithCurrency(totalBalance),
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 26.sp,
                                letterSpacing = (-1).sp
                            ),
                            color = if (totalBalance >= 0) MaterialTheme.colorScheme.primary else expenseColor
                        )
                    }

                    PremiumStatusBadge(totalBalance >= 0)
                }

                Spacer(modifier = Modifier.height(32.dp))

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = stringResource(R.string.debt_lent),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${(progressByLent * 100).toInt()}% / ${(100 - (progressByLent * 100)).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.4f)
                        )
                        Text(
                            text = stringResource(R.string.debt_borrowed),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = expenseColor
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // "Glow" effektli Progress Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(CircleShape)
                            .background(expenseColor.copy(0.4f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedProgress)
                                .fillMaxHeight()
                                .clip(CircleShape)
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(MaterialTheme.colorScheme.primary.copy(0.8f), MaterialTheme.colorScheme.primary)
                                    )
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SummaryVerticalStat(
                        label = stringResource(R.string.debt_lent),
                        amount = totalLent,
                        color = MaterialTheme.colorScheme.primary
                    )
                    VerticalDivider(
                        modifier = Modifier.height(40.dp).width(1.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(0.3f)
                    )
                    SummaryVerticalStat(
                        label = stringResource(R.string.debt_borrowed),
                        amount = totalBorrowed,
                        color = expenseColor
                    )
                }
            }
        }
    }
}

@Composable
fun PremiumStatusBadge(isCreditor: Boolean) {
    val color = if (isCreditor) MaterialTheme.colorScheme.primary else expenseColor
    val infiniteTransition = rememberInfiniteTransition(label = "Pulse")

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse),
        label = "Alpha"
    )

    Surface(
        shape = CircleShape,
        color = color.copy(alpha = 0.1f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .graphicsLayer { this.alpha = alpha }
                    .background(color, CircleShape)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = if (isCreditor) stringResource(R.string.debt_status_creditor)
                else stringResource(R.string.debt_status_debtor),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
                color = color
            )
        }
    }
}

@Composable
fun SummaryVerticalStat(label: String, amount: Double, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
        )
        Text(
            text = formatAmountWithCurrency(amount),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
            color = color.copy(0.9f),
            fontSize = 14.sp
        )
    }
}