package com.example.walletapp.wallet.presentation.ui.otherScreens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.walletapp.navigation.Screen
import com.example.walletapp.wallet.presentation.ui.otherScreens.topbar.CustomTopBar

val PrimaryAccent = Color(0xFF4759C1)
val SecondaryText = Color.Gray


data class SettingItem(
    val title: String,
    val icon: ImageVector,
    val iconTint: Color,
    val route: String? = null,
    val action: () -> Unit = {}
)

@Composable
fun SettingsScreen(navController: NavController) {
    val scrollState = rememberScrollState()

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

            UserProfileSection(
                userName = "Wallet App",
                userEmail = "info@walletapp.uz",
                balance = "15,250,000 UZS",
                membership = "Premium Member"
            )

            Spacer(Modifier.height(24.dp))

            SettingsSection(title = "Account Settings") {
                SettingCard(
                    items = listOf(
                        SettingItem("Edit Profile", Icons.Default.Person, Color(0xFFE57373)),
                        SettingItem("Setup PIN/FaceID", Icons.Default.Fingerprint, Color(0xFF81C784)),
                        SettingItem("Subscription", Icons.Default.WorkspacePremium, Color(0xFF4FC3F7)),
                        SettingItem("Data Backup", Icons.Default.Backup, Color(0xFF7986CB)),
                    ),
                    navController = navController
                )
            }

            Spacer(Modifier.height(24.dp))

            SettingsSection(title = "Application Management") {
                SettingCard(
                    items = listOf(
                        SettingItem("Choose Theme", Icons.Default.DarkMode, Color(0xFFFFB74D)),
                        SettingItem("Accounts", Icons.Default.AccountBalanceWallet, Color(0xFFE57373), route = Screen.Wallet.route),
                        SettingItem("Number Format", Icons.Default.FormatListNumbered, Color(0xFFFFB74D)),
                        SettingItem("Currency", Icons.Default.AttachMoney, Color(0xFF4DB6AC)),
                        SettingItem("Notifications", Icons.Default.Notifications, Color(0xFF9575CD)),
                        SettingItem("Export Data", Icons.Default.CloudDownload, Color(0xFF7986CB), action = { /* Ma'lumot eksport logikasi */ }),
                    ),
                    navController = navController
                )
            }

            Spacer(Modifier.height(24.dp))

            SettingsSection(title = "Help and Actions") {
                SettingCard(
                    items = listOf(
                        SettingItem("Help Center", Icons.Default.HelpCenter, Color.Gray),
                        SettingItem("Share App", Icons.Default.Share, Color.Gray, action = { /* Share App intent */ }),
                        SettingItem("Privacy Policy", Icons.Default.Policy, Color.Gray),
                        SettingItem("Log Out", Icons.AutoMirrored.Filled.ExitToApp, Color.Red),
                    ),
                    navController = navController
                )
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}


@Composable
fun UserProfileSection(
    userName: String,
    userEmail: String,
    balance: String,
    membership: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryAccent.copy(alpha = 0.95f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "User Avatar",
                    tint = Color.White,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column {
                Text(
                    text = userName,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = userEmail,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Balance: $balance",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
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
            fontWeight = FontWeight.Bold,
            color = SecondaryText,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        content()
    }
}

@Composable
fun SettingCard(items: List<SettingItem>, navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.6f)),
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            items.forEachIndexed { index, item ->
                SettingRow(item = item) {
                    if (item.route != null) {
                        navController.navigate(item.route)
                    } else {
                        item.action()
                    }
                }

                if (index < items.lastIndex) {
                    Divider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                    )
                }
            }
        }
    }
}

@Composable
fun SettingRow(item: SettingItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(item.iconTint.copy(alpha = 0.15f)),
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
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
            )
        }
        if (item.route != null || item.action != {}) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Go to ${item.title}",
                tint = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.6f)
            )
        }
    }
}