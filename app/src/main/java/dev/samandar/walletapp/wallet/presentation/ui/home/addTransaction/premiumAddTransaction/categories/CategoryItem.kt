package dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.categories

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.ui.theme.defaultColor
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
            isPressed -> 0.85f
            isSelected -> 1.1f
            else -> 1f
        },
        animationSpec = spring(
            dampingRatio = 0.55f,
            stiffness = Spring.StiffnessLow
        ), label = "mainScale"
    )

    val haptic = LocalHapticFeedback.current


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
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onSelect()
                }
            )
            .padding(vertical = 6.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(64.dp)
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(30.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                        .graphicsLayer {
                            alpha = 0.4f
                            scaleX = 1.3f
                            scaleY = 1.3f
                        }
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.radialGradient(
                                colors = listOf(primaryColor, Color.Transparent)
                            ),
                            shape = CircleShape
                        )
                )
            }

            Surface(
                modifier = Modifier
                    .size(56.dp)
                    .graphicsLayer {
                        translationY = if (isSelected) (-4).dp.toPx() else 0f
                    },
                shape = CircleShape,
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.03f),
                border = androidx.compose.foundation.BorderStroke(
                    width = (1).dp,
                    color = if (isSelected) primaryColor else Color.Transparent
                ),
                shadowElevation = if (isSelected) 12.dp else 0.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(id = cat.iconResId ?: R.drawable.cash_icon2),
                        contentDescription = null,
                        tint = if (isSelected) primaryColor else defaultColor,
                        modifier = Modifier
                            .size(28.dp)
                            .graphicsLayer {
                                val s = if (isSelected) 1.1f else 1f
                                scaleX = s
                                scaleY = s
                            }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = displayName.toString(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                fontSize = 11.sp,
                letterSpacing = 0.6.sp
            ),
            color = if (isSelected) primaryColor
            else MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}