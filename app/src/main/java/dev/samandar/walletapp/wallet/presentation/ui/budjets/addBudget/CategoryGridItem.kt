package dev.samandar.walletapp.wallet.presentation.ui.budjets.addBudget

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.wallet.domain.model.Category
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.categories.getTranslatedName
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.categories.helper.getSafeIconId


@Composable
fun OptimalCategoryGridItem(
    category: Category,
    isSelected: Boolean,
    onCategoryClick: () -> Unit
) {
    val categoryName = getTranslatedName(category.name)
    val customCategoryColor: Color = Color(category.colorArgb)

    val selectedContainerColor = customCategoryColor.copy(0.1f)

    val iconBackgroundColor = customCategoryColor

    val containerBgColor by animateColorAsState(
        targetValue = if (isSelected) selectedContainerColor else Color.Transparent,
        label = "CategoryContainerBg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) customCategoryColor else MaterialTheme.colorScheme.onTertiary.copy(0.7f),
        label = "CategoryTextColor"
    )

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onCategoryClick)
            .background(containerBgColor)
            .border(
                width = if (isSelected) 1.dp else 0.dp,
                color = if (isSelected) customCategoryColor.copy(alpha = 0.8f) else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconBackgroundColor.copy(0.15f)),
            contentAlignment = Alignment.Center
        ) {
            val iconSize = 20.dp
            val context = LocalContext.current
            val safeIconId = remember(category.iconResId) {
                val id = category.iconResId ?: 0
                if (id != 0) {
                    getSafeIconId(context, id)
                } else {
                    0
                }
            }
            if (safeIconId != 0) {
                Icon(
                    painter = painterResource(id = safeIconId),
                    contentDescription = category.name,
                    tint = customCategoryColor,
                    modifier = Modifier.size(iconSize)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Category,
                    contentDescription = category.name,
                    tint = customCategoryColor,
                    modifier = Modifier.size(iconSize)
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = categoryName.toString(),
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = textColor,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 2.dp)
        )
    }
}