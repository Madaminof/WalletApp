package dev.samandar.walletapp.wallet.presentation.ui.topbars.topbarScreen

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


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
                icon = Icons.Default.ArrowBackIosNew,
                color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
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
    icon:ImageVector,
    color: Color,
    modifier: Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "backScale"
    )

    Box(
        modifier = modifier
            .padding(start = 12.dp)
            .size(36.dp)
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(0.5f))
            .clickableSingle {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Back",
            tint = color,
            modifier = Modifier.size(20.dp)
        )
    }
}


fun Modifier.clickableSingle(
    enabled: Boolean = true,
    onClick: () -> Unit
) = composed {
    val multipleEventsCutter = remember { MultipleEventsCutter.get() }
    Modifier.clickable(
        enabled = enabled,
        indication = ripple(),
        interactionSource = remember { MutableInteractionSource() }
    ) {
        multipleEventsCutter.processEvent { onClick() }
    }
}

internal class MultipleEventsCutter {
    private val now: Long get() = System.currentTimeMillis()
    private var lastEventTime: Long = 0

    fun processEvent(event: () -> Unit) {
        if (now - lastEventTime >= 500L) {
            event()
        }
        lastEventTime = now
    }

    companion object {
        fun get(): MultipleEventsCutter = MultipleEventsCutter()
    }
}