package dev.samandar.walletapp.wallet.presentation.ui.premiumFunctions.debts.addDebt

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.samandar.walletapp.ui.theme.expenseColor
import dev.samandar.walletapp.ui.theme.incomeColor
import dev.samandar.walletapp.wallet.domain.model.debt.DebtType
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R


@Composable
fun DebtTypeSelector(
    selectedType: DebtType,
    onTypeSelect: (DebtType) -> Unit
) {
    val isLent = selectedType == DebtType.LENT

    val targetActiveColor = if (isLent) incomeColor else expenseColor
    val animatedColor by animateColorAsState(
        targetValue = targetActiveColor,
        animationSpec = tween(400),
        label = "color"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .width(300.dp)
                .height(45.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(0.1f))
                .padding(3.dp)
        ) {
            val maxWidth = maxWidth
            val indicatorOffset by animateDpAsState(
                targetValue = if (isLent) 0.dp else maxWidth / 2 - 3.dp,
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                label = "slide"
            )
            Box(
                modifier = Modifier
                    .offset(x = indicatorOffset)
                    .fillMaxWidth(0.5f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(16.dp))
                    .background(animatedColor)
            )

            Row(modifier = Modifier.fillMaxSize()) {
                TabOption(
                    label = stringResource(R.string.debt_type_lent),
                    isSelected = isLent,
                    modifier = Modifier.weight(1f),
                    onClick = { onTypeSelect(DebtType.LENT) }
                )
                TabOption(
                    label = stringResource(R.string.debt_type_owed),
                    isSelected = !isLent,
                    modifier = Modifier.weight(1f),
                    onClick = { onTypeSelect(DebtType.BORROWED) }
                )
            }
        }
    }
}

@Composable
private fun TabOption(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onTertiary.copy(0.1f),
        animationSpec = tween(250)
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
            ),
            color = textColor
        )
    }
}