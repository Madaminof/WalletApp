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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.ui.theme.defaultColor
import dev.samandar.walletapp.utils.Strings


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
    selectedColor: Color,
    onIconSelected: (Int) -> Unit
) {
    SelectorWrapper(title = stringResource(Strings.select_icon)) {
        items(extendedIcons) { iconRes ->
            IconSelectionItem(
                isSelected = iconRes == selectedIcon,
                onClick = { onIconSelected(iconRes) },
                iconRes = iconRes,
                activeColor = selectedColor
            )
        }
    }
}

@Composable
private fun IconSelectionItem(
    isSelected: Boolean,
    onClick: () -> Unit,
    iconRes: Int,
    activeColor: Color
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "iconScale"
    )

    Box(
        modifier = Modifier
            .size(48.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(
                if (isSelected) activeColor.copy(alpha = 0.12f)
                else MaterialTheme.colorScheme.onTertiary.copy(0.03f)
            )
            .then(
                if (isSelected) Modifier.border(
                    width = 1.5.dp,
                    color = activeColor.copy(alpha = 0.4f),
                    shape = CircleShape
                ) else Modifier
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = if (isSelected) activeColor else defaultColor,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
fun ColorSelector(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit
) {
    SelectorWrapper(title = stringResource(Strings.select_color)) {
        items(extendedColors) { color ->
            SelectionItem(
                isSelected = color == selectedColor,
                onClick = { onColorSelected(color) },
                activeColor = color
            )
        }
    }
}

@Composable
private fun SelectionItem(
    isSelected: Boolean,
    onClick: () -> Unit,
    activeColor: Color
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size(48.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onTertiary.copy(0.03f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 2.dp,
                        color = activeColor.copy(alpha = 0.3f),
                        shape = CircleShape
                    )
            )
        }
        Box(
            modifier = Modifier
                .size(if (isSelected) 32.dp else 36.dp)
                .clip(CircleShape)
                .background(activeColor)
                .then(
                    if (isSelected) Modifier.shadow(
                        elevation = 8.dp,
                        shape = CircleShape,
                        ambientColor = activeColor,
                        spotColor = activeColor
                    ) else Modifier
                )
        )
    }
}

@Composable
private fun SelectorWrapper(
    title: String,
    content: LazyListScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            ),
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
            content = content
        )
    }
}