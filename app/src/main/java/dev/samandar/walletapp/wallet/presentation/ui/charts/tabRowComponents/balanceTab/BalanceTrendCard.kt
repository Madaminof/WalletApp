package dev.samandar.walletapp.wallet.presentation.ui.charts.tabRowComponents.balanceTab

import android.annotation.SuppressLint
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.samandar.walletapp.R
import dev.samandar.walletapp.ui.theme.expenseColor
import dev.samandar.walletapp.ui.theme.incomeColor
import dev.samandar.walletapp.wallet.presentation.ui.charts.tabRowComponents.BalanceTabViewModel
import dev.samandar.walletapp.wallet.presentation.ui.charts.tabRowComponents.CircularIconButton
import dev.samandar.walletapp.wallet.presentation.ui.charts.tabRowComponents.primaryAccent
import dev.samandar.walletapp.wallet.presentation.ui.home.cardStatistics.TimePeriod
import dev.samandar.walletapp.wallet.presentation.utils.formatAmountWithCurrency

val primaryAccent = Color(0xFF4759C1)

@Composable
fun TimeFilterRow(selected: TimePeriod, onFilterChange: (TimePeriod) -> Unit) {
    val options = listOf(
        TimePeriod.Daily,
        TimePeriod.Weekly,
        TimePeriod.Monthly,
        TimePeriod.Year,
        TimePeriod.AllTime
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(0.2f))
            .height(30.dp)
            .padding(2.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEach { option ->
            val optionText = stringResource(id = option.labelResId)
            val isSelected = option == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) primaryAccent else Color.Transparent)
                    .clickable { onFilterChange(option) }
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = optionText,
                    fontSize = 10.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (isSelected) Color.White else Color.DarkGray.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun BalanceTrendCard(
    viewModel: BalanceTabViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.balanceState.collectAsStateWithLifecycle()

    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow), label = "CardScale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .pointerInput(Unit) {
                detectTapGestures(onPress = { isPressed = true; tryAwaitRelease(); isPressed = false })
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.6f)),
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
            if (state.error != null) {
                Text("${stringResource(R.string.error_generic)} ${state.error}", color = MaterialTheme.colorScheme.error)
                return@Card
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.title_balance_trend),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.8f)
                )
            }

            Spacer(Modifier.height(16.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = formatAmountWithCurrency(state.totalBalance),
                    color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.8f),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(8.dp))

                val trendPercentage = state.trendPercentage
                val balanceColor = if (trendPercentage < 0) expenseColor else incomeColor
                val trendText = if (trendPercentage >= 0) {
                    "+${String.format("%.2f", trendPercentage)}"
                } else {
                    String.format("%.2f", trendPercentage)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(balanceColor.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$trendText %",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = balanceColor,
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text ="${stringResource(R.string.period_label)} (${state.periodLabel})",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            BalanceTrendLineChart(
                data = state.trendData,
                lineColor = primaryAccent,
                selectedFilter = state.selectedFilter
            )

            Spacer(Modifier.height(16.dp))

            TimeFilterRow(state.selectedFilter, onFilterChange = viewModel::onFilterChange)
        }
    }
}