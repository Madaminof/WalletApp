package dev.samandar.walletapp.wallet.presentation.ui.home.cardStatistics

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.presentation.ui.home.totalBalanceCard.primaryAccent

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.ui.window.DialogProperties

@Composable
fun PeriodSelectionDialog(
    selectedPeriodKey: String,
    onDismiss: () -> Unit,
    onPeriodSelected: (String) -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    var tempSelectedPeriodKey by remember { mutableStateOf(selectedPeriodKey) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    val closeWithAnimation = {
        isVisible = false
    }

    LaunchedEffect(isVisible) {
        if (!isVisible) {
            kotlinx.coroutines.delay(250)
            onDismiss()
        }
    }

    val selectedLabel = remember(tempSelectedPeriodKey) {
        ALL_PERIODS.firstOrNull { it.key == tempSelectedPeriodKey }?.labelResId
            ?: Strings.filter_month
    }
    val buttonLabel = stringResource(selectedLabel)

    val dialogBackgroundColor = MaterialTheme.colorScheme.onPrimaryContainer
    val textColor = MaterialTheme.colorScheme.onTertiary.copy(0.7f)

    Dialog(
        onDismissRequest = closeWithAnimation,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(animationSpec = tween(300)),
                exit = fadeOut(animationSpec = tween(250))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(onClick = closeWithAnimation)
                )
            }

            AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it/2 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn(),
                exit = slideOutVertically(
                    targetOffsetY = { it/2 },
                    animationSpec = tween(250, easing = FastOutSlowInEasing)
                ) + fadeOut()
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 36.dp)
                        .clip(RoundedCornerShape(28.dp)),
                    color = dialogBackgroundColor,
                    shadowElevation = 16.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Divider(
                                modifier = Modifier
                                    .width(40.dp)
                                    .clip(CircleShape),
                                thickness = 4.dp,
                                color = Color.LightGray.copy(alpha = 0.5f)
                            )
                        }
                        Text(
                            text = stringResource(Strings.title_dialog_expense_card),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                            ),
                            fontSize = 18.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 16.dp),
                            textAlign = TextAlign.Center
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            ALL_PERIODS.forEach { period ->
                                val isSelected = period.key == tempSelectedPeriodKey
                                val periodLabel = stringResource(period.labelResId)

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { tempSelectedPeriodKey = period.key }
                                        .background(if (isSelected) primaryAccent.copy(alpha = 0.08f) else Color.Transparent)
                                        .padding(horizontal = 12.dp, vertical = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = periodLabel,
                                        fontSize = 15.sp,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (isSelected) primaryAccent else textColor
                                    )
                                    Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                                        if (isSelected) {
                                            Icon(
                                                Icons.Filled.Check,
                                                contentDescription = "Selected",
                                                tint = Color.White,
                                                modifier = Modifier
                                                    .size(18.dp)
                                                    .clip(CircleShape)
                                                    .background(primaryAccent)
                                            )
                                        } else {
                                            Box(
                                                modifier = Modifier
                                                    .size(24.dp)
                                                    .clip(CircleShape)
                                                    .background(MaterialTheme.colorScheme.onTertiary.copy(0.05f))
                                                    .border(1.5.dp, Color.LightGray.copy(alpha = 0.1f), CircleShape)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        Button(
                            onClick = {
                                onPeriodSelected(tempSelectedPeriodKey)
                                closeWithAnimation()
                            },
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = primaryAccent),
                            enabled = true
                        ) {
                            Text(
                                text = "${stringResource(Strings.dialog_btn_txt)}($buttonLabel)",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}