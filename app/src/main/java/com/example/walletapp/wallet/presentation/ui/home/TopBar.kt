package com.example.walletapp.wallet.presentation.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.walletapp.core.AppStatusBarColor
import com.example.walletapp.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    onMenuClick: () -> Unit,
    navController: NavController
) {
    AppStatusBarColor(MaterialTheme.colorScheme.primaryContainer)

    val navigationIconBackground = MaterialTheme.colorScheme.primary.copy(0.07f)

    val actionsBackUpBackground = MaterialTheme.colorScheme.onTertiary.copy(0.03f)
    val actionsBackUpIcon = MaterialTheme.colorScheme.onTertiary.copy(0.4f)

    val actionsSettingBackground = MaterialTheme.colorScheme.onTertiary.copy(0.03f)
    val actionsSettingIcon = MaterialTheme.colorScheme.onTertiary.copy(0.8f)




    TopAppBar(
        title = {
            Text(
                text = "Wallet Analyst",
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                modifier = Modifier.padding(start = 12.dp)
            )
        },
        navigationIcon = {
            Box(
                modifier = Modifier
                    .padding(start = 12.dp)
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(navigationIconBackground),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        actions = {
            Box(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(actionsBackUpBackground),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = { /* Backup action */ }) {
                    Icon(
                        Icons.Default.Backup,
                        contentDescription = "Backup",
                        tint = actionsBackUpIcon
                    )
                }
            }
            Box(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(actionsSettingBackground),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = { navController.navigate(Screen.SettingScreen.route) }) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = actionsSettingIcon
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = Color.White,
            actionIconContentColor = Color.White,
            navigationIconContentColor = Color.White,
        )
    )
}