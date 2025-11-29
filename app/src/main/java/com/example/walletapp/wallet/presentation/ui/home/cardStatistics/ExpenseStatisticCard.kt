package com.example.walletapp.wallet.presentation.ui.home.cardStatistics

import android.annotation.SuppressLint
import android.graphics.Paint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.walletapp.ui.theme.expenseColor
import com.example.walletapp.wallet.presentation.viewmodel.CategoryData
import java.text.DecimalFormat
import kotlin.math.min
import kotlin.math.roundToInt

private val ALL_PERIODS = listOf(TimePeriod.Daily, TimePeriod.Weekly, TimePeriod.Monthly, TimePeriod.AllTime)

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

    val formatter = remember { DecimalFormat("#,###") }
    val formattedTotalAmount = formatter.format(totalAmount)

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
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
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
                    text = "Sarflanganlar Statistikasi",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onTertiary
                )
                IconButton(onClick = onMoreClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Batafsil Tahlil",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { showPeriodDialog = true }
                    .background(MaterialTheme.colorScheme.primary.copy(0.1f))
                    .padding(vertical = 4.dp, horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedPeriod.name,
                    color = MaterialTheme.colorScheme.onTertiary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.width(4.dp))
                Icon(
                    Icons.Default.ArrowDownward,
                    contentDescription = "Davrni tanlash",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(16.dp))
            Text(
                text = "JAMI XARAJAT",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onTertiary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "$formattedTotalAmount UZS",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 26.sp,
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
                                data = CategoryData("Boshqalar", remainingAmount, Color.LightGray),
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
            Spacer(Modifier.height(8.dp))

        }
    }
}

@Composable
fun PremiumDoughnutChart(
    data: List<CategoryData>,
    totalAmount: Double,
    modifier: Modifier = Modifier,
    chartThickness: Dp = 35.dp,
    animationDuration: Int = 1000
) {
    val totalAngle = 360f
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(data) {
        animatedProgress.animateTo(
            1f,
            animationSpec = tween(animationDuration, easing = FastOutSlowInEasing)
        )
    }

    val formatter = remember { DecimalFormat("#,###") }
    val density = LocalDensity.current
    val formattedTotal = formatter.format(totalAmount)

    Box(modifier = modifier.aspectRatio(1f)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val size = this.size
            val thicknessPx = chartThickness.toPx()
            val chartSize = min(size.width, size.height)
            val center = Offset(size.width / 2f, size.height / 2f)
            val chartRadius = chartSize / 2f - thicknessPx / 2

            val rect = Rect(
                topLeft = Offset(thicknessPx / 2, thicknessPx / 2),
                bottomRight = Offset(chartSize - thicknessPx / 2, chartSize - thicknessPx / 2)
            )

            var startAngle = 270f

            data.forEach { item ->
                val sweepAngle = (item.amount / totalAmount).toFloat() * totalAngle
                val animatedSweep = sweepAngle * animatedProgress.value

                drawArc(
                    color = item.color,
                    startAngle = startAngle,
                    sweepAngle = animatedSweep,
                    useCenter = false,
                    topLeft = rect.topLeft,
                    size = rect.size,
                    style = Stroke(width = thicknessPx, cap = StrokeCap.Butt)
                )
                startAngle += sweepAngle
            }
            drawIntoCanvas {
                val textPaint = Paint().apply {
                    isAntiAlias = true
                    textAlign = Paint.Align.CENTER
                }
                val labelText = "JAMI XARAJAT"
                val labelSizePx = with(density) { 10.sp.toPx() }
                textPaint.textSize = labelSizePx
                it.nativeCanvas.drawText(
                    labelText,
                    center.x,
                    center.y + with(density) { 12.dp.toPx() },
                    textPaint
                )
                val maxTextWidth = chartRadius * 1.5f
                var currentTextSizeSpValue = 18f
                val minTextSizeSpValue = 14f

                var textSizePx = with(density) { currentTextSizeSpValue.sp.toPx() }

                while (currentTextSizeSpValue > minTextSizeSpValue) {
                    textPaint.textSize = textSizePx
                    val measuredWidth = textPaint.measureText(formattedTotal)

                    if (measuredWidth < maxTextWidth) {
                        break
                    }
                    currentTextSizeSpValue -= 2f
                    textSizePx = with(density) { currentTextSizeSpValue.sp.toPx() }
                }
                it.nativeCanvas.drawText(
                    formattedTotal,
                    center.x,
                    center.y - with(density) { 4.dp.toPx() },
                    textPaint
                )
            }
        }
    }
}


@Composable
fun PeriodSelectionDialog(
    selectedPeriod: TimePeriod,
    onDismiss: () -> Unit,
    onPeriodSelected: (TimePeriod) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        title = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Statistika Davrini Tanlash",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onTertiary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), thickness = 0.5.dp)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 0.dp)) {
                ALL_PERIODS.forEach { period ->
                    val isSelected = period == selectedPeriod
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                onPeriodSelected(period)
                                onDismiss()
                            }
                            .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.onPrimaryContainer)
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = period.name,
                            fontSize = 16.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onTertiary.copy(0.5f)
                        )
                        if (isSelected) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Tanlangan",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("BEKOR QILISH", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
            }
        }
    )
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