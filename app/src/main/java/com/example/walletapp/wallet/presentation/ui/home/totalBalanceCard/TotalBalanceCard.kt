package com.example.walletapp.wallet.presentation.ui.home.totalBalanceCard

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.walletapp.ui.theme.expenseColor
import com.example.walletapp.ui.theme.incomeColor
import kotlin.math.absoluteValue

val primaryAccent = Color(0xFF4759C1)

@Composable
fun TotalBalanceCard(
    viewModel: TotalBalanceCardViewModel = hiltViewModel(),
    onFilterClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val state by viewModel.cardState.collectAsState()

    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow), label = "CardScale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .pointerInput(Unit) {
                detectTapGestures(onPress = { isPressed = true; tryAwaitRelease(); isPressed = false })
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer),
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
                    text = "Total balance",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.8f)
                )
                CircularIconButton(
                    onClick = onFilterClick,
                    icon = Icons.Default.FilterList,
                    contentDescription = "Filter",
                    tint = primaryAccent,
                    backgroundColor = primaryAccent.copy(alpha = 0.1f),
                    size = 32.dp
                )
            }

            Spacer(Modifier.height(4.dp))

            val balanceColor = if (state.netBalance < 0) expenseColor else incomeColor
            val formattedBalance = String.format("%,.2f", state.netBalance.absoluteValue)

            Text(
                text = "${if (state.netBalance < 0) "-" else ""}$formattedBalance UZS",
                color = balanceColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                PeriodNavigationButton(
                    icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    onClick = { viewModel.onPeriodNavigate(forward = false) },
                    size = 30.dp
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
                    val sign = if (state.periodBalance >= 0) "+" else ""

                    Text(
                        text = "$sign$periodAmount UZS, ${state.periodLabel}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                    )
                }
                PeriodNavigationButton(
                    icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    onClick = { viewModel.onPeriodNavigate(forward = true) },
                    size = 30.dp
                )
            }

            Spacer(Modifier.height(16.dp))

            BalanceLineChart(state.barChartData)

            Spacer(Modifier.height(16.dp))

            TimeFilterRow(state.selectedFilter, onFilterChange = viewModel::onFilterChange)
        }
    }
    if (state.isFilterDialogOpen) {
        AccountFilterDialog(
            accounts = state.accounts,
            selectedAccountIds = state.selectedAccountIds,
            onAccountSelectionChange = viewModel::onAccountSelectionChange,
            onDismiss = viewModel::onFilterDismiss,
            onApply = viewModel::onFilterDismiss
        )
    }
}

@Composable
fun BalanceLineChart(
    data: List<BarChartItem>,
    modifier: Modifier = Modifier,
    lineColor: Color = expenseColor.copy(alpha = 0.9f),
) {
    if (data.isEmpty()) {
        EmptyChartView(modifier = modifier.fillMaxWidth().height(100.dp))
        return
    }

    val maxAbsValue = data.maxOfOrNull { it.value }?.coerceAtLeast(0.01) ?: 1.0

    val horizontalAxisColor = Color.Gray
    val gridColor = Color.LightGray.copy(alpha = 0.3f)
    val gradientColors = listOf(lineColor.copy(alpha = 0.5f), Color.White)

    Column(modifier = modifier.fillMaxWidth().height(100.dp)) {
        Row(Modifier.fillMaxWidth().weight(1f)) {
            Column(
                modifier = Modifier.width(35.dp).fillMaxHeight().padding(end = 2.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End
            ) {
                val formatValue = { value: Double ->
                    when {
                        value == 0.0 -> "0"
                        value >= 1.0 -> "${String.format("%.1f", value)}M"
                        value >= 0.001 -> "${String.format("%.0f", value * 1000)}K"
                        else -> "${String.format("%.0f", value * 1000000)}"
                    }
                }

                val labels = listOf(maxAbsValue, maxAbsValue / 2, 0.0)

                labels.forEach { value ->
                    Text(text = formatValue(value), fontSize = 7.sp, color = horizontalAxisColor)
                }
            }
            Canvas(modifier = Modifier.weight(1f).fillMaxHeight().padding(start = 2.dp, end = 2.dp)) {
                val chartHeight = size.height
                val chartWidth = size.width
                val numPoints = data.size

                val spaceBetweenPoints = chartWidth / (if (numPoints > 1) numPoints - 1 else 1)

                val zeroLineY = chartHeight - 2.dp.toPx()
                val topY = 2.dp.toPx()
                val pathEffect = PathEffect.dashPathEffect(floatArrayOf(2f, 2f), 0f)

                drawLine(gridColor, Offset(0f, zeroLineY), Offset(chartWidth, zeroLineY), 0.5.dp.toPx(), pathEffect = pathEffect)
                drawLine(gridColor, Offset(0f, topY), Offset(chartWidth, topY), 0.5.dp.toPx(), pathEffect = pathEffect)

                if (numPoints > 0) {
                    val points = data.mapIndexed { index, item ->
                        val x = index * spaceBetweenPoints
                        val normalizedValue = (item.value.toFloat() / maxAbsValue.toFloat()).coerceIn(0f, 1f)
                        val y = zeroLineY - (normalizedValue * (chartHeight - topY - 2.dp.toPx()))
                        Offset(x, y)
                    }

                    val linePath = Path().apply {
                        points.firstOrNull()?.let { moveTo(it.x, it.y) }
                        for (i in 1 until points.size) {
                            val prev = points[i - 1]
                            val current = points[i]
                            val cX = (prev.x + current.x) / 2
                            cubicTo(cX, prev.y, cX, current.y, current.x, current.y)
                        }
                    }

                    // Gradient Area Path
                    val filledPath = Path().apply {
                        addPath(linePath) // linePath ni to'g'ri nusxalash
                        lineTo(points.last().x, zeroLineY)
                        lineTo(points.first().x, zeroLineY)
                        close()
                    }

                    // Gradientni chizish
                    val brush = Brush.verticalGradient(colors = gradientColors, startY = 0f, endY = zeroLineY)

                    clipPath(filledPath) { drawRect(brush = brush, topLeft = Offset(0f, 0f), size = Size(chartWidth, zeroLineY)) }

                    // Asosiy Chiziqni chizish
                    drawPath(path = linePath, color = lineColor, style = Stroke(width = 1.5.dp.toPx()))

                    // Nuqtalarni belgilash
                    points.forEach { offset ->
                        drawCircle(color = lineColor, radius = 2.5.dp.toPx(), center = offset)
                        drawCircle(color = Color.White, radius = 1.2.dp.toPx(), center = offset)
                    }
                }
            }
        }

        Spacer(Modifier.height(4.dp))

        // Gorizontal Yorliqlar (Bottom Axis) - MUSTAHKAMLIK UCHUN OPTIMALLASH
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 35.dp + 2.dp, end = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween // Yorliqlar teng masofada joylashadi
        ) {
            val totalDataSize = data.size
            val maxLabelsToShow = 6 // Maksimal ko'rsatiladigan yorliqlar soni

            val step = if (totalDataSize > 1) (totalDataSize - 1) / (maxLabelsToShow - 1).coerceAtLeast(1) else 1

            data.forEachIndexed { index, item ->
                if (index == 0 || index == totalDataSize - 1 || (index % step == 0 && index != totalDataSize - 1 && index != 0)) {
                    Text(
                        text = item.label,
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Normal,
                        color = horizontalAxisColor.copy(alpha = 0.5f),
                        maxLines = 1,
                        // Yorliqlar yopishib qolmasligi uchun Row Scope'da weight ishlatiladi
                        modifier = Modifier.weight(1f, fill = false)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyChartView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.LightGray.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.FilterList,
            contentDescription = null,
            tint = Color.Gray.copy(alpha = 0.6f),
            modifier = Modifier.size(32.dp)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Ma'lumotlar mavjud emas. Filtrni o'zgartiring.",
            color = Color.Gray,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun CircularIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    tint: Color,
    backgroundColor: Color = Color.Transparent,
    size: Dp = 32.dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = contentDescription, tint = tint, modifier = Modifier.size(size * 0.55f))
    }
}

@Composable
private fun PeriodNavigationButton(
    icon: ImageVector,
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
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.6f), modifier = Modifier.size(size * 0.66f))
    }
}

@Composable
fun TimeFilterRow(selected: String, onFilterChange: (String) -> Unit) {
    val options = listOf("Day", "Week", "Month", "Year", "All")

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
            val isSelected = option == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) primaryAccent else Color.Transparent)
                    .clickable { onFilterChange(option) }
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    fontSize = 10.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                    color = if (isSelected) Color.White else Color.DarkGray.copy(alpha = 0.8f)
                )
            }
        }
    }
}