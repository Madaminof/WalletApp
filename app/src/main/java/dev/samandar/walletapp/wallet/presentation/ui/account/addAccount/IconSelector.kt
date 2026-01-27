package dev.samandar.walletapp.wallet.presentation.ui.account.addAccount

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.samandar.walletapp.R


// Ikonkalar ro'yxati
val extendedIcons = listOf(
    R.drawable.cash_ic1, R.drawable.cash_ic2,R.drawable.cash_ic8,R.drawable.cash_ic7,
    R.drawable.cash_ic3, R.drawable.cash_ic4, R.drawable.cash_ic5,R.drawable.card_default_icon,R.drawable.wallet_ic1,
    R.drawable.cash_ic6,
)

// Ranglar ro'yxati
val extendedColors = listOf(
    Color(0xFF1976D2), Color(0xFF0F9915), Color(0xFFFF9800), Color(0xFFE91E63),
    Color(0xFF9C27B0), Color(0xFF795548), Color(0xFF009688), Color(0xFFB0BEC5),
    Color(0xFFFDD835), Color(0xFFC62828), Color(0xFF37474F), Color(0xFF3F51B5), Color(0xFFFF5722)
)
val a = Color(0xFF009688)


@Composable
fun IconSelector(
    selectedIcon: Int,
    onIconSelected: (Int) -> Unit
) {
    SelectorWrapper(title = stringResource(R.string.add_account_select_icon)) {
        items(extendedIcons) { iconRes ->
            SelectionItem(
                isSelected = iconRes == selectedIcon,
                onClick = { onIconSelected(iconRes) },
                content = {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(32.dp)
                    )
                },
                activeColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ColorSelector(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit
) {
    SelectorWrapper(title = stringResource(R.string.add_account_select_color)) {
        items(extendedColors) { color ->
            SelectionItem(
                isSelected = color == selectedColor,
                onClick = { onColorSelected(color) },
                content = {
                    Box(modifier = Modifier.fillMaxSize().padding(8.dp).clip(RoundedCornerShape(16.dp)).background(color))
                },
                activeColor = color
            )
        }
    }
}


@Composable
private fun SelectorWrapper(
    title: String,
    content: LazyListScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            color = Color.Gray.copy(alpha = 0.8f),
            modifier = Modifier.padding(start = 4.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
            content = content
        )
    }
}

@Composable
private fun SelectionItem(
    isSelected: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
    activeColor: Color
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.15f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size(60.dp)
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) activeColor.copy(0.3f) else MaterialTheme.colorScheme.onPrimaryContainer.copy(0.04f))
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = activeColor.copy(0.4f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}