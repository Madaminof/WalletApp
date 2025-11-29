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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.wallet.domain.model.Account
import com.example.walletapp.wallet.domain.model.Transaction
import java.text.DecimalFormat
import java.util.Date
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
    val formatter = remember { DecimalFormat("#,###.##") }
    val totalBalance = accounts.sumOf { it.initialBalance }

    val balanceTrendData = remember(accounts, transactions) {
        generateBalanceTrend(accounts, transactions)
    }

    val firstBalance = balanceTrendData.firstOrNull()?.amount ?: 1.0
    val trendPercentage = if (firstBalance != 0.0)
        ((totalBalance - firstBalance) / firstBalance) * 100.0
    else 0.0

    val trendColor =
        if (trendPercentage < 0) Color(0xFFE53935) else Color(0xFF4CAF50)

    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onBackground)
    ) {
        item {
            BalanceTrendCardPremium(
                totalBalance,
                trendPercentage,
                trendColor,
                balanceTrendData,
                formatter
            )
        }
        item {
            BalanceByAccountsCardPremium(
                accounts,
                totalBalance,
                formatter
            )
        }
    }
}
@Composable
fun BalanceTrendCardPremium(
    totalBalance: Double,
    trendPercentage: Double,
    trendColor: Color,
    trendData: List<BalancePoint>,
    formatter: DecimalFormat
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiary
        ),
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Balans O‘zgarishi",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                    Text(
                        "O‘tgan davr bilan solishtirish",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                IconButton(onClick = { /* share */ }) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text("Joriy balans", fontSize = 12.sp, color = Color.Gray)
                    Text(
                        "${formatter.format(totalBalance)} so'm",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("O‘zgarish", fontSize = 12.sp, color = Color.Gray)
                    Text(
                        "${if (trendPercentage > 0) "+" else ""}${trendPercentage.roundToInt()}%",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = trendColor
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Color(0xFF2196F3).copy(0.05f)
                    )
            ) {
                PremiumLineChart(trendData)
            }
        }
    }
    Spacer(modifier = Modifier.height(2.dp))

}
@Composable
fun PremiumLineChart(data: List<BalancePoint>) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        if (data.size < 2) return@Canvas

        val amounts = data.map { it.amount }
        val maxAmount = amounts.maxOrNull() ?: 1.0
        val minAmount = amounts.minOrNull() ?: 0.0

        val padding = maxAmount * 0.1
        val highest = maxAmount + padding
        val lowest = (minAmount - padding).coerceAtLeast(0.0)
        val range = highest - lowest

        val w = size.width
        val h = size.height
        val count = data.size - 1

        val path = Path()

        data.forEachIndexed { i, p ->
            val x = i.toFloat() / count * w
            val ny = ((p.amount - lowest) / range).toFloat().coerceIn(0f, 1f)
            val y = h * (1f - ny)

            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            drawCircle(
                color = Color.Black.copy(alpha = 0.15f),
                radius = 6.dp.toPx(),
                center = Offset(x, y + 2)
            )
            drawCircle(
                color = Color(0xFF2196F3),
                radius = 5.dp.toPx(),
                center = Offset(x, y)
            )
        }

        drawPath(
            path = path,
            color = Color(0xFF2196F3),
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}
@Composable
fun BalanceByAccountsCardPremium(
    accounts: List<Account>,
    totalBalance: Double,
    formatter: DecimalFormat
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
            contentColor = MaterialTheme.colorScheme.onTertiary
        ),
        shape = RectangleShape
    ) {

        Column(Modifier.padding(20.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Hisoblar bo‘yicha balans",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Pul qayerda ko‘proq saqlanmoqda?",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                IconButton(onClick = {}) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            accounts.forEach { account ->
                PremiumAccountBar(
                    account = account,
                    totalBalance = totalBalance,
                    formatter = formatter
                )
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun PremiumAccountBar(
    account: Account,
    totalBalance: Double,
    formatter: DecimalFormat
) {
    val color = when (account.name.lowercase()) {
        "karta", "card", "humo", "uzcard" -> Color(0xFF00BCD4)
        "naqd", "cash" -> Color(0xFFFFC107)
        else -> Color(0xFF9C27B0)
    }

    val percentage =
        if (totalBalance != 0.0)
            (account.initialBalance / totalBalance).toFloat()
        else 0f

    val anim by animateFloatAsState(
        targetValue = percentage,
        animationSpec = tween(900),
        label = "acc_bar_anim"
    )

    Column {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                account.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Text(
                "${formatter.format(account.initialBalance)} so'm",
                color = color,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(6.dp))

        Box(
            Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(Color.LightGray.copy(alpha = .25f))
        ) {
            Box(
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(anim)
                    .clip(RoundedCornerShape(5.dp))
                    .background(color)
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
