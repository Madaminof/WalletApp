package dev.samandar.walletapp.wallet.presentation.ui.premiumFunctions.shoppingLists

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.ui.theme.shoppingList

@Composable
fun EmptyListState(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f, // Doira 30% ga kengayadi
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(160.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .scale(pulseScale)
                    .alpha(pulseAlpha)
                    .background(shoppingList.copy(0.5f), CircleShape)
            )
            Surface(
                shape = CircleShape,
                color = shoppingList.copy(alpha = 0.1f),
                modifier = Modifier.size(85.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(id = R.drawable.shopp_list_ic),
                        contentDescription = null,
                        tint = shoppingList,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        // Sarlavha
        Text(
            text = stringResource(R.string.empty_list_title),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            ),
            color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.empty_list_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.5f),
            modifier = Modifier.padding(horizontal = 24.dp),
            lineHeight = 24.sp
        )
    }
}