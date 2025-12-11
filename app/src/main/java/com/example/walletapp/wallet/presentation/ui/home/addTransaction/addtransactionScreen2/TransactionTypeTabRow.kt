package com.example.walletapp.wallet.presentation.ui.home.addTransaction.addtransactionScreen2

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.walletapp.wallet.domain.model.TransactionType

@Composable
fun TransactionTypeTabRow(
    selected: TransactionType,
    onSelect: (TransactionType) -> Unit
) {
    val tabs = listOf(
        TransactionType.INCOME to "Daromad",
        TransactionType.EXPENSE to "Xarajat",
    )

    val selectedContainerColor = MaterialTheme.colorScheme.primary
    val unselectedContainerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
    val selectedContentColor = MaterialTheme.colorScheme.onPrimary
    val unselectedContentColor = MaterialTheme.colorScheme.onTertiary.copy(0.3f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(unselectedContainerColor),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabs.forEach { (type, title) ->
            val isSelected = type == selected
            val animatedBackgroundColor by animateColorAsState(
                targetValue = if (isSelected) selectedContainerColor else Color.Transparent,
                animationSpec = tween(durationMillis = 300),
                label = "BackgroundColorAnimation"
            )
            val animatedTextColor by animateColorAsState(
                targetValue = if (isSelected) selectedContentColor else unselectedContentColor,
                animationSpec = tween(durationMillis = 300),
                label = "TextColorAnimation"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(2.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        color = animatedBackgroundColor
                    )
                    .clickable { onSelect(type) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    color = animatedTextColor,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}