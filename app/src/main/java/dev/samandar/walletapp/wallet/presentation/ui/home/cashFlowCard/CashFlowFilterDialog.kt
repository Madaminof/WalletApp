package dev.samandar.walletapp.wallet.presentation.ui.home.cashFlowCard

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.presentation.ui.home.totalBalanceCard.primaryAccent
import dev.samandar.walletapp.utils.FilterKeys

@Composable
fun CashFlowFilterDialog(
    initialSelectedFilter: String,
    onFilterChange: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var isVisible by remember { mutableStateOf(false) }
    var tempSelectedFilter by remember { mutableStateOf(initialSelectedFilter) }

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

    val filterMap = remember {
        listOf(
            FilterKeys.DAY to Strings.filter_day,
            FilterKeys.WEEK to Strings.filter_week,
            FilterKeys.MONTH to Strings.filter_month,
            FilterKeys.YEAR to Strings.filter_year,
            FilterKeys.ALL to Strings.filter_all,
        )
    }

    val keyToLabelMap = filterMap.associate { (key, resId) ->
        key to stringResource(resId)
    }

    LaunchedEffect(initialSelectedFilter) {
        if (filterMap.any { it.first == initialSelectedFilter }) {
            tempSelectedFilter = initialSelectedFilter
        } else {
            tempSelectedFilter = FilterKeys.MONTH
        }
    }

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
            androidx.compose.animation.AnimatedVisibility(
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

            androidx.compose.animation.AnimatedVisibility(
                visible = isVisible,
                enter = slideInVertically(
                    initialOffsetY = { it/2 }, // Pastdan boshlash
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
                        .fillMaxWidth(0.85f)
                        .wrapContentHeight()
                        .clip(RoundedCornerShape(28.dp)),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    shadowElevation = 16.dp,
                    tonalElevation = 2.dp
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
                                color = Color.LightGray.copy(alpha = 0.8f)
                            )
                        }
                        Text(
                            text = stringResource(Strings.dialog_title),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                            ),
                            fontSize = 18.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 16.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            filterMap.forEach { (key, resId) ->
                                val label = stringResource(resId)
                                val isChecked = tempSelectedFilter == key

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { tempSelectedFilter = key }
                                        .background(if (isChecked) primaryAccent.copy(alpha = 0.08f) else Color.Transparent)
                                        .padding(horizontal = 12.dp, vertical = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 15.sp,
                                        fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (isChecked) primaryAccent else textColor
                                    )

                                    Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                                        if (isChecked) {
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
                                                    .border(
                                                        width = 2.dp,
                                                        color = MaterialTheme.colorScheme.onTertiary.copy(0.06f),
                                                        shape = CircleShape
                                                    )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        Button(
                            onClick = {
                                onFilterChange(tempSelectedFilter)
                                closeWithAnimation()
                            },
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = primaryAccent),
                            enabled = true
                        ) {
                            val selectedLabel = keyToLabelMap[tempSelectedFilter] ?: tempSelectedFilter
                            Text(
                                text = "${stringResource(Strings.dialog_btn_txt)} ($selectedLabel)",
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