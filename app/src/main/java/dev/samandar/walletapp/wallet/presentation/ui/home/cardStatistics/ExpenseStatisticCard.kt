package dev.samandar.walletapp.wallet.presentation.ui.home.cardStatistics

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.samandar.walletapp.R
import dev.samandar.walletapp.ui.theme.expenseColor
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.categories.getTranslatedName
import dev.samandar.walletapp.wallet.presentation.ui.home.totalBalanceCard.CircularIconButton
import dev.samandar.walletapp.wallet.presentation.ui.home.totalBalanceCard.primaryAccent
import dev.samandar.walletapp.wallet.presentation.utils.formatAmountWithCurrency
import dev.samandar.walletapp.wallet.presentation.viewmodel.chartViewmodel.CategoryData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


val ALL_PERIODS = listOf(
    TimePeriod.Daily,
    TimePeriod.Weekly,
    TimePeriod.Monthly,
    TimePeriod.AllTime
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ExpenseStatisticCardPremium(
    viewModel: StatisticsViewModel = hiltViewModel(),
    onMoreClick: () -> Unit = {}
) {
    val expenseData by viewModel.expenseStatistics.collectAsStateWithLifecycle()
    val totalAmount by viewModel.totalExpense.collectAsStateWithLifecycle()
    val selectedPeriodLabelResId by viewModel.selectedPeriodLabelResId.collectAsStateWithLifecycle()
    val selectedPeriod by viewModel.selectedPeriod.collectAsStateWithLifecycle()

    var showPeriodDialog by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessLow),
        label = "CardScale"
    )

    if (showPeriodDialog) {
        PeriodSelectionDialog(
            selectedPeriodKey = selectedPeriod.key,
            onDismiss = { showPeriodDialog = false },
            onPeriodSelected = {
                viewModel.changePeriodByKey(it)
                showPeriodDialog = false
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .pointerInput(Unit) {
                detectTapGestures(onPress = { isPressed = true; tryAwaitRelease(); isPressed = false })
            },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Strings.title_expense_statistics),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.8f)
                    )

                    CircularIconButton(
                        onClick = onMoreClick,
                        icon = R.drawable.arrow_right_ic,
                        contentDescription = "Details",
                        tint = primaryAccent.copy(0.8f),
                        backgroundColor = primaryAccent.copy(alpha = 0.1f),
                        size = 32.dp
                    )
                }
                Surface(
                    onClick = { showPeriodDialog = true },
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.07f),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = stringResource(selectedPeriodLabelResId),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            if (expenseData.isNotEmpty() && totalAmount > 0.0) {
                Text(
                    text = stringResource(Strings.total_expense),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.6f)
                )
                Text(
                    text = formatAmountWithCurrency(totalAmount),
                    color = expenseColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PremiumDoughnutChart(
                        data = expenseData,
                        totalAmount = totalAmount,
                        modifier = Modifier.weight(1f).height(150.dp),
                        selectedFilter = stringResource(selectedPeriodLabelResId),
                        chartThickness = 18.dp
                    )

                    Spacer(Modifier.width(20.dp))

                    Column(modifier = Modifier.weight(1.2f)) {
                        val topCategories = remember(expenseData) {
                            expenseData.sortedByDescending { it.amount }.take(3)
                        }

                        topCategories.forEachIndexed { index, data ->
                            PremiumCategoryProgressItem(data, totalAmount, index * 100)
                        }

                        val remainingAmount = totalAmount - topCategories.sumOf { it.amount }
                        if (remainingAmount > 0) {
                            PremiumCategoryProgressItem(
                                CategoryData(stringResource(Strings.others), remainingAmount, Color.Gray.copy(0.4f)),
                                totalAmount,
                                300
                            )
                        }
                    }
                }
            } else {
                EmptyExpenseStatistics(modifier = Modifier.fillMaxWidth().height(180.dp))
            }

        }
    }
}


@Composable
fun PremiumCategoryProgressItem(
    data: CategoryData,
    totalAmount: Double,
    delay: Int
) {
    val percentage = (data.amount / totalAmount).toFloat()
    val animatedProgress = remember { Animatable(0f) }
    val itemScale = remember { Animatable(0.9f) }
    val itemAlpha = remember { Animatable(0f) }

    LaunchedEffect(data) {
        launch {
            delay(delay.toLong())
            animatedProgress.animateTo(
                targetValue = percentage,
                animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow)
            )
        }
        launch {
            delay(delay.toLong())
            itemAlpha.animateTo(1f, tween(400))
            itemScale.animateTo(1f, spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMediumLow))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .graphicsLayer {
                alpha = itemAlpha.value
                scaleX = itemScale.value
                scaleY = itemScale.value
            }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(6.dp),
                    shape = CircleShape,
                    color = data.color,
                    shadowElevation = 4.dp
                ) {}

                Spacer(Modifier.width(8.dp))

                Text(
                    text = getTranslatedName(data.categoryName).toString(),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    ),
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${(percentage * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = data.color
                )
            }
        }

        Spacer(Modifier.height(3.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onTertiary.copy(0.04f))
                .border(
                    width = 0.5.dp,
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.02f),
                    shape = CircleShape
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress.value)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                data.color.copy(alpha = 0.8f),
                                data.color,
                                data.color.copy(alpha = 0.9f)
                            )
                        )
                    )
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val path = Path().apply {
                        addRoundRect(
                            RoundRect(
                                rect = Rect(Offset.Zero, size),
                                cornerRadius = CornerRadius(size.height / 2)
                            )
                        )
                    }
                    clipPath(path) {
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.25f),
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.1f)
                                )
                            )
                        )
                    }
                }
            }
        }
    }
}