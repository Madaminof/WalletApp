package dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.presentation.ui.drawableMenu.FollowUsDialog
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencySelectionDialog
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.language.LanguageSelectionDialog
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.numFormat.NumberFormatSelectionDialog
import dev.samandar.walletapp.wallet.presentation.ui.topbars.topbarScreen.CustomTopBar


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
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showFollowUseDialog by remember { mutableStateOf(false) }


    Scaffold(
        topBar = {
            CustomTopBar(
                title = stringResource(Strings.title_settings),
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
            SettingsSection(title = stringResource(Strings.setting_Card_title1)) {
                SettingCard(
                    items = listOf(
                        SettingItem(
                            stringResource(Strings.setting_choose_theme_title),
                            Icons.Default.DarkMode,
                            Color(0xFFFFB74D),
                            action = { showThemeDialog = true }
                        ),
                        SettingItem(
                            stringResource(Strings.setting_currency_title),
                            Icons.Default.CurrencyExchange,
                            Color(0xFF4DB6AC),
                            action = { showCurrencyDialog = true }
                        ),
                        SettingItem(
                            stringResource(Strings.setting_numFormat_title),
                            Icons.Default.FormatListNumbered,
                            Color(0xFFFFB74D),
                            action = { showformatNumberDialog = true }
                        ),
                        SettingItem(
                            stringResource(Strings.setting_language_title),
                            Icons.Default.Language,
                            Color(0xFF7986CB),
                            action = {showLanguageDialog = true}
                        ),
                    ),
                    navController = navController
                )
            }

            Spacer(Modifier.height(24.dp))

            SettingsSection(title = stringResource(Strings.setting_Card_title2)) {
                SettingCard(
                    items = listOf(
                        SettingItem(stringResource(Strings.title_follow_us), Icons.Default.Public, Color(
                            0xFFEC1D65
                        ), action = {showFollowUseDialog = true}
                        ),
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
        if (showLanguageDialog) {
            LanguageSelectionDialog(
                onDismiss = { showLanguageDialog = false }
            )
        }
        if (showFollowUseDialog) {
            FollowUsDialog(
                onDismiss = { showFollowUseDialog = false }
            )
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