package com.example.walletapp.wallet.presentation.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.walletapp.R


@Composable
fun HomeBottomBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    val items = listOf(
        BottomBarItem(title = stringResource(R.string.bottom_home), Icons.Default.Home),
        BottomBarItem(title = stringResource(R.string.bottom_charts), Icons.Default.PieChart),
        BottomBarItem(title = stringResource(R.string.bottom_budjets), Icons.Default.AccountBalanceWallet),
        BottomBarItem(title = stringResource(R.string.bottom_account), Icons.Default.Wallet)
    )
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 4.dp,
        modifier = Modifier.height(72.dp)
    ) {
        items.forEachIndexed { index, item ->
            val isSelected = index == selectedIndex

            NavigationBarItem(
                selected = isSelected,
                onClick = { onItemSelected(index) },
                icon = {
                    AnimatedContent(targetState = isSelected) { selected ->
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = if (selected) MaterialTheme.colorScheme.primary else Color.Gray,
                            modifier = Modifier
                                .size(if (selected) 28.dp else 24.dp)
                                .indication(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                )
                        )
                    }
                },
                label = {
                    AnimatedVisibility(visible = isSelected) {
                        Text(
                            text = item.title,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                },
                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent,
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = Color.Gray,
                ),
            )
        }
    }
}

data class BottomBarItem(val title: String, val icon: ImageVector)
