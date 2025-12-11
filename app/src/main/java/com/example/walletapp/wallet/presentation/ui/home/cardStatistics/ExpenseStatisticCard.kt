package com.example.walletapp.wallet.presentation.ui.home.cardStatistics

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.walletapp.ui.theme.expenseColor
import com.example.walletapp.wallet.presentation.ui.home.totalBalanceCard.CircularIconButton
import com.example.walletapp.wallet.presentation.ui.home.totalBalanceCard.primaryAccent
import com.example.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencyManager
import com.example.walletapp.wallet.presentation.utils.formatAmountWithCurrency
import com.example.walletapp.wallet.presentation.viewmodel.CategoryData
import java.text.DecimalFormat
import kotlin.math.roundToInt

val ALL_PERIODS = listOf(TimePeriod.Daily, TimePeriod.Weekly, TimePeriod.Monthly, TimePeriod.AllTime)
val activeCurrency by CurrencyManager.currentCurrency

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ExpenseStatisticCardPremium(
    viewModel: StatisticsViewModel = hiltViewModel(),
    onMoreClick: () -> Unit = {}
) {
    val expenseData by viewModel.expenseStatistics.collectAsStateWithLifecycle()
    val totalAmount by viewModel.totalExpense.collectAsStateWithLifecycle()
    val selectedPeriod by viewModel.selectedPeriod.collectAsStateWithLifecycle()

    PremiumStatisticsCardContent(
        expenseData = expenseData,
        totalAmount = totalAmount,
        selectedPeriod = selectedPeriod,
        onPeriodChange = viewModel::changePeriod,
        onMoreClick = onMoreClick
    )
}
@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
private fun PremiumStatisticsCardContent(
    expenseData: List<CategoryData>,
    totalAmount: Double,
    selectedPeriod: TimePeriod,
    onPeriodChange: (TimePeriod) -> Unit,
    onMoreClick: () -> Unit = {}
) {
    var isPressed by remember { mutableStateOf(false) }
    var showPeriodDialog by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    val topCategories = remember(expenseData) {
        expenseData.sortedByDescending { it.amount }.take(3)
    }

    if (showPeriodDialog) {
        PeriodSelectionDialog(
            selectedPeriod = selectedPeriod,
            onDismiss = { showPeriodDialog = false },
            onPeriodSelected = {
                onPeriodChange(it)
                showPeriodDialog = false
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.onPrimaryContainer)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Expense Statistics",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.8f)
                )
                CircularIconButton(
                    onClick = onMoreClick,
                    icon = Icons.Default.ArrowForwardIos,
                    contentDescription = "Go to expense list",
                    tint = primaryAccent,
                    backgroundColor = primaryAccent.copy(alpha = 0.1f),
                    size = 32.dp
                )
            }
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { showPeriodDialog = true }
                    .background(MaterialTheme.colorScheme.primary.copy(0.05f))
                    .padding(vertical = 2.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedPeriod.name,
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.7f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.width(4.dp))
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = "Select period",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(14.dp))
            Text(
                text = "TOTAL EXPENSE",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = formatAmountWithCurrency(totalAmount),
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = expenseColor
            )

            Spacer(Modifier.height(24.dp))
            if (expenseData.isNotEmpty() && totalAmount > 0.0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PremiumDoughnutChart(
                        data = expenseData,
                        totalAmount = totalAmount,
                        modifier = Modifier.weight(0.6f).height(140.dp)
                    )
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(0.5f)) {
                        topCategories.forEach { data ->
                            CategoryLegendItem(data = data, totalAmount = totalAmount)
                        }
                        val remainingAmount = totalAmount - topCategories.sumOf { it.amount }
                        if (remainingAmount > 0) {
                            CategoryLegendItem(
                                data = CategoryData("Others", remainingAmount, Color.LightGray),
                                totalAmount = totalAmount,
                                isRemaining = true
                            )
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Chiqimlar hali kiritilmagan.", color = Color.Gray, fontSize = 14.sp)
                }
            }
            Spacer(Modifier.height(12.dp))

        }
    }
}


@Composable
fun CategoryLegendItem(data: CategoryData, totalAmount: Double, isRemaining: Boolean = false) {
    val percentage = (data.amount / totalAmount * 100).roundToInt()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(data.color)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = data.categoryName,
                fontSize = 10.sp,
                fontWeight = if (isRemaining) FontWeight.Normal else FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onTertiary.copy(0.5f)
            )
        }

        Text(
            text = "$percentage%",
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onTertiary.copy(0.5f)
        )
    }
}