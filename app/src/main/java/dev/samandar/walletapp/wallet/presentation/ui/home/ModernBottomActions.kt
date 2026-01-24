package dev.samandar.walletapp.wallet.presentation.ui.home

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Wallet
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.navigation.Screen

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun ModernBottomActions(
    currentRoute: String?,
    listState: LazyListState,
    onNavigate: (String) -> Unit,
    onFabClick: () -> Unit
) {
    var isEntered by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isEntered = true
    }


    val context = LocalContext.current
    val isHistoryScreen = currentRoute == Screen.ExpenseList.route

    val vibrator = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as android.os.VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }
    val triggerVibration = {
        if (vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(20)
            }
        }
    }

    val isAtTheEnd by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItemsCount = layoutInfo.totalItemsCount

            if (totalItemsCount == 0) {
                false
            } else {
                val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                val isLastIndex = lastVisibleItem?.index == totalItemsCount - 1

                if (isLastIndex && lastVisibleItem != null) {
                    val viewportBottom = layoutInfo.viewportEndOffset
                    val itemBottom = lastVisibleItem.offset + lastVisibleItem.size

                    itemBottom <= viewportBottom
                } else {
                    false
                }
            }
        }
    }
    val shouldHide = isAtTheEnd

    val scrollOffset = if (shouldHide) 150.dp else 0.dp

    val bottomOffset by animateDpAsState(
        targetValue = if (!isEntered) 150.dp else scrollOffset,
        animationSpec = spring(
            stiffness = Spring.StiffnessLow,
            dampingRatio = Spring.DampingRatioLowBouncy
        ),
        label = "BottomBarEntrance"
    )

    val navItems = listOf(
        Triple("Home", R.drawable.home_icon, Screen.Home.route),
        Triple("History", R.drawable.history_icon, Screen.ExpenseList.route),
        Triple("Budgets", R.drawable.budget_ic, Screen.Budgets.route)
    )
    val selectedIndex = navItems.indexOfFirst { it.third == currentRoute }.coerceAtLeast(0)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(8.dp)
            .offset(y = bottomOffset),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(54.dp)
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(30.dp))
                        .graphicsLayer {
                            renderEffect = android.graphics.RenderEffect.createBlurEffect(
                                250f, 250f, android.graphics.Shader.TileMode.CLAMP
                            ).asComposeRenderEffect()
                        }
                ) {
                    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.onPrimaryContainer))
                }
            }

            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(30.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.97f),
                border = BorderStroke(0.5.dp, Color.Gray.copy(0.3f))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                        val itemWidth = maxWidth / navItems.size
                        val indicatorOffset by animateDpAsState(itemWidth * selectedIndex, spring(0.8f, 400f))
                        Box(
                            modifier = Modifier.offset(x = indicatorOffset).width(itemWidth).fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(modifier = Modifier.size(40.dp).background(Brush.radialGradient(listOf(MaterialTheme.colorScheme.primary.copy(0.4f), Color.Transparent)), CircleShape))
                        }
                    }
                    Row(modifier = Modifier.fillMaxSize()) {
                        navItems.forEach { (label, icon, route) ->
                            val isSelected = currentRoute == route
                            Box(
                                modifier = Modifier.weight(1f).fillMaxHeight().clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    if (currentRoute != route) {
                                        triggerVibration()
                                        onNavigate(route)
                                    }
                                },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = icon),
                                    contentDescription = label,
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else Color(
                                        0xFF757A8B
                                    ),
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Box(
            modifier = Modifier.size(54.dp),
            contentAlignment = Alignment.Center
        ) {
            val fabScale by animateFloatAsState(
                targetValue = if (isHistoryScreen) 0f else 1f,
                animationSpec = spring(stiffness = Spring.StiffnessMedium, dampingRatio = Spring.DampingRatioLowBouncy)
            )

            Box(
                modifier = Modifier.graphicsLayer {
                    scaleX = fabScale
                    scaleY = fabScale
                    alpha = fabScale
                }
            ) {
                AliveFab(
                    icon = Icons.Default.Add,
                    onClick = onFabClick
                )
            }
        }
    }
}

@Composable
fun AliveFab(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.88f else 1f)

    val infiniteTransition = rememberInfiniteTransition(label = "FabAnim")
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -6f,
        animationSpec = infiniteRepeatable(tween(1500, easing = EaseInOutSine), RepeatMode.Reverse)
    )

    Box(
        modifier = Modifier
            .graphicsLayer {
                translationY = floatAnim
                scaleX = scale
                scaleY = scale
            }
            .size(58.dp),
        contentAlignment = Alignment.Center
    ) {
        // Aura Effect
        val auraScale by infiniteTransition.animateFloat(
            initialValue = 1f, targetValue = 1.25f,
            animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { scaleX = auraScale; scaleY = auraScale; alpha = 0.15f }
                .background(MaterialTheme.colorScheme.primary, CircleShape)
        )

        Surface(
            modifier = Modifier.fillMaxSize().clickable(interactionSource, null) { onClick() },
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            shadowElevation = 8.dp,
            border = BorderStroke(1.dp, Color.White.copy(0.2f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = Color.White, modifier = Modifier.size(28.dp))
            }
        }
    }
}