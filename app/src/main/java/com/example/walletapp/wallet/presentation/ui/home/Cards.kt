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
        animationSpec = tween(durationMillis = 600)
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
        shape = RoundedCornerShape(12.dp),
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
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(6.dp))

            Text(
                text = "${if (totalBalance >= 0) "+" else ""}${"%,d".format(totalBalance)} UZS",
                color = balanceColor,
                fontSize = 20.sp,
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
fun SummaryDetail(icon: ImageVector, label: String, amount: Int, color: Color, isPositive: Boolean) {
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
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
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
                .background(incomeColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
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
        modifier = Modifier
            .scale(scale)
            .padding(vertical = 0.dp)
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
                    .size(26.dp)
                    .background(color = color.copy(alpha = 0.1f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onTertiary
            )
        }
    }
}

