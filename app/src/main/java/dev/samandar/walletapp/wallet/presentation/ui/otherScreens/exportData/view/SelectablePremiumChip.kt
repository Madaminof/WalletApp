package dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.view

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SelectablePremiumChip(
    selected: Boolean,
    label: String,
    onClick: () -> Unit,
) {
    val containerGray = MaterialTheme.colorScheme.onTertiary.copy(0.03f)
    val activeColor = MaterialTheme.colorScheme.primary

    val bgColor by animateColorAsState(
        targetValue = if (selected) activeColor else containerGray,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "bgColor"
    )

    val contentColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.onPrimary else Color.Gray,
        label = "contentColor"
    )
    val elevation by animateDpAsState(
        targetValue = if (selected) 0.dp else 0.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "elevation"
    )

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        tonalElevation = 0.dp,
        shadowElevation = elevation,
        border = if (!selected) BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.onTertiary.copy(0.05f)
        ) else null,
        modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                letterSpacing = 0.1.sp,
                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.SemiBold
            ),
            color = contentColor
        )
    }
}

