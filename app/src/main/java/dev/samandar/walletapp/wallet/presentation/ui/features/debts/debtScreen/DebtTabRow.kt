package dev.samandar.walletapp.wallet.presentation.ui.features.debts.debtScreen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.utils.Strings

@Composable
fun DebtTabRow(
    selectedTab: Int,
    tabs: List<String>,
    onTabSelected: (Int) -> Unit,
    activeColor: Color = MaterialTheme.colorScheme.primary
) {
    val animatedColor by animateColorAsState(
        targetValue = activeColor,
        animationSpec = tween(400),
        label = "TabColor"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f),
        shape = RoundedCornerShape(24.dp)
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = animatedColor,
            indicator = { tabPositions ->
                Box(
                    Modifier
                        .tabIndicatorOffset(tabPositions[selectedTab])
                        .fillMaxSize()
                        .padding(4.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(animatedColor.copy(alpha = 0.12f))
                        .border(1.dp, animatedColor.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                )
            },
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                val isSelected = selectedTab == index

                Tab(
                    selected = isSelected,
                    onClick = { onTabSelected(index) },
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .height(44.dp),
                    interactionSource = remember { MutableInteractionSource() },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp,
                                letterSpacing = 0.3.sp
                            ),
                            color = if (isSelected) animatedColor else MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.4f)
                        )
                    }
                )
            }
        }
    }
}


@Composable
fun EmptyDebtsState(message: String,activeColor: Color = MaterialTheme.colorScheme.primary
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Surface(
                modifier = Modifier.size(100.dp),
                color = activeColor.copy(0.15f),
                shape = CircleShape
            ) {
                Icon(
                    painter = painterResource(R.drawable.debt_ic2),
                    contentDescription = null,
                    modifier = Modifier.padding(24.dp).fillMaxSize(),
                    tint = activeColor.copy(0.8f)
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(Strings.empty_debts_desc), // "Hozircha hamma hisob-kitoblar joyida"
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                ),
                color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}