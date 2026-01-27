package dev.samandar.walletapp.wallet.presentation.ui.topbars.addTopbar

import androidx.compose.animation.core.Spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.presentation.ui.topbars.topbarScreen.PremiumButton
import androidx.compose.runtime.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTopBar(
    navController: NavController,
    canSave: Boolean,
    onSave: () -> Unit,
    title:String
) {
    CenterAlignedTopAppBar(
        title = { Text(title, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)) },
        navigationIcon = {
            PremiumButton(
                onClick = {navController.popBackStack()},
                icon = R.drawable.close_ic,
                color = MaterialTheme.colorScheme.primary.copy(0.8f),
                modifier = Modifier
            )
        },
        actions = {
            ActionButton(
                icon = R.drawable.check_icon,
                color = MaterialTheme.colorScheme.primary,
                canSave = canSave,
                onSave = onSave
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onTertiary,
            navigationIconContentColor = MaterialTheme.colorScheme.onTertiary
        )
    )
}

@Composable
fun ActionButton(
    icon: Int,
    color: Color,
    canSave: Boolean,
    onSave: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    var lastClickTime by remember { mutableLongStateOf(0L) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed && canSave) 0.85f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy),
        label = "scaleAnim"
    )
    Box(
        modifier = Modifier
            .padding(end = 12.dp)
            .size(32.dp)
            .scale(scale)
            .pointerInput(canSave) {
                if (canSave) {
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
                                onSave()
                            }
                        }
                    )
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = if (canSave) color else color.copy(alpha = 0.3f),
            modifier = Modifier.size(32.dp)
        )
    }
}