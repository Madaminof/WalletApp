package dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.categories

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.wallet.domain.model.Category


@Composable
fun CategoryItem(
    cat: Category,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val primaryColor = Color(cat.colorArgb)
    val displayName = getTranslatedName(cat.name)

    val animatedScale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.94f
            isSelected -> 1.12f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )
    val animatedBgColor by animateColorAsState(
        targetValue = if (isSelected) primaryColor else MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.03f),
        animationSpec = tween(250),
        label = "bgColor"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onSelect
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(50.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = CircleShape,
                color = animatedBgColor,
                tonalElevation = if (isSelected) 4.dp else 0.dp
            ) {
                Icon(
                    painter = painterResource(id = cat.iconResId ?: R.drawable.ic_naqd_pul),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .padding(12.dp)
                        .graphicsLayer {
                            val s = if (isSelected) 1.1f else 1f
                            scaleX = s
                            scaleY = s
                        }
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = displayName.toString(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 0.2.sp
            ),
            color = if (isSelected) primaryColor else MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}