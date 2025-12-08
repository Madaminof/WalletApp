package com.example.walletapp.wallet.presentation.ui.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.example.walletapp.core.AppStatusBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    onMenuClick: () -> Unit,
) {
    AppStatusBarColor(MaterialTheme.colorScheme.primaryContainer)
    TopAppBar(
        title = {
            Text(text = "Home", fontWeight = FontWeight.Medium)
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = null, tint =  MaterialTheme.colorScheme.primary)
            }
        },
        actions = {
            IconButton(onClick = { /* Notifications bosilganda */ }) {
                Icon(Icons.Default.Backup, contentDescription = "Backup", tint =  MaterialTheme.colorScheme.onTertiary.copy(0.7f))
            }
            IconButton(onClick = { /* Notifications bosilganda */ }) {
                Icon(Icons.Default.Settings, contentDescription = "Notifications", tint =  MaterialTheme.colorScheme.onTertiary,)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onTertiary,
            actionIconContentColor = MaterialTheme.colorScheme.onTertiary,
            navigationIconContentColor = MaterialTheme.colorScheme.onTertiary,
        )
    )
}
