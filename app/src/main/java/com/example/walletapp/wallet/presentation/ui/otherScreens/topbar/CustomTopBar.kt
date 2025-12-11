package com.example.walletapp.wallet.presentation.ui.otherScreens.topbar

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(
    navController: NavController,
    title: String,
    onBackClick: (() -> Unit),
    actions: @Composable RowScope.() -> Unit = {}
) {
    val IconBackgroundColor = MaterialTheme.colorScheme.onTertiary.copy(0.03f)

    val containerColor = MaterialTheme.colorScheme.primaryContainer
    val contentColor = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
    val actionColor = MaterialTheme.colorScheme.onTertiary.copy(0.8f)

    TopAppBar(
        title = {
            Text(
                text = title,
                color = contentColor,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(start = 12.dp)
            )
        },
        navigationIcon = {
            Box(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(IconBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                AnimatedBackButtonMinimal(onBackClick = onBackClick, contentColor = contentColor)
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
            titleContentColor = contentColor,
            actionIconContentColor = actionColor,
            navigationIconContentColor = contentColor
        ),
        scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    )
}

@Composable
fun AnimatedBackButtonMinimal(onBackClick: () -> Unit, contentColor: Color) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "backButtonScaleAnimationPremium"
    )

    IconButton(
        onClick = onBackClick,
        modifier = Modifier.scale(scale),
        interactionSource = interactionSource,
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = Color.Transparent
        )
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBackIosNew,
            contentDescription = "Back",
            tint = contentColor
        )
    }
}