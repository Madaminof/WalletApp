package dev.samandar.walletapp.wallet.presentation.ui.topbars.topbarScreen


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(
    title: String,
    onBackClick: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp,
                    fontSize = 20.sp
                ),
                color = MaterialTheme.colorScheme.onTertiary.copy(0.9f),
                modifier = Modifier.padding(start = 12.dp)
            )
        },
        navigationIcon = {
            PremiumButton(
                onClick = onBackClick,
                icon = R.drawable.back_ic,
                color = MaterialTheme.colorScheme.primary.copy(0.8f),
                modifier = Modifier
            )
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onTertiary,
            navigationIconContentColor = MaterialTheme.colorScheme.onTertiary
        ),
        scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    )
}


@Composable
fun PremiumButton(
    onClick: () -> Unit,
    icon: Int,
    color: Color,
    modifier: Modifier
) {
    var isPressed by remember { mutableStateOf(false) }
    var lastClickTime by remember { mutableLongStateOf(0L) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        label = "scaleAnim"
    )

    Icon(
        painter = painterResource(id = icon),
        contentDescription = "Back",
        tint = color,
        modifier = modifier
            .padding(start = 12.dp)
            .size(32.dp)
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = {
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastClickTime > 500L) {
                            lastClickTime = currentTime
                            onClick()
                        }
                    }
                )
            }
    )
}