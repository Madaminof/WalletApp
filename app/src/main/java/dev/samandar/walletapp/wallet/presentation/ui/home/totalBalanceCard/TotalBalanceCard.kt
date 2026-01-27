package dev.samandar.walletapp.wallet.presentation.ui.home.totalBalanceCard

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.samandar.walletapp.R
import dev.samandar.walletapp.ui.theme.expenseColor
import dev.samandar.walletapp.ui.theme.incomeColor
import dev.samandar.walletapp.utils.FilterKeys
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.presentation.utils.FormatAmount
import dev.samandar.walletapp.wallet.presentation.utils.getCurrencySymbol
import kotlin.math.absoluteValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import dev.samandar.walletapp.wallet.presentation.ui.charts.categoryStatistic.FilterActionButton
import dev.samandar.walletapp.wallet.presentation.ui.charts.categoryStatistic.UniversalFilterMenu
import dev.samandar.walletapp.wallet.presentation.ui.home.activeCurrency
import dev.samandar.walletapp.wallet.presentation.viewmodel.chartViewmodel.TimeFilter

val primaryAccent = Color(0xFF4759C1)


@Composable
fun TotalBalanceCard(
    viewModel: TotalBalanceCardViewModel = hiltViewModel(),
    onFilterClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val state by viewModel.cardState.collectAsState()

    val balanceColor = if (state.netBalance < 0) expenseColor else incomeColor
    var isMenuExpanded by remember { mutableStateOf(false) }

    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow), label = "CardScale"
    )
    val haptic = LocalHapticFeedback.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.rotationX = rotationX
                cameraDistance = 15f * density
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ){
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

                // MUHIM: Tugma va Menyu bitta Box ichida bo'lishi shart!
                Box(contentAlignment = Alignment.TopEnd) {
                    FilterActionButton(
                        onClick = { isMenuExpanded = true },
                        icon = Icons.Default.FilterList,
                        size = 30.dp
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

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                PeriodNavigationButton(
                    icon = R.drawable.back_ic,
                    onClick = { viewModel.onPeriodNavigate(forward = false) },
                    size = 32.dp
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(30.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                        .padding(horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val periodAmount = String.format("%,.0f", state.periodBalance)
                    val sign = if (state.isIncomeMode && state.periodBalance>0) "+"
                    else if (!state.isIncomeMode && state.periodBalance>0) "-"
                    else ""

                    Text(
                        text = "$sign$periodAmount ${getCurrencySymbol(activeCurrency)}, ${state.periodLabel}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onTertiary.copy(0.7f),
                    )
                }
                PeriodNavigationButton(
                    icon = R.drawable.arrow_right,
                    onClick = { viewModel.onPeriodNavigate(forward = true) },
                    size = 32.dp,
                )
            }

            Spacer(Modifier.height(12.dp))

            BalanceLineChart(
                data = state.barChartData,
                isIncomeMode = state.isIncomeMode,
                globalMaxLimit = state.globalMaxLimit
            )

            Spacer(Modifier.height(8.dp))

            TimeFilterRow(state.selectedFilter, onFilterChange = viewModel::onFilterChange)
        }
    }
}


@Composable
fun CircularIconButton(
    onClick: () -> Unit,
    icon: Int,
    contentDescription: String,
    tint: Color,
    backgroundColor: Color = Color.Transparent,
    size: Dp = 32.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(painter = painterResource(icon), contentDescription = contentDescription, tint = tint, modifier = Modifier.size(size * 0.55f))
    }
}

@Composable
private fun PeriodNavigationButton(
    icon: Int,
    onClick: () -> Unit,
    size: Dp = 30.dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(painter = painterResource(icon), contentDescription = null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f), modifier = Modifier.size(size * 0.66f))
    }
}

@Composable
fun TimeFilterRow(selectedKey: String, onFilterChange: (String) -> Unit) {

    val options = listOf(
        FilterKeys.DAY to R.string.filter_day,
        FilterKeys.WEEK to R.string.filter_week,
        FilterKeys.MONTH to R.string.filter_month,
        FilterKeys.YEAR to R.string.filter_year,
        FilterKeys.ALL to R.string.filter_all
    )

    Row(
        modifier = Modifier,
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEach { (key, resId) ->
            val optionName = stringResource(resId)

            val isSelected = key == selectedKey

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) primaryAccent else Color.Transparent)
                    .clickable { onFilterChange(key) }
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = optionName,
                    fontSize = 8.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (isSelected) Color.White else Color.DarkGray.copy(alpha = 0.8f)
                )
            }
        }
    }
}