package dev.samandar.walletapp.wallet.presentation.ui.home.totalBalanceCardPremium

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.samandar.walletapp.R
import dev.samandar.walletapp.ui.theme.expenseColor
import dev.samandar.walletapp.ui.theme.incomeColor
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.presentation.utils.FormatAmount
import kotlin.math.absoluteValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.style.TextAlign
import dev.samandar.walletapp.wallet.presentation.ui.charts.categoryStatistic.FilterActionButton
import dev.samandar.walletapp.wallet.presentation.ui.home.totalBalanceCard.EmptyChartView
import dev.samandar.walletapp.wallet.presentation.ui.home.totalBalanceCard.PeriodNavigationButton
import dev.samandar.walletapp.wallet.presentation.ui.home.totalBalanceCard.TimeFilterRow
import dev.samandar.walletapp.wallet.presentation.ui.home.totalBalanceCard.TotalBalanceViewModel
import dev.samandar.walletapp.wallet.presentation.ui.home.totalBalanceCard.UniversalAccountFilterMenu
import dev.samandar.walletapp.wallet.presentation.ui.home.totalBalanceCard.primaryAccent


@Composable
fun TotalBalanceCardPremium(
    viewModel: TotalBalanceViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.cardState.collectAsState()
    var isMenuExpanded by remember { mutableStateOf(false) }

    val balanceColor = if (state.netBalance >= 0) incomeColor else expenseColor

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = primaryAccent, modifier = Modifier.size(32.dp))
                }
                return@Card
            }
            Row(
                modifier = Modifier.fillMaxWidth().height(32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Strings.total_balance),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.8f)
                )

                Box(contentAlignment = Alignment.TopEnd) {
                    FilterActionButton(
                        onClick = { isMenuExpanded = true },
                        icon = R.drawable.filter_ic,
                        size = 28.dp
                    )

                    UniversalAccountFilterMenu(
                        isExpanded = isMenuExpanded,
                        onDismiss = { isMenuExpanded = false },
                        accounts = state.accounts,
                        selectedAccountIds = state.selectedAccountIds,
                        onAccountSelectionChange = viewModel::onAccountSelectionChange
                    )
                }
            }
            Spacer(Modifier.height(4.dp))

            Text(
                text = "${if (state.netBalance < 0) "-" else ""}${FormatAmount(state.netBalance.absoluteValue)}",
                color = balanceColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(2.dp))

            // --- PERIOD NAVIGATION ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                PeriodNavigationButton(
                    icon = R.drawable.back_ic,
                    onClick = { viewModel.onPeriodNavigate(forward = false) },
                    size = 42.dp
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(28.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(0.05f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.periodLabel,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onTertiary.copy(0.7f)
                    )
                }
                PeriodNavigationButton(
                    icon = R.drawable.arrow_right,
                    onClick = { viewModel.onPeriodNavigate(forward = true) },
                    size = 42.dp
                )
            }

            if (state.isLoading) {
                Box(Modifier.fillMaxWidth().height(160.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(strokeWidth = 2.dp)
                }
            }else if (state.netBalance == 0.0) {
                    EmptyChartView(
                        modifier = Modifier.fillMaxWidth().height(160.dp).padding(bottom = 10.dp)
                    )
                } else {
                BalanceLineChartPremium(
                    data = state.chartPoints,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            TimeFilterRow(
                selectedKey = state.selectedFilter,
                onFilterChange = viewModel::onFilterChange
            )
        }
    }
}