package dev.samandar.walletapp.wallet.presentation.ui.budjets.budgetDetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.domain.model.BudgetStatus
import dev.samandar.walletapp.wallet.presentation.ui.budjets.PremiumCustomLinearProgressIndicator
import dev.samandar.walletapp.wallet.presentation.ui.charts.detailScreen.DashedDivider
import dev.samandar.walletapp.wallet.presentation.ui.charts.detailScreen.IconBox
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.categories.getTranslatedName
import dev.samandar.walletapp.wallet.presentation.utils.FormatAmount


@Composable
fun MainBudgetCard(status: BudgetStatus) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.8f)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BudgetHeaderSection(status)

            DashedDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = Color.LightGray.copy(alpha = 0.6f)
            )

            BudgetSummaryCard(status = status)
        }
    }
}


@Composable
fun BudgetHeaderSection(
    status: BudgetStatus,
) {
    val budget = status.budget
    val categoryName = getTranslatedName(budget.category.name)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconBox(
                iconRes = budget.category.iconResId,
                color = Color(budget.category.colorArgb),
            )
            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = categoryName.toString(),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 16.sp
            )
        }
    }
}



@Composable
fun BudgetSummaryCard(status: BudgetStatus) {

    val progressValue = (status.percentageUsed / 100).toFloat().coerceIn(0f, 1f)
    val remaining = remember(status.budget.maxAmount, status.spentAmount) {
        status.budget.maxAmount - status.spentAmount
    }
    val progressColor = remember(remaining, progressValue) {
        when {
            remaining < 0 -> Color(0xFFE57373)
            progressValue >= 0.9f -> Color(0xFFFFB74D)
            else -> Color(0xFF81C784)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            DetailSummaryRow(
                label = stringResource(R.string.budget_summary_label_max_budget),
                value = FormatAmount(status.budget.maxAmount),
                valueColor = Color(0xFF66BB6A)
            )
            Divider(Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            DetailSummaryRow(
                label = stringResource(R.string.budget_summary_label_spent_amount),
                value = FormatAmount(status.spentAmount),
                valueColor = Color(0xFFE57373)
            )
            Divider(Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            DetailSummaryRow(
                label = stringResource(R.string.budget_summary_label_remaining_amount),
                value = FormatAmount(status.remainingAmount),
                valueColor = if (status.isOverBudget) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(8.dp))

            PremiumCustomLinearProgressIndicator(
                progressFloat = progressValue,
                progressColor = progressColor,
                trackColor = MaterialTheme.colorScheme.onTertiary.copy(0.01f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.budget_summary_progress_label, status.percentageUsed.toInt()),
                modifier = Modifier.align(Alignment.End),
                color = MaterialTheme.colorScheme.onTertiary.copy(0.7f),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Normal
                ),
                fontSize = 10.sp

            )
            Text(
                text = stringResource(
                    R.string.budget_daily_limit,
                    FormatAmount(status.dailyLimit)
                ),
                color = progressColor,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                fontSize = 12.sp
            )
        }
    }
}


@Composable
fun DetailSummaryRow(label: String, value: String, valueColor: Color = MaterialTheme.colorScheme.onPrimaryContainer) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onTertiary.copy(0.7f),
                fontWeight = FontWeight.Normal
            ),
            fontSize = 13.sp
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                color = valueColor
            ),
            fontSize = 12.sp
        )
    }
}
