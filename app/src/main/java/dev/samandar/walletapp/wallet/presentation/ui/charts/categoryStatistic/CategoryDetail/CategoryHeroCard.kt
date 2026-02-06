package dev.samandar.walletapp.wallet.presentation.ui.charts.categoryStatistic.CategoryDetail

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.ui.theme.expenseColor
import dev.samandar.walletapp.ui.theme.incomeColor
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.domain.model.TransactionType
import dev.samandar.walletapp.wallet.presentation.utils.formatAmountWithCurrency

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CategoryHeroCard(
    totalAmount: Double,
    count: Int,
    transactionType: TransactionType,
    periodLabel: String = "o'tgan oyga nisbatan",
    peakAmount: Double,
    peakDate: java.time.LocalDate?,
) {
    val isExpense = transactionType == TransactionType.EXPENSE
    val dateFormatter = java.time.format.DateTimeFormatter.ofPattern("d-MMM", java.util.Locale.ENGLISH)
    val formattedPeakDate = peakDate?.format(dateFormatter) ?: "---"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        // 1. Title & Icon Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = if (isExpense) stringResource(Strings.total_expense) else stringResource(Strings.total_income),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.8f)
                )
                Text(
                    text = periodLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.5f)
                )
            }
            Icon(
                painter = painterResource(if (isExpense) R.drawable.up_right_expense else R.drawable.up_down_income),
                contentDescription = null,
                modifier = Modifier
                    .size(28.dp),
                tint = if (isExpense) expenseColor.copy(0.8f) else incomeColor.copy(0.8f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = formatAmountWithCurrency(totalAmount),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-1).sp,
                fontSize = 28.sp
            ),
            color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Count
            StatItem(
                label = stringResource(Strings.transactions_count),
                value =  stringResource(R.string.count_format, count),
                modifier = Modifier.weight(1f)
            )
            // Divider
            Box(
                modifier = Modifier
                    .height(30.dp)
                    .width(1.dp)
                    .background(MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.1f))
            )
            Column(
                modifier = Modifier
                    .weight(1.5f)
                    .padding(start = 20.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isExpense) stringResource(Strings.max_expense) else  stringResource(Strings.max_income),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.5f)
                    )
                    Text(
                        text = " | $formattedPeakDate",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.5f),
                    )
                }
                Text(
                    text = formatAmountWithCurrency(peakAmount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isExpense) expenseColor else incomeColor
                )
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, modifier: Modifier) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.5f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
        )
    }
}