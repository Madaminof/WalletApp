package dev.samandar.walletapp.wallet.presentation.ui.charts.categoryStatistic.CategoryDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.domain.model.TransactionType
import dev.samandar.walletapp.wallet.presentation.utils.formatAmountWithCurrency

@Composable
fun CategoryAnalysisHeader(
    totalAmount: Double,
    count: Int,
    avgAmount: Double,
    maxAmount: Double,
    color: Color,
    transactionType: TransactionType,
) {
    val labelText =
        if (transactionType == TransactionType.EXPENSE) stringResource(Strings.total_expense) else stringResource(
            Strings.total_income
        )
    val maxLabelText =
        if (transactionType == TransactionType.EXPENSE) stringResource(Strings.max_expense) else stringResource(
            Strings.max_income
        )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Column(modifier = Modifier.padding(start = 4.dp)) {
            Text(
                text = labelText.uppercase(),
                style = MaterialTheme.typography.titleSmall,
                color = Color.Gray,
                letterSpacing = 1.sp
            )
            Text(
                text = formatAmountWithCurrency(totalAmount),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onTertiary,
                letterSpacing = (-1).sp
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AnalysisCard(
                label = stringResource(Strings.transactions_count),
                value = stringResource(R.string.count_format, count),
                icon = Icons.Default.TrendingUp,
                color = color,
                modifier = Modifier.weight(1f)
            )
            AnalysisCard(
                label = stringResource(Strings.average_value),
                value = formatAmountWithCurrency(avgAmount),
                icon = Icons.Default.BarChart,
                color = color,
                modifier = Modifier.weight(1.2f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.8f),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(color, CircleShape)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = maxLabelText,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                    Text(
                        text = formatAmountWithCurrency(maxAmount),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun AnalysisCard(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier,
) {
    Surface(
        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.8f),
        shape = RoundedCornerShape(24.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(color.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
            )
        }
    }
}