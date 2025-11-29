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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.walletapp.navigation.Screen
import com.example.walletapp.wallet.domain.model.Account
import com.example.walletapp.wallet.presentation.viewmodel.AccountViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

private const val MAX_ACCOUNTS = 4

private fun parseColor(colorHex: String?): Color {
    return try {
        if (colorHex.isNullOrBlank() || !colorHex.startsWith("#")) {
            Color(0xFF8D6E63)
        } else {
            Color(android.graphics.Color.parseColor(colorHex))
        }
    } catch (e: IllegalArgumentException) {
        Color(0xFF8D6E63)
    }
}
@Composable
private fun formatBalance(balance: Double): String {
    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance(Locale("uz", "UZ")).apply {
            minimumFractionDigits = 0
            maximumFractionDigits = 0
        }
    }
    return currencyFormatter.format(balance)
}

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
                    containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiary
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
    val borderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp, 8.dp, 8.dp, 8.dp)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onBackground),

        shape = RoundedCornerShape(20.dp),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(accounts.size) { index ->
                    WalletCardItem(account = accounts[index]) { onItemClick(it) }
                }
                if (accounts.size < MAX_ACCOUNTS) {
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
}
@Composable
fun WalletCardItem(account: Account, onClick: (Account) -> Unit) {
    val baseColor = parseColor(account.colorHex)
    val lightColor = lerp(baseColor, Color.White, 0.2f)
    val darkColor = lerp(baseColor, Color.Black, 0.2f)

    val contentColor = if (isLightColor(baseColor)) Color.Black else Color.White
    val secondaryContentColor = contentColor.copy(alpha = 0.8f)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clickable { onClick(account) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(lightColor, darkColor),
                        startY = 0f,
                        endY = 500f
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Ikonka
                account.iconResId?.let {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.9f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = it),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = darkColor
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "Balans",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = secondaryContentColor,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                    Text(
                        text = formatBalance(account.initialBalance),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = contentColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = account.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = secondaryContentColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
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
            .height(150.dp)
            .clickable(enabled = canAddMore) { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onBackground),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 1.dp,
                    color = if (canAddMore) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add Account",
                modifier = Modifier.size(40.dp),
                tint = if (canAddMore) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

private fun isLightColor(color: Color): Boolean {
    val luminance = 0.2126 * color.red + 0.7152 * color.green + 0.0722 * color.blue
    return luminance > 0.5
}