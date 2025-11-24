package com.example.walletapp.wallet.presentation.ui.account

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.walletapp.navigation.Screen
import com.example.walletapp.ui.theme.shoppingList
import com.example.walletapp.wallet.domain.model.Account
import com.example.walletapp.wallet.presentation.viewmodel.AccountViewModel
import kotlinx.coroutines.launch

private const val MAX_ACCOUNTS = 4

@Composable
fun WalletScreen(
    accounts: List<Account>,
    navController: NavController,
    accountViewModel: AccountViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var selectedAccount by remember { mutableStateOf<Account?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onBackground)
    ) {

        Column(modifier = Modifier.fillMaxSize()) {
            WalletCard(
                accounts = accounts,
                navController = navController,
                onLimitReached = {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Hamyon qo'shish cheklangan (Max 4 ta)"
                        )
                    }
                },
                onItemClick = { selectedAccount = it }
            )
        }
        selectedAccount?.let { account ->
            AccountDetailBottomSheet(
                account = account,
                onDismiss = { selectedAccount = null },
                onUpdate = { acc ->
                    // TODO: Update logic
                    selectedAccount = null
                },
                onDelete = { acc ->
                    accountViewModel.deleteAccount(acc)
                    selectedAccount = null
                }
            )
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            snackbar = { data ->
                Snackbar(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = shoppingList
                ) {
                    Text(text = data.visuals.message)
                }
            }
        )
    }
}

@Composable
fun WalletCard(
    accounts: List<Account>,
    navController: NavController,
    onLimitReached: () -> Unit,
    onItemClick: (Account) -> Unit
) {
    val canAddMore = accounts.size < MAX_ACCOUNTS

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.5f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(accounts.size) { index ->
                    WalletCardItem(account = accounts[index]) { onItemClick(it) }
                }
                item {
                    WalletCardAdd(
                        canAddMore = canAddMore,
                        onClick = {
                            if (canAddMore) {
                                navController.navigate(Screen.addAccound.route)
                            } else {
                                onLimitReached()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun WalletCardAdd(canAddMore: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { onClick() }
            .border(
                width = 2.dp,
                brush = Brush.radialGradient(
                    colors = listOf(
                        if (canAddMore) Color.Green.copy(alpha = 0.3f) else Color.Gray.copy(alpha = 0.2f),
                        Color.Transparent
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add Account",
                modifier = Modifier.size(40.dp),
                tint = if (canAddMore) Color.Green else Color.Gray
            )
        }
    }
}

@Composable
fun WalletCardItem(account: Account, onClick: (Account) -> Unit) {
    val baseColor = account.colorHex?.let { Color(android.graphics.Color.parseColor(it)) }
        ?: MaterialTheme.colorScheme.surface
    val lightColor = lerp(baseColor, Color.White, 0.3f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { onClick(account) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = baseColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            account.iconResId?.let {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(lightColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = it),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = Color.Unspecified
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = account.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isLightColor(baseColor)) Color.Black else Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${account.initialBalance} UZS",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isLightColor(baseColor)) Color.Black.copy(0.8f) else Color.White.copy(0.8f)
            )
        }
    }
}

private fun isLightColor(color: Color): Boolean {
    val luminance = 0.2126 * color.red + 0.7152 * color.green + 0.0722 * color.blue
    return luminance > 0.5
}
