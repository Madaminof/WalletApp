package com.example.walletapp.wallet.presentation.ui.charts.tabRowComponents

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.walletapp.wallet.domain.model.Account
import com.example.walletapp.wallet.domain.model.Transaction
import java.text.DecimalFormat
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.max
import kotlin.math.abs

// --- DATA & HELPER FUNCTIONS (O'ZGARMADI) ---

fun getAccountColor(account: Account, primaryColor: Color): Color {
    val hexString = account.colorHex
    if (!hexString.isNullOrBlank()) {
        return try {
            Color(android.graphics.Color.parseColor(hexString))
        } catch (e: IllegalArgumentException) {
            primaryColor
        }
    }
    return primaryColor
}

data class BalancePoint(
    val date: Long,
    val amount: Double
)

fun generateBalanceTrend(accounts: List<Account>, transactions: List<Transaction>): List<BalancePoint> {
    if (accounts.isEmpty()) return listOf(BalancePoint(Date().time, 0.0))

    val initialBalance = accounts.sumOf { it.initialBalance }
    val sortedTransactions = transactions.sortedBy { it.date }

    val balanceTrend = mutableListOf<BalancePoint>()
    var runningBalance = initialBalance

    if (sortedTransactions.isEmpty()) {
        balanceTrend.add(BalancePoint(date = Date().time, amount = runningBalance))
        return balanceTrend
    }

    val firstTxDate = sortedTransactions.first().date
    balanceTrend.add(BalancePoint(date = firstTxDate - 86400000, amount = initialBalance))

    sortedTransactions.forEach { tx ->
        runningBalance += if (tx.type.name == "INCOME") tx.amount else -tx.amount
        val txDate = tx.date

        if (balanceTrend.isNotEmpty() && balanceTrend.last().date == txDate) {
            balanceTrend[balanceTrend.lastIndex] = balanceTrend.last().copy(amount = runningBalance)
        } else {
            balanceTrend.add(BalancePoint(date = txDate, amount = runningBalance))
        }
    }

    val now = Date().time
    if (balanceTrend.lastOrNull()?.date?.let { it < now } == true || balanceTrend.isEmpty()) {
        balanceTrend.add(BalancePoint(date = now, amount = runningBalance))
    }

    return balanceTrend.takeLast(7)
}

// --- MODERN BALANCE AREA CHART (IXCHAMLANGAN JOYLAShUV UCHUN QAYTA OPTIMALLAShTIRILDI) ---

@OptIn(ExperimentalTextApi::class)
@Composable
fun ModernBalanceAreaChart(
    data: List<BalancePoint>,
    lineColor: Color,
    formatter: DecimalFormat,
    chartStartPadding: Dp = 40.dp,
    chartBottomPadding: Dp = 8.dp
) {
    val textMeasurer = rememberTextMeasurer()
    val dateFormat = remember { SimpleDateFormat("dd/MM", Locale.getDefault()) }
    val textStyle = TextStyle(fontSize = 9.sp, color = Color.Gray)
    val density = LocalDensity.current
    val strokeWidthPx = remember { with(density) { 2.dp.toPx() } }
    val circleRadiusPx = remember { with(density) { 2.dp.toPx() } }
    val circleStrokePx = remember { with(density) { 4.dp.toPx() } }

    val chartPaddings = PaddingValues(
        start = chartStartPadding,
        end = 8.dp,
        top = 8.dp,
        bottom = chartBottomPadding
    )
    if (data.size < 2) {
        Box(modifier = Modifier.fillMaxSize())
        return
    }

    val amounts = remember(data) { data.map { it.amount } }
    val minAmount = remember(amounts) { amounts.minOrNull() ?: 0.0 }
    val maxAmount = remember(amounts) { amounts.maxOrNull() ?: 1.0 }

    val range = remember(minAmount, maxAmount) { max(maxAmount - minAmount, 1e-6) }
    val numPoints = data.size

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp) // Vertikal padding kamaytirildi
                .weight(1f)
                .padding(chartPaddings)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val stepX = if (numPoints > 1) w / (numPoints - 1).toFloat() else 0f

                val yAxisLabelXOffset = -(chartStartPadding.toPx() + 3.dp.toPx())

                val areaFillBrush = Brush.verticalGradient(
                    colors = listOf(lineColor.copy(alpha = 0.4f), Color.Transparent),
                    startY = 0f,
                    endY = h
                )

                // Y-o'qi yorliqlari va Grid chiziqlari (3 nuqtada: Min, Max, O'rta)
                listOf(
                    Pair(0f, maxAmount),
                    Pair(0.5f, minAmount + range / 2),
                    Pair(1f, minAmount)
                ).forEach { (fraction, amount) ->
                    val y = h * fraction

                    // Grid chizig'i
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.5f),
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 1.dp.toPx()
                    )

                    // Y-o'qi yorlig'i
                    val labelText = if (abs(amount) >= 1000) "${formatter.format(amount / 1000)}k" else formatter.format(amount)
                    val textLayoutResult = textMeasurer.measure(labelText, textStyle)
                    drawText(
                        textMeasurer = textMeasurer,
                        text = labelText,
                        style = textStyle,
                        topLeft = Offset(
                            x = yAxisLabelXOffset + chartStartPadding.toPx() - textLayoutResult.size.width,
                            y = y - textLayoutResult.size.height / 2f
                        )
                    )
                }

                // Chizma chizish
                val linePath = Path()
                val fillPath = Path()

                data.forEachIndexed { index, point ->
                    val x = index * stepX
                    val ny = ((point.amount - minAmount) / range).toFloat().coerceIn(0f, 1f)
                    val y = h * (1f - ny)

                    if (index == 0) {
                        linePath.moveTo(x, y)
                        fillPath.moveTo(x, h)
                        fillPath.lineTo(x, y)
                    } else {
                        linePath.lineTo(x, y)
                        fillPath.lineTo(x, y)
                    }

                    if (index == numPoints - 1) {
                        // So'nggi nuqtadagi markerni chizish
                        drawCircle(
                            color = Color.White,
                            radius = circleStrokePx,
                            center = Offset(x, y),
                            style = Stroke(width = circleRadiusPx)
                        )
                        drawCircle(
                            color = lineColor,
                            radius = circleRadiusPx,
                            center = Offset(x, y)
                        )
                    }
                }

                // Area fill
                fillPath.lineTo(w, h)
                fillPath.close()
                drawPath(
                    path = fillPath,
                    brush = areaFillBrush,
                )

                // Line stroke
                drawPath(
                    path = linePath,
                    color = lineColor,
                    style = Stroke(width = strokeWidthPx)
                )
            }
        }

        // X-o'qi yorliqlari (Ixcham, faqat 3 ta asosiy sana ko'rsatiladi)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = chartStartPadding, end = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            data.forEachIndexed { index, point ->
                if (index == 0 || index == numPoints - 1 || (numPoints >= 3 && index == numPoints / 2)) {
                    Text(
                        text = dateFormat.format(Date(point.date)),
                        style = textStyle,
                        modifier = when(index) {
                            0 -> Modifier.wrapContentWidth(Alignment.Start)
                            numPoints - 1 -> Modifier.wrapContentWidth(Alignment.End)
                            else -> Modifier.wrapContentWidth(Alignment.CenterHorizontally)
                        }
                    )
                } else {
                    Spacer(Modifier.weight(1f).width(0.dp))
                }
            }
        }
    }
}

// --- BALANCE TAB (O'ZGARMADI) ---

@Composable
fun BalanceTab(
    viewModel: BalanceTabViewModel = hiltViewModel()
) {
    val state by viewModel.balanceState.collectAsStateWithLifecycle()

    val accounts = state.accounts
    val transactions = state.transactions
    val isLoading = state.isLoading
    val error = state.error

    val formatter = remember { DecimalFormat("#,###.##") }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (error != null) {
        Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
            Text(
                text = "Xato yuz berdi: $error",
                color = MaterialTheme.colorScheme.error,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
        return
    }

    val initialBalance = accounts.sumOf { it.initialBalance }
    val totalBalance = initialBalance + transactions.sumOf { if (it.type.name == "INCOME") it.amount else -it.amount }

    val balanceTrendData = remember(accounts, transactions) { generateBalanceTrend(accounts, transactions) }

    val firstBalance = balanceTrendData.firstOrNull()?.amount ?: initialBalance
    val trendPercentage = if (firstBalance != 0.0) ((totalBalance - firstBalance) / abs(firstBalance)) * 100.0 else 0.0
    val trendColor = if (trendPercentage < 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary

    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            BalanceTrendCard(totalBalance, trendPercentage, trendColor, balanceTrendData, formatter)
        }
        item {
            BalanceByAccountsCard(accounts, totalBalance, formatter)
        }
    }
}

// --- BALANCE TREND CARD (ENG OPTIMAL VA IXCHAM) ---

@Composable
fun BalanceTrendCard(
    totalBalance: Double,
    trendPercentage: Double,
    trendColor: Color,
    trendData: List<BalancePoint>,
    formatter: DecimalFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onTertiary.copy(0.07f),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiary
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Balance Trend", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onTertiary.copy(1.0f))
                }
                IconButton(onClick = { /* share */ }) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.onTertiary.copy(0.8f))
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                "${formatter.format(totalBalance)} so'm",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            val trendText = if (trendPercentage >= 0) {
                "+${formatter.format(trendPercentage)}%"
            } else {
                "${formatter.format(trendPercentage)}%"
            }

            Text(
                "$trendText - Oldingi davrga nisbatan",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = trendColor,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(8.dp))

            // 5. CHART AREA
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp) // Chart balandligi kamaytirildi
            ) {
                ModernBalanceAreaChart(
                    data = trendData,
                    lineColor = MaterialTheme.colorScheme.primary,
                    formatter = formatter
                )
            }
        }
    }
}

// --- BALANCE BY ACCOUNTS CARD (ENG OPTIMAL VA IXCHAM) ---

@Composable
fun BalanceByAccountsCard(
    accounts: List<Account>,
    totalBalance: Double,
    formatter: DecimalFormat
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val cardShape = RoundedCornerShape(16.dp)
    Card(
        modifier = Modifier.fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onTertiary.copy(0.07f),
                shape = cardShape
            ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = cardShape,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(Modifier.padding(vertical = 12.dp)) {

            // 1. SARLAVHA va BO'LISH QATORI (Ixchamlashtirildi)
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Balance by Accounts", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onTertiary.copy(1.0f))
                    Text("Eng ko'p mablag' qaysi hisobda?", fontSize = 10.sp, color = MaterialTheme.colorScheme.onTertiary.copy(0.5f))
                }
                IconButton(onClick = { /* share */ }) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.onTertiary.copy(0.8f))
                }
            }

            Spacer(Modifier.height(8.dp))

            // 2. TOTAL BALANS KO'RSATKICHI (Markazlashtirilgan)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    "${formatter.format(totalBalance)} so'm",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                )
            }

            Spacer(Modifier.height(12.dp))

            PremiumStackedAccountBar(
                accounts = accounts,
                totalBalance = totalBalance,
                formatter = formatter,
                primaryColor = primaryColor
            )
        }
    }
}

// --- YIG'MA HISOB BAR KOMPONENTI (ENG IXCHAM LEGEND) ---

@Composable
fun PremiumStackedAccountBar(
    accounts: List<Account>,
    totalBalance: Double,
    formatter: DecimalFormat,
    primaryColor: Color
) {
    val activeAccounts = remember(accounts) { accounts.filter { it.initialBalance > 0.0 } }

    if (activeAccounts.isEmpty() || totalBalance <= 0) {
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), contentAlignment = Alignment.Center) {
            Text("Faol hisoblar topilmadi.", color = MaterialTheme.colorScheme.onTertiary.copy(0.6f))
        }
        return
    }

    val accountData = activeAccounts.map { account ->
        Triple(
            getAccountColor(account, primaryColor),
            (account.initialBalance / totalBalance).toFloat(),
            account
        )
    }.sortedByDescending { it.second }

    val totalProgress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1000, delayMillis = 200),
        label = "stacked_bar_anim"
    )

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        // 1. STACKED BAR (Yig'ma Chiziq)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
        ) {
            accountData.forEach { (color, percentage, _) ->
                val segmentWidth = percentage * totalProgress
                Box(
                    Modifier
                        .fillMaxHeight()
                        .weight(segmentWidth.coerceAtLeast(0.001f), fill = false)
                        .background(color)
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        // 2. LEGEND (Eng ixcham Afsona) - Hamma ma'lumot bitta qatorda
        accountData.forEach { (color, percentage, account) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Rangli nuqta + Hisob nomi
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        account.name,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                    )
                }

                // Balans + Foiz (Bitta matn ichida)
                Text(
                    "${formatter.format(account.initialBalance)} (${(percentage * 100).toInt()}%)",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onTertiary.copy(1.0f)
                )
            }
        }
    }
}