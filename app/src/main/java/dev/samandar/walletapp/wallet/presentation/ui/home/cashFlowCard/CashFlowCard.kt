package dev.samandar.walletapp.wallet.presentation.ui.home

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.samandar.walletapp.R
import dev.samandar.walletapp.ui.theme.expenseColor
import dev.samandar.walletapp.ui.theme.incomeColor
import dev.samandar.walletapp.utils.FilterKeys
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.presentation.ui.charts.categoryStatistic.FilterActionButton
import dev.samandar.walletapp.wallet.presentation.ui.home.cashFlowCard.CashFlowFilterMenu
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencyManager
import dev.samandar.walletapp.wallet.presentation.utils.FormatAmount
import dev.samandar.walletapp.wallet.presentation.viewmodel.CashFlowViewModel

val activeCurrency by CurrencyManager.currentCurrency

@Composable
fun CashFlowItem(
    icon: Int,
    label: String,
    amount: Double,
    itemColor: Color,
    isTotal: Boolean = false
) {
    val displayColor = if (isTotal) {
        if (amount < 0) expenseColor else incomeColor
    } else {
        itemColor
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = if (isTotal) 8.dp else 2.dp, horizontal = 0.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(icon),
                contentDescription = label,
                tint = displayColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))

            Text(
                text = "$label",
                fontSize = if (isTotal) 14.sp else 13.sp,
                fontWeight = if (isTotal) FontWeight.SemiBold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
            )
        }
        Text(
            text = FormatAmount(amount),
            fontSize = if (isTotal) 14.sp else 13.sp,
            fontWeight = if (isTotal) FontWeight.SemiBold else FontWeight.Normal,
            color = displayColor,
        )
    }
}

@Composable
fun CashFlowCard(
    viewModel: CashFlowViewModel = hiltViewModel()
) {
    var isFilterMenuExpanded by remember { mutableStateOf(false) }
    val state by viewModel.cardState.collectAsState()

    if (state.isLoading) {
        CashFlowLoadingState()
    } else {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer),
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().height(32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Strings.title_cash_flow),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.8f)
                    )
                    Box(contentAlignment = Alignment.TopEnd) {
                        FilterActionButton(
                            onClick = { isFilterMenuExpanded = true },
                            icon = R.drawable.filter_ic,
                            size = 28.dp
                        )

                        CashFlowFilterMenu(
                            isExpanded = isFilterMenuExpanded,
                            onDismiss = { isFilterMenuExpanded = false },
                            selectedFilter = state.selectedFilter,
                            onFilterSelected = { newFilter ->
                                viewModel.onFilterChange(newFilter)
                            }
                        )
                    }
                }
                val label = if (state.selectedFilter == FilterKeys.ALL) {
                    stringResource(R.string.filter_all)
                } else {
                    state.periodLabel
                }
                Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onTertiary.copy(0.7f))
                Spacer(Modifier.height(4.dp))

                CashFlowItem(R.drawable.up_down_income, stringResource(Strings.title_income), state.income, incomeColor)
                CashFlowItem(R.drawable.up_right_expense,stringResource(Strings.title_expense), state.expenses, expenseColor)

                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                CashFlowItem(
                    icon = R.drawable.balance_ic,
                    label = stringResource(Strings.title_total),
                    amount = state.total,
                    itemColor = if (state.total < 0) expenseColor else incomeColor,
                    isTotal = true
                )
            }
        }
    }

}

@Composable
fun CashFlowLoadingState() {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .graphicsLayer { this.alpha = alpha },
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().height(32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(18.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.onTertiary.copy(0.12f))
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onTertiary.copy(0.12f))
                )
            }
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .width(60.dp)
                    .height(10.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.onTertiary.copy(0.08f))
            )

            Spacer(Modifier.height(12.dp))

            repeat(2) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(16.dp).background(MaterialTheme.colorScheme.onTertiary.copy(0.1f), CircleShape))
                        Spacer(Modifier.width(8.dp))
                        Box(modifier = Modifier.width(60.dp).height(12.dp).background(MaterialTheme.colorScheme.onTertiary.copy(0.1f), RoundedCornerShape(4.dp)))
                    }
                    Box(modifier = Modifier.width(80.dp).height(12.dp).background(MaterialTheme.colorScheme.onTertiary.copy(0.1f), RoundedCornerShape(4.dp)))
                }
            }

            Spacer(Modifier.height(8.dp))
            Divider(color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.05f))
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(18.dp).background(MaterialTheme.colorScheme.onTertiary.copy(0.15f), CircleShape))
                    Spacer(Modifier.width(8.dp))
                    Box(modifier = Modifier.width(50.dp).height(14.dp).background(MaterialTheme.colorScheme.onTertiary.copy(0.15f), RoundedCornerShape(4.dp)))
                }
                Box(modifier = Modifier.width(100.dp).height(16.dp).background(MaterialTheme.colorScheme.onTertiary.copy(0.15f), RoundedCornerShape(4.dp)))
            }
        }
    }
}