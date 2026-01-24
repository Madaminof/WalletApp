package dev.samandar.walletapp.wallet.presentation.ui.budjets.budgetDetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.domain.model.BudgetStatus
import dev.samandar.walletapp.wallet.presentation.ui.budjets.addBudget.toLocalizedString
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun SecondaryInfoCard(status: BudgetStatus) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.8f)
    ){
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BudgetInfoSection(status)
        }
    }
}

@Composable
private fun BudgetInfoSection(status: BudgetStatus) {
    val budget = status.budget
    val dateFormatter = remember { SimpleDateFormat("d-MMM, HH:mm", Locale.getDefault()) }

    DetailRowItem(
        label = stringResource(R.string.budget_detail_label_period),
        value = budget.period.toLocalizedString().uppercase(),
    )
    DetailRowItem(
        label = stringResource(R.string.budget_detail_label_start_date),
        value = dateFormatter.format(Date(budget.startDate)),
    )
    budget.endDate?.let { endDate ->
        DetailRowItem(
            label = stringResource(R.string.budget_detail_label_end_date),
            value = dateFormatter.format(Date(endDate)),
        )
    }
    DetailRowItem(
        label = stringResource(R.string.budget_detail_label_status),
        value = if (budget.isActive) stringResource(R.string.budget_detail_status_active) else stringResource(R.string.budget_detail_status_inactive),
    )
    DetailRowItem(
        label = stringResource(R.string.budget_detail_label_remaining_days),
        value = stringResource(R.string.budget_detail_days_unit, status.daysRemaining),
    )
}
