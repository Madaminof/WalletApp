package dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.dateTimePicker

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.samandar.walletapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDatePickerDialog(
    initialDate: Long,
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val primaryBlue = MaterialTheme.colorScheme.primary
    val mainSurface = MaterialTheme.colorScheme.onPrimaryContainer
    val contentText = MaterialTheme.colorScheme.onTertiary

    ZoomDialog(onDismiss = onDismiss) { animateOut ->
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDate)
        Surface(
            modifier = Modifier.fillMaxWidth(0.95f).scale(0.8f).wrapContentHeight(),
            shape = RoundedCornerShape(32.dp),
            color = mainSurface,
            tonalElevation = 12.dp
        ) {
            Column(
                modifier = Modifier.padding(top = 20.dp, bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DatePicker(
                    state = datePickerState,
                    title = null, headline = null, showModeToggle = false,
                    colors = DatePickerDefaults.colors(
                        containerColor = Color.Transparent,
                        selectedDayContainerColor = primaryBlue,
                        todayDateBorderColor = primaryBlue,
                        todayContentColor = primaryBlue,
                        dayContentColor = contentText.copy(0.8f),
                        weekdayContentColor = primaryBlue
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = animateOut) {
                        Text(stringResource(R.string.cancel), color = contentText.copy(0.4f))
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { onDateSelected(it) }
                            animateOut()
                        },
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(stringResource(R.string.ok), fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val primaryBlue = MaterialTheme.colorScheme.primary
    val mainSurface = MaterialTheme.colorScheme.onPrimaryContainer
    val contentText = MaterialTheme.colorScheme.onTertiary

    ZoomDialog(onDismiss = onDismiss) { animateOut ->
        val timePickerState = rememberTimePickerState(
            initialHour = initialHour,
            initialMinute = initialMinute,
            is24Hour = true
        )
        Surface(
            modifier = Modifier.scale(0.8f).wrapContentSize(),
            shape = RoundedCornerShape(36.dp),
            color = mainSurface,
            tonalElevation = 12.dp
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CompositionLocalProvider(LocalContentColor provides primaryBlue) {
                    TimePicker(
                        state = timePickerState,
                        colors = TimePickerDefaults.colors(
                            selectorColor = primaryBlue,
                            clockDialColor = contentText.copy(0.03f),
                            timeSelectorSelectedContainerColor = primaryBlue,
                            timeSelectorSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
                            timeSelectorUnselectedContainerColor = contentText.copy(0.03f),
                            timeSelectorUnselectedContentColor = contentText.copy(0.7f),
                            clockDialUnselectedContentColor = contentText.copy(0.7f),
                        )
                    )
                }

                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = animateOut) {
                        Text(stringResource(R.string.cancel), color = contentText.copy(0.4f))
                    }
                    Spacer(Modifier.width(12.dp))
                    Button(
                        onClick = {
                            onTimeSelected(timePickerState.hour, timePickerState.minute)
                            animateOut()
                        },
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(stringResource(R.string.ok), fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}


@Composable
fun ZoomDialog(onDismiss: () -> Unit, content: @Composable (animateOut: () -> Unit) -> Unit) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }
    val animateOut: () -> Unit = { isVisible = false }

    Dialog(
        onDismissRequest = animateOut,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = true)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            AnimatedVisibility(
                visible = isVisible,
                enter = scaleIn(animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)) + fadeIn(),
                exit = scaleOut(animationSpec = tween(200)) + fadeOut()
            ) {
                DisposableEffect(Unit) { onDispose { if (!isVisible) onDismiss() } }
                content(animateOut)
            }
        }
    }
}