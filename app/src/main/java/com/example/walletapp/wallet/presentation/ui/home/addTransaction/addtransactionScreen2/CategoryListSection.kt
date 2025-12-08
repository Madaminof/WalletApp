package com.example.walletapp.wallet.presentation.ui.home.addTransaction.addtransactionScreen2

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.R
import com.example.walletapp.wallet.domain.model.Category

@Composable
fun CategoryListSection(
    categories: List<Category>,
    selected: Category?,
    onSelect: (Category) -> Unit
) {
    if (categories.isEmpty()) return
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        items(categories, key = { it.id }) { cat ->
            CategoryItem(
                cat = cat,
                isSelected = cat == selected,
                onSelect = onSelect
            )
        }
    }
}


@Composable
fun CategoryItem(
    cat: Category,
    isSelected: Boolean,
    onSelect: (Category) -> Unit
) {
    val categoryColor = Color(cat.colorArgb)
    val animationDuration = 250 // ms

    val targetBackgroundColor = if (isSelected) categoryColor else MaterialTheme.colorScheme.onTertiary.copy(0.05f)
    val targetContentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onTertiary
    val targetIconBackground = if (isSelected) Color.White else categoryColor
    val targetIconTint = if (isSelected) categoryColor else Color.White

    val backgroundColor by animateColorAsState(
        targetValue = targetBackgroundColor,
        animationSpec = tween(animationDuration), label = "backgroundColorAnimation"
    )
    val contentColor by animateColorAsState(
        targetValue = targetContentColor,
        animationSpec = tween(animationDuration), label = "contentColorAnimation"
    )
    val iconBackground by animateColorAsState(
        targetValue = targetIconBackground,
        animationSpec = tween(animationDuration), label = "iconBackgroundAnimation"
    )
    val iconTint by animateColorAsState(
        targetValue = targetIconTint,
        animationSpec = tween(animationDuration), label = "iconTintAnimation"
    )

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1.0f,
        animationSpec = tween(animationDuration), label = "scaleAnimation"
    )

    Row(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable { onSelect(cat) }
            .padding(vertical = 5.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(iconBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = cat.iconResId ?: R.drawable.ic_naqd_pul),
                contentDescription = cat.name,
                tint = iconTint,
                modifier = Modifier.size(16.dp)
            )
        }
        Text(
            cat.name,
            color = contentColor,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp
        )
    }
}