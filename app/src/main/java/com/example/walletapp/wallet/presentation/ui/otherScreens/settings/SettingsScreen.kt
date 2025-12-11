package com.example.walletapp.wallet.presentation.ui.otherScreens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.walletapp.wallet.presentation.ui.otherScreens.settings.items.themes.ThemeManager
import com.example.walletapp.navigation.Screen
import com.example.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencyManager
import com.example.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencySelectionDialog
import com.example.walletapp.wallet.presentation.ui.otherScreens.settings.items.numFormat.NumberFormatSelectionDialog
import com.example.walletapp.wallet.presentation.ui.otherScreens.topbar.CustomTopBar


data class SettingItem(
    val title: String,
    val icon: ImageVector,
    val iconTint: Color,
    val route: String? = null,
    val action: (() -> Unit)? = null
)

@Composable
fun SettingsScreen(navController: NavController) {
    val scrollState = rememberScrollState()

    var showThemeDialog by remember { mutableStateOf(false) }
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showformatNumberDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CustomTopBar(
                navController = navController,
                title = "Settings",
                onBackClick = {navController.popBackStack()},
            )

        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            PremiumSubscriptionCard()

            Spacer(Modifier.height(32.dp))

            SettingsSection(title = "Application Settings") {
                SettingCard(
                    items = listOf(
                        SettingItem(
                            "Choose Theme",
                            Icons.Default.DarkMode,
                            Color(0xFFFFB74D),
                            action = { showThemeDialog = true }
                        ),
                        SettingItem(
                            "Accounts",
                            Icons.Default.AccountBalanceWallet,
                            Color(0xFFE57373),
                            route = Screen.Wallet.route
                        ),
                        SettingItem(
                            "Currency",
                            Icons.Default.AttachMoney,
                            Color(0xFF4DB6AC),
                            action = { showCurrencyDialog = true }
                        ),
                        SettingItem(
                            "Number Format",
                            Icons.Default.FormatListNumbered,
                            Color(0xFFFFB74D),
                            action = { showformatNumberDialog = true }
                        ),
                        SettingItem(
                            "Language",
                            Icons.Default.Language,
                            Color(0xFF7986CB)
                        ),
                    ),
                    navController = navController
                )
            }

            Spacer(Modifier.height(24.dp))

            SettingsSection(title = "Data and Notifications") {
                SettingCard(
                    items = listOf(
                        SettingItem("Notifications", Icons.Default.Notifications, Color(0xFF9575CD)),
                        SettingItem("Export Data", Icons.Default.CloudDownload, Color(0xFF7986CB), action = { /* Export logic */ }),
                    ),
                    navController = navController
                )
            }

            Spacer(Modifier.height(24.dp))

            SettingsSection(title = "Help and Actions") {
                SettingCard(
                    items = listOf(
                        SettingItem("Help Center", Icons.Default.HelpCenter, Color(0xFF81C784)),
                        SettingItem("Share App", Icons.Default.Share, Color(0xFF4FC3F7), action = { /* Share App intent */ }),
                    ),
                    navController = navController
                )
            }

            Spacer(Modifier.height(40.dp))
        }


        if (showThemeDialog) {
            ThemeSelectionDialog(
                onDismiss = { showThemeDialog = false }
            )
        }
        if (showCurrencyDialog) {
            CurrencySelectionDialog(
                onDismiss = { showCurrencyDialog = false }
            )
        }
        if (showformatNumberDialog) {
            NumberFormatSelectionDialog(
                onDismiss = { showformatNumberDialog = false }
            )
        }
    }
}

@Composable
fun PremiumSubscriptionCard(
    onUpgradeClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    Icons.Default.WorkspacePremium,
                    contentDescription = "Premium Icon",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Upgrade to Premium",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Unlock all features, remove limits, and support development.",
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onUpgradeClick,
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                )
            ) {
                Text(
                    text = "Get Premium",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}


@Composable
fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
            modifier = Modifier.padding(bottom = 10.dp, start = 8.dp)
        )
        content()
    }
}

@Composable
fun SettingCard(items: List<SettingItem>, navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.6f)),
    ) {
        Column {
            items.forEachIndexed { index, item ->
                SettingRow(item = item) {
                    if (item.route != null) {
                        navController.navigate(item.route)
                    } else {
                        item.action?.invoke()
                    }
                }

                if (index < items.lastIndex) {
                    Divider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        }
    }
}

@Composable
fun SettingRow(item: SettingItem, onClick: () -> Unit) {
    val contentColor = MaterialTheme.colorScheme.onTertiary.copy(0.7f)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(item.iconTint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    item.icon,
                    contentDescription = item.title,
                    tint = item.iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(Modifier.width(16.dp))
            Text(
                text = item.title,
                fontSize = 15.sp,
                color = contentColor,
                fontWeight = FontWeight.Medium,
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Go to ${item.title}",
            tint = MaterialTheme.colorScheme.outline
        )

    }
}