package com.example.walletapp.wallet.presentation.ui.charts.tabRowComponents

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.wallet.domain.model.Account
import com.example.walletapp.wallet.domain.model.Transaction
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

data class BalancePoint(
    val date: Date,
    val amount: Double
)

@Composable
fun BalanceTab(
    accounts: List<Account>,
    transactions: List<Transaction>
) {
    val formatter = remember { DecimalFormat("#,###.00") }
    val totalBalance = accounts.sumOf { it.initialBalance }

    val balanceTrendData = remember(accounts, transactions) {
        generateBalanceTrend(accounts, transactions)
    }

    val firstBalance = balanceTrendData.firstOrNull()?.amount ?: 1.0
    val trendPercentage = if (firstBalance != 0.0) ((totalBalance - firstBalance) / firstBalance) * 100.0 else 0.0
    val trendColor = if (trendPercentage < 0) Color(0xFFE91E63) else Color(0xFF4CAF50)

    LazyColumn(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.onBackground)
    ) {
        item {
            BalanceTrendCard(
                totalBalance = totalBalance,
                trendPercentage = trendPercentage,
                trendColor = trendColor,
                trendData = balanceTrendData,
                formatter = formatter
            )
        }
        item {
            BalanceByAccountsCard(
                accounts = accounts,
                totalBalance = totalBalance,
                formatter = formatter
            )
        }
    }
}


@Composable
fun BalanceTrendCard(
    totalBalance: Double,
    trendPercentage: Double,
    trendColor: Color,
    trendData: List<BalancePoint>,
    formatter: DecimalFormat
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardColors(
            contentColor = MaterialTheme.colorScheme.onTertiary,
            containerColor = MaterialTheme.colorScheme.onBackground,
            disabledContainerColor = MaterialTheme.colorScheme.onBackground,
            disabledContentColor = MaterialTheme.colorScheme.onTertiary
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 4.dp
        ),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Balans O'zgarishi",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Oldingi davrga nisbatan pulingizdagi farq.",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                IconButton(onClick = { /* Ulashish */ }) {
                    Icon(Icons.Default.Share, contentDescription = "Ulashish", tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Joriy Balans",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "${formatter.format(totalBalance)} so'm",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "oldingi davrga nisbatan",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "${if (trendPercentage > 0) "+" else ""}${trendPercentage.roundToInt()}%",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = trendColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(0.5f))
            ) {
                LineChartCanvas(trendData)
            }

            Spacer(modifier = Modifier.height(8.dp))

            val dateFormatter = remember { SimpleDateFormat("dd-MMM", Locale("uz", "UZ")) }
            val firstDateLabel = trendData.firstOrNull()?.date?.let { dateFormatter.format(it) } ?: "..."
            val lastDateLabel = trendData.lastOrNull()?.date?.let { dateFormatter.format(it) } ?: "Bugun"

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(firstDateLabel, fontSize = 10.sp, color = Color.Gray)
                if (trendData.size > 2) {
                    val midPoint = trendData[trendData.size / 2].date
                    Text(dateFormatter.format(midPoint), fontSize = 10.sp, color = Color.Gray)
                }
                Text(lastDateLabel, fontSize = 10.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun LineChartCanvas(data: List<BalancePoint>) {
    Canvas(modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp, vertical = 8.dp)) {
        if (data.size < 2) return@Canvas

        val amounts = data.map { it.amount }
        val maxAmount = amounts.maxOrNull() ?: 1.0
        val minAmount = amounts.minOrNull() ?: 0.0

        val padding = (maxAmount * 0.1).coerceAtLeast(1.0)
        val totalMax = maxAmount + padding
        val totalMin = (minAmount - padding).coerceAtLeast(0.0)
        val range = totalMax - totalMin

        val width = size.width
        val height = size.height
        val totalPoints = data.size - 1

        val linePath = Path()

        data.forEachIndexed { index, point ->
            val x = (index.toFloat() / totalPoints.toFloat()) * width

            val normalizedY = ((point.amount - totalMin) / range).toFloat().coerceIn(0f, 1f)
            val y = height * (1f - normalizedY)

            if (index == 0) {
                linePath.moveTo(x, y)
            } else {
                linePath.lineTo(x, y)
            }
            drawCircle(
                color = Color(0xFF2196F3),
                radius = 4.dp.toPx(),
                center = Offset(x, y)
            )
        }

        // Chiziqni chizish
        drawPath(
            path = linePath,
            color = Color(0xFF2196F3),
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
fun BalanceByAccountsCard(accounts: List<Account>, totalBalance: Double, formatter: DecimalFormat) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardColors(
            contentColor = MaterialTheme.colorScheme.onTertiary,
            containerColor = MaterialTheme.colorScheme.onBackground,
            disabledContainerColor = MaterialTheme.colorScheme.onBackground,
            disabledContentColor = MaterialTheme.colorScheme.onTertiary
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 4.dp
        ),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hisoblar Bo'yicha Balans",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Pullarimning aksariyati qaysi hisobda?",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                IconButton(onClick = { /* Ulashish */ }) {
                    Icon(Icons.Default.Share, contentDescription = "Ulashish", tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "${formatter.format(totalBalance)} so'm",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.onTertiary
            )

            Spacer(modifier = Modifier.height(16.dp))
            accounts.forEach { account ->
                AccountBalanceBar(
                    account = account,
                    totalBalance = totalBalance,
                    formatter = formatter
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun AccountBalanceBar(
    account: Account,
    totalBalance: Double,
    formatter: DecimalFormat,
    ) {

    val accountColor = when(account.name.lowercase()) {
        "card", "karta", "humo", "uzcard" -> Color(0xFF009688)
        "cash", "naqd" -> Color(0xFFFFA000)
        else -> Color(0xFF03A9F4)
    }

    val percentage = if (totalBalance != 0.0) (account.initialBalance / totalBalance).toFloat().coerceIn(0f, 1f) else 0f
    val animatedWidth by animateFloatAsState(
        targetValue = percentage,
        animationSpec = tween(durationMillis = 800),
        label = "AccountBalanceBarAnimation"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = account.name,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onTertiary,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.width(8.dp))
            Text(
                text = "${formatter.format(account.initialBalance)} so'm",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = accountColor,
                maxLines = 1,
                overflow = TextOverflow.Clip
            )
        }

        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(Color.LightGray.copy(alpha = 0.5f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = animatedWidth)
                    .clip(RoundedCornerShape(5.dp))
                    .background(accountColor)
            )
        }
    }
}

fun generateBalanceTrend(accounts: List<Account>, transactions: List<Transaction>): List<BalancePoint> {
    val totalBalance = accounts.sumOf { it.initialBalance }
    val sortedTransactions = transactions.sortedBy { it.date }

    val balanceTrend = mutableListOf<BalancePoint>()
    var runningBalance = totalBalance

    sortedTransactions.forEach { tx ->
        runningBalance += if (tx.type.name == "INCOME") tx.amount else -tx.amount
        val txDate = Date(tx.date)

        balanceTrend.add(BalancePoint(date = txDate, amount = runningBalance))
    }
    if (balanceTrend.isEmpty() || balanceTrend.last().date.before(Date())) {
        balanceTrend.add(BalancePoint(date = Date(), amount = runningBalance))
    }

    return balanceTrend
}
