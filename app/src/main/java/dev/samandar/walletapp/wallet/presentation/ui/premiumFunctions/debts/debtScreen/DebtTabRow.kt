package dev.samandar.walletapp.wallet.presentation.ui.premiumFunctions.debts.debtScreen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SentimentDissatisfied
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.ui.theme.expenseColor
import dev.samandar.walletapp.ui.theme.incomeColor
import dev.samandar.walletapp.utils.Strings


@Composable
fun DebtTabRow(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf(
        stringResource(Strings.tab_i_lent),
        stringResource(Strings.tab_i_borrowed)
    )

    val activeColor = if (selectedTab == 0) incomeColor else expenseColor

    val animatedColor by animateColorAsState(
        targetValue = activeColor,
        animationSpec = tween(500),
        label = "TabColor"
    )

    TabRow(
        selectedTabIndex = selectedTab,
        containerColor = Color.Transparent,
        contentColor = animatedColor,
        indicator = { tabPositions ->
            Box(
                Modifier
                    .tabIndicatorOffset(tabPositions[selectedTab])
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .height(3.dp)
                    .clip(CircleShape)
                    .background(animatedColor)
            )
        },
        divider = {
            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            )
        }
    ) {
        tabs.forEachIndexed { index, title ->
            val isSelected = selectedTab == index

            Tab(
                selected = isSelected,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp,
                            letterSpacing = 0.2.sp
                        ),
                        color = if (isSelected) animatedColor else MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.5f),
                        maxLines = 1
                    )
                },
                selectedContentColor = animatedColor,
                unselectedContentColor = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun EmptyDebtsState(message: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.SentimentDissatisfied,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.3f)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}