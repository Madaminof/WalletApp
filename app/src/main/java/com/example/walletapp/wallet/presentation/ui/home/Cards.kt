package com.example.walletapp.wallet.presentation.ui.home

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.CallReceived
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.walletapp.ui.theme.expenseColor
import com.example.walletapp.ui.theme.goals
import com.example.walletapp.ui.theme.incomeColor
import com.example.walletapp.wallet.presentation.ui.home.diogramCharts.DoughnutChart
import com.example.walletapp.wallet.presentation.viewmodel.CategoryData
import com.example.walletapp.wallet.presentation.viewmodel.HomeViewModel
import kotlinx.coroutines.delay
import java.text.DecimalFormat

suspend fun animateValue(
    from: Int,
    to: Int,
    duration: Int,
    onUpdate: (Int) -> Unit
) {
    val steps = 30
    val delayTime = duration / steps
    for (i in 0..steps) {
        val progress = i / steps.toFloat()
        val value = (from + (to - from) * progress).toInt()
        onUpdate(value)
        delay(delayTime.toLong())
    }
}

@SuppressLint("UnusedContentLambdaTargetStateParameter")
@Composable
fun IncomeExpenseCard(
    viewModel: HomeViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val incomeExpensePair by viewModel.incomeExpenseFlow.collectAsState(initial = Pair(0.0, 0.0))
    val targetIncome = incomeExpensePair.first.toInt()
    val targetExpense = incomeExpensePair.second.toInt()

    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )
    val animatedIncome by animateIntAsState(targetValue = targetIncome, label = "Income Animation")
    val animatedExpense by animateIntAsState(targetValue = targetExpense, label = "Expense Animation")

    val totalBalance = animatedIncome - animatedExpense
    val totalSum = animatedIncome + animatedExpense

    val expenseRatio: Float = if (totalSum == 0) 0f else (animatedExpense.toFloat() / totalSum.toFloat()).coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = expenseRatio,
        animationSpec = tween(durationMillis = 800)
    )
    val neutralColor = Color.Gray
    val balanceColor = when {
        totalBalance > 0 -> incomeColor
        totalBalance < 0 -> expenseColor
        else -> neutralColor
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp, vertical = 2.dp)
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
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Sof Balance",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onTertiary
            )
            Spacer(Modifier.height(6.dp))

            Text(
                text = "${if (totalBalance >= 0) "+" else ""}${"%,d".format(totalBalance)} UZS",
                color = balanceColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(Modifier.height(16.dp))
            CustomProgressBar(
                progress = animatedProgress,
                incomeColor = incomeColor,
                expenseColor = expenseColor
            )

            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SummaryDetail(
                    icon = Icons.Default.CallReceived,
                    label = "Daromad",
                    amount = animatedIncome,
                    color = incomeColor,
                    isPositive = true
                )
                SummaryDetail(
                    icon = Icons.Default.CallMade,
                    label = "Xarajat",
                    amount = animatedExpense,
                    color = expenseColor,
                    isPositive = false
                )
            }
        }
    }
}
@Composable
fun SummaryDetail(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, amount: Int, color: Color, isPositive: Boolean) {
    Column(horizontalAlignment = Alignment.Start) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(2.dp))
        Text(
            text = "${if(isPositive) "+" else "-"}${"%,d".format(amount)} UZS",
            color = color,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}
@Composable
fun CustomProgressBar(progress: Float, incomeColor: Color, expenseColor: Color) {
    val safeProgress = progress.coerceIn(0f, 1f)
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Foydalanilgan:",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onTertiary
            )
            Text(
                text = "${(safeProgress * 100).toInt()}%",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = expenseColor
            )
        }

        Spacer(Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(incomeColor.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(safeProgress)
                    .background(expenseColor, RoundedCornerShape(4.dp))
            )
        }
    }
}


@Composable
fun QuickInCards(
    icon: ImageVector,
    title: String,
    color: Color,
    onClick: () -> Unit = {}
) {
    var pressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.90f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "scale"
    )

    val bgColor by animateColorAsState(
        targetValue = if (pressed) color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.onPrimaryContainer,
        animationSpec = tween(durationMillis = 200),
        label = "bgColor"
    )

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (pressed) 0.dp else 2.dp),
        modifier = Modifier
            .scale(scale)
            .padding(vertical = 2.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        tryAwaitRelease()
                        pressed = false
                    }
                )
            }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(color = color.copy(alpha = 0.1f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onTertiary
            )
        }
    }
}

@Composable
fun ExpenseStatisticCard(
    expenseData: List<CategoryData>,
    totalAmount: Double,
    period: String = "Oxirgi 30 kun",
    onMoreClick: () -> Unit = {}
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    val formatter = remember { DecimalFormat("#,###.00") }
    val formattedTotalAmount = formatter.format(totalAmount)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 0.dp)
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
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Xarajatlar Statistikasi",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                    IconButton(onClick = onMoreClick) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Batafsil sozlamalar",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(
                    text = period,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "$formattedTotalAmount UZS",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(8.dp))
            }
            if (expenseData.isNotEmpty() && totalAmount > 0.0) {
                DoughnutChart(
                    data = expenseData,
                    totalAmount = totalAmount
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Chiqimlar hali kiritilmagan.",
                        color = Color.Gray
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            TextButton(
                onClick = onMoreClick,
                modifier = Modifier.align(Alignment.End).padding(end = 12.dp)
            ) {
                Text(
                    text = "Batafsil Ko'rish",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .size(16.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}


@Composable
fun GoalsCard(
    onCreateGoalClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp, vertical = 8.dp)
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
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(
                            brush = Brush.linearGradient(
                                listOf(goals, goals)
                            ),
                            shape = RoundedCornerShape(18.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.TrackChanges,
                        contentDescription = "Goals Icon",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Maqsadlar",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Birinchi maqsadingizni belgilang va uni osonlik bilan kuzating.",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
                progress = 0.0f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50)),
                color = Color(0xFF66BB6A),
                trackColor = Color(0xFFE0E0E0)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tugma
            TextButton(
                onClick = onCreateGoalClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = "Yaratish",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .size(16.dp)
                )
            }
        }
    }
}
