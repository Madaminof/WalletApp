package dev.samandar.walletapp.wallet.presentation.ui.charts.historyTransactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.ui.theme.expenseColor
import dev.samandar.walletapp.ui.theme.incomeColor
import dev.samandar.walletapp.wallet.presentation.utils.formatAmountWithCurrency
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource

@Composable
fun MonthlySummaryCard(title: String, balance: Double, income: Double, expense: Double) {
    val shadowColor2 = Color.Black.copy(0.06f)
    val formattedExpense = if (expense == 0.0) formatAmountWithCurrency(expense) else "-${formatAmountWithCurrency(expense)}"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawIntoCanvas { canvas ->
                    val paint = Paint()
                    val frameworkPaint = paint.asFrameworkPaint()
                    frameworkPaint.color = shadowColor2.toArgb()

                    frameworkPaint.setShadowLayer(
                        20f, // Blur - qanchalik tarqalishi
                        0f,  // X offset
                        50f, // Y offset - soyani pastga surish
                        shadowColor2.toArgb()
                    )
                    canvas.drawRoundRect(
                        left = 10f,             // Yonlardan biroz ichkariga olamiz
                        top = size.height * 0.6f, // Soyani Card'ning 60% pastidan boshlaymiz
                        right = size.width - 10f,
                        bottom = size.height,
                        radiusX = 28.dp.toPx(),
                        radiusY = 28.dp.toPx(),
                        paint = paint
                    )
                }
            },
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        val topColor = MaterialTheme.colorScheme.primaryContainer
        val bottomColor = MaterialTheme.colorScheme.onPrimaryContainer
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(topColor, bottomColor),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY // Butun bo'yiga yoyiladi
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.onTertiary.copy(0.05f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.balance_ic),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        color =  MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = formatAmountWithCurrency(balance),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color =  MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 0.5.dp,
                color = Color.Black.copy(0.05f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SummaryMiniItem(
                    label = stringResource(R.string.tab_expense),
                    amount = formattedExpense,
                    color =  expenseColor,
                    icon = R.drawable.up_right_expense
                )

                Box(Modifier.width(1.dp).height(28.dp).background(Color.Black.copy(0.05f)))

                SummaryMiniItem(
                    label = stringResource(R.string.tab_income),
                    amount = formatAmountWithCurrency(income),
                    color =  incomeColor,
                    icon = R.drawable.up_down_income,
                    isEnd = true
                )
            }
        }
    }
}

@Composable
private fun SummaryMiniItem(
    label: String,
    amount: String,
    color: Color,
    icon: Int,
    isEnd: Boolean = false
) {
    Column(
        modifier = Modifier.padding(horizontal = 4.dp),
        horizontalAlignment = if (isEnd) Alignment.End else Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (isEnd) Arrangement.End else Arrangement.Start
        ) {
            if (!isEnd) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = color
                )
                Spacer(Modifier.width(4.dp))
            }

            Text(
                text = label,
                fontSize = 13.sp,
                color =  MaterialTheme.colorScheme.onTertiary.copy(0.5f),
                fontWeight = FontWeight.Medium
            )

            if (isEnd) {
                Spacer(Modifier.width(4.dp))
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = color
                )
            }
        }
        Text(
            text = amount,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(top = 1.dp)
        )
    }
}
