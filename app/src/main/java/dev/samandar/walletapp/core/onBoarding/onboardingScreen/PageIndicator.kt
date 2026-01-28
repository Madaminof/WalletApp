package dev.samandar.walletapp.core.onBoarding.onboardingScreen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.samandar.walletapp.core.onBoarding.onboardingPages

@Composable
fun PageIndicator(
    currentPage: Int,
    pageSize: Int = onboardingPages.size,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    unselectedColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp), // Masofa biroz jichchalandi
        modifier = Modifier.padding(vertical = 12.dp)
    ) {
        repeat(pageSize) { index ->
            val isSelected = currentPage == index

            // Kenglik uchun "spring" animatsiyasi (biroz rezinkaga o'xshab cho'ziladi)
            val width by animateDpAsState(
                targetValue = if (isSelected) 28.dp else 8.dp,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "width_anim"
            )

            // Rang uchun silliq o'tish
            val color by animateColorAsState(
                targetValue = if (isSelected) selectedColor else unselectedColor,
                label = "color_anim"
            )

            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(width)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}