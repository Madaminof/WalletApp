package com.example.walletapp.wallet.presentation.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.walletapp.R
import com.example.walletapp.core.AppStatusBarColor
import com.example.walletapp.wallet.presentation.viewmodel.BalanceItem
import com.example.walletapp.wallet.presentation.viewmodel.HomeViewModel
import com.google.firebase.auth.FirebaseUser


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    user: FirebaseUser?,
    selectedIndex: Int,
    onMenuClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()

) {
    AppStatusBarColor(MaterialTheme.colorScheme.primaryContainer)
    TopAppBar(
        title = {
            when (selectedIndex) {
                0 -> TitleSection(
                    user = user,
                    viewModel = viewModel
                )
                1 -> Text(text = stringResource(R.string.title_Charts), color = MaterialTheme.colorScheme.onTertiary, fontWeight = FontWeight.Bold)
                2 -> Text(text = stringResource(R.string.title_Budgets), color = MaterialTheme.colorScheme.onTertiary, fontWeight = FontWeight.Bold)
                3 -> Text(text = stringResource(R.string.title_Account), color = MaterialTheme.colorScheme.onTertiary, fontWeight = FontWeight.Bold)
            }
        },
        navigationIcon = {
            IconButton(onClick =onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = null, tint =  MaterialTheme.colorScheme.onTertiary,)
            }
        },
        actions = {
            when (selectedIndex) {
                3 -> {
                    IconButton(onClick = { /* Settings mantiqi */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint =  MaterialTheme.colorScheme.onTertiary,)
                    }
                }
                else -> {
                    IconButton(onClick = { /* Notifications bosilganda */ }) {
                        Icon(Icons.Default.NotificationsNone, contentDescription = "Notifications", tint =  MaterialTheme.colorScheme.onTertiary,)
                    }
                }
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



@Composable
fun TitleSection(
    user: FirebaseUser?,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val balances by viewModel.balanceItemsFlow.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    var selectedBalance by remember { mutableStateOf<BalanceItem?>(null) }

    LaunchedEffect(balances) {
        selectedBalance = balances.firstOrNull()
    }

    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.padding(bottom = 6.dp)
    ) {
        Text(
            text = user?.displayName ?: "Username",
            color = MaterialTheme.colorScheme.onTertiary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            letterSpacing = 0.2.sp
        )
        Spacer(modifier = Modifier.height(1.dp))
        Surface(
            color = Color.White.copy(alpha = 0.3f),
            shape = MaterialTheme.shapes.small,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable(enabled = balances.isNotEmpty()) { expanded = !expanded }
                    .padding(horizontal = 8.dp)
                    .height(40.dp)
            ) {
                Text(
                    text = "${selectedBalance?.title ?: "Loading"}: ${selectedBalance?.amount ?: "0 UZS"}",
                    color = MaterialTheme.colorScheme.onTertiary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiary,
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            balances.forEach { balance ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = balance.iconResId),
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("${balance.title}: ${balance.amount}", color = MaterialTheme.colorScheme.onTertiary)
                        }
                    },
                    onClick = {
                        selectedBalance = balance
                        expanded = false
                    }
                )
            }
        }
    }
}
