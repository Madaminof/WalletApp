package dev.samandar.walletapp.wallet.presentation.ui.home.NavBarActionButton

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.navigation.Screen
import dev.samandar.walletapp.ui.theme.defaultColor


@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun ModernBottomActions(
    currentRoute: String?,
    listState: LazyListState,
    onNavigate: (String) -> Unit,
    onFabClick: () -> Unit,
) {
    var isEntered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isEntered = true }

    val context = LocalContext.current

    val vibrator = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as android.os.VibratorManager).defaultVibrator
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
            if (layoutInfo.totalItemsCount == 0) false
            else {
                val lastItem = layoutInfo.visibleItemsInfo.lastOrNull()
                lastItem?.index == layoutInfo.totalItemsCount - 1 &&
                        (lastItem.offset + lastItem.size) <= layoutInfo.viewportEndOffset
            }
        }
    }

    val bottomOffset by animateDpAsState(
        targetValue = if (!isEntered || isAtTheEnd) 160.dp else 0.dp,
        animationSpec = spring(
            stiffness = Spring.StiffnessLow,
            dampingRatio = Spring.DampingRatioLowBouncy
        ),
        label = "BottomBarEntrance"
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = bottomOffset)
            .background(
                brush = Brush.verticalGradient(
                    0.0f to Color.Transparent,
                    0.3f to MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                    0.7f to MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.96f),
                    1.0f to MaterialTheme.colorScheme.primaryContainer
                )
            )
            .navigationBarsPadding()
            .padding(top = 40.dp, bottom = 12.dp, start = 20.dp, end = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // NAVBAR BLOCK
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            ) {
                // Android 12+ uchun haqiqiy Blur
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(30.dp))
                            .graphicsLayer {
                                renderEffect = android.graphics.RenderEffect
                                    .createBlurEffect(
                                        35f, 35f, android.graphics.Shader.TileMode.CLAMP
                                    )
                                    .asComposeRenderEffect()
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(0.1f))
                        )
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(30.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.95f),
                    border = BorderStroke(0.5.dp, defaultColor.copy(0.15f))
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        val navItems = listOf(
                            Triple("Home", R.drawable.home_icon, Screen.Home.route),
                            Triple("History", R.drawable.history_icon, Screen.ExpenseList.route),
                            Triple("Budgets", R.drawable.budget_ic, Screen.Budgets.route)
                        )
                        val selectedIndex =
                            navItems.indexOfFirst { it.third == currentRoute }.coerceAtLeast(0)

                        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                            val itemWidth = maxWidth / navItems.size
                            val indicatorOffset by animateDpAsState(
                                itemWidth * selectedIndex,
                                spring(0.8f, 400f)
                            )
                            Box(
                                modifier = Modifier
                                    .offset(x = indicatorOffset)
                                    .width(itemWidth)
                                    .fillMaxHeight(),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            Brush.radialGradient(
                                                listOf(
                                                    MaterialTheme.colorScheme.primary.copy(0.4f),
                                                    Color.Transparent
                                                )
                                            ), CircleShape
                                        )
                                )
                            }
                        }

                        Row(modifier = Modifier.fillMaxSize()) {
                            navItems.forEach { (label, icon, route) ->
                                val isSelected = currentRoute == route
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clickable(
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

            // FAB BLOCK
            Box(modifier = Modifier.size(56.dp)) {
                AliveFab(
                    icon = Icons.Default.Add,
                    onClick = {
                        triggerVibration()
                        onFabClick()
                    }
                )
            }
        }
    }
}
