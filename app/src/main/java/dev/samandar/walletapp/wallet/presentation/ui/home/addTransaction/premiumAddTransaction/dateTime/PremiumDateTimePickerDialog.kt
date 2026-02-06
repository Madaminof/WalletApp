package dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.dateTime

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.helperFunctions.ZoomDialog
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

@Composable
fun PremiumDateTimePickerDialog(
    initialDateTime: Long,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val calendar = remember { Calendar.getInstance().apply { timeInMillis = initialDateTime } }

    var selectedYear by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var selectedDay by remember { mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }
    var selectedHour by remember { mutableStateOf(calendar.get(Calendar.HOUR_OF_DAY)) }
    var selectedMinute by remember { mutableStateOf(calendar.get(Calendar.MINUTE)) }

    val years = remember { (2020..2030).toList() }
    val months = remember { (0..11).toList() }
    val daysInMonth = remember(selectedMonth, selectedYear) {
        Calendar.getInstance().apply { set(selectedYear, selectedMonth, 1) }.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    ZoomDialog(onDismiss = onDismiss) {
        Surface(
            modifier = Modifier
                .width(320.dp)
                .wrapContentHeight(),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            tonalElevation = 12.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = SimpleDateFormat("EEEE", Locale.getDefault()).format(
                                Calendar.getInstance().apply { set(selectedYear, selectedMonth, selectedDay) }.time
                            ),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(
                                Calendar.getInstance().apply { set(selectedYear, selectedMonth, selectedDay) }.time
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiary.copy(0.5f)
                        )
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(0.4f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = String.format("%02d:%02d", selectedHour, selectedMinute),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        color = MaterialTheme.colorScheme.primary.copy(0.1f),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onTertiary.copy(0.05f))
                    ) {}

                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // KUN
                        WheelPicker(
                            items = (1..daysInMonth).map { String.format("%02d", it) },
                            initialIndex = selectedDay - 1,
                            onValueChange = { selectedDay = it + 1 }
                        )
                        // OY
                        WheelPicker(
                            items = months.map { SimpleDateFormat("MMM", Locale.getDefault()).format(Calendar.getInstance().apply { set(Calendar.MONTH, it) }.time) },
                            initialIndex = selectedMonth,
                            onValueChange = { selectedMonth = it }
                        )
                        // YIL
                        WheelPicker(
                            items = years.map { it.toString() },
                            initialIndex = years.indexOf(selectedYear).coerceAtLeast(0),
                            onValueChange = { selectedYear = years[it] }
                        )

                        // Ajratuvchi chiziq (O'ta mayin)
                        Box(modifier = Modifier.width(1.dp).fillMaxHeight(0.5f).align(Alignment.CenterVertically).background(MaterialTheme.colorScheme.onTertiary.copy(0.2f)))

                        // SOAT
                        WheelPicker(
                            items = (0..23).map { String.format("%02d", it) },
                            initialIndex = selectedHour,
                            onValueChange = { selectedHour = it }
                        )
                        // MINUT
                        WheelPicker(
                            items = (0..59).map { String.format("%02d", it) },
                            initialIndex = selectedMinute,
                            onValueChange = { selectedMinute = it }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // --- FOOTER BUTTONS ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(54.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(stringResource(Strings.dialog_button_cancel), color = MaterialTheme.colorScheme.onTertiary.copy(0.4f), fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            val result = Calendar.getInstance().apply {
                                set(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute)
                            }
                            onConfirm(result.timeInMillis)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1.5f).height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        Text(stringResource(Strings.dialog_button_save), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun WheelPicker(
    items: List<String>,
    initialIndex: Int,
    onValueChange: (Int) -> Unit
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val view = LocalView.current

    val centerIndex by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) 0
            else {
                val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2
                visibleItems.minByOrNull { abs((it.offset + it.size / 2) - viewportCenter) }?.index ?: 0
            }
        }
    }

    LaunchedEffect(centerIndex) {
        if (centerIndex in items.indices) {
            onValueChange(centerIndex)
            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
        }
    }

    LazyColumn(
        state = listState,
        flingBehavior = snapFlingBehavior,
        contentPadding = PaddingValues(vertical = 74.dp),
        modifier = Modifier.width(55.dp).height(200.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(items.size) { index ->
            val isSelected = index == centerIndex

            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.25f else 0.8f,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
            )

            val alpha by animateFloatAsState(
                targetValue = if (isSelected) 1f else 0.3f,
                animationSpec = tween(150)
            )

            Box(
                modifier = Modifier
                    .height(52.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                        rotationX = (centerIndex - index) * 12f
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = items[index],
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onTertiary.copy(0.5f),
                    fontSize = 12.sp
                )
            }
        }
    }
}