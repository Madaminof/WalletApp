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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.samandar.walletapp.R
import dev.samandar.walletapp.ui.theme.CurrencyRates
import dev.samandar.walletapp.ui.theme.Export
import dev.samandar.walletapp.ui.theme.followUs
import dev.samandar.walletapp.ui.theme.language
import dev.samandar.walletapp.ui.theme.numberFormat
import dev.samandar.walletapp.ui.theme.theme
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.presentation.ui.drawableMenu.FollowUsDialog
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencySelectionDialog
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.language.LanguageSelectionDialog
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.numFormat.NumberFormatSelectionDialog
import dev.samandar.walletapp.wallet.presentation.ui.topbars.topbarScreen.CustomTopBar


data class SettingItem(
    val title: String,
    val icon: Int,
    val iconTint: Color,
    val isLocked: Boolean = false,
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
                            R.drawable.setting_theme,
                            theme,
                            action = { showThemeDialog = true }
                        ),
                        SettingItem(
                            stringResource(Strings.setting_numFormat_title),
                            R.drawable.setting_nubers,
                            numberFormat,
                            action = { showformatNumberDialog = true }
                        ),
                        SettingItem(
                            stringResource(Strings.setting_language_title),
                            R.drawable.setting_language,
                            language,
                            action = {showLanguageDialog = true}
                        ),
                        SettingItem(
                            stringResource(Strings.setting_currency_title),
                            R.drawable.currency_rates_icon,
                            CurrencyRates,
                            isLocked = true,
                            action = { }
                        ),
                        SettingItem(
                            stringResource(Strings.export_title),
                            R.drawable.export_ic,
                            Export,
                            isLocked = true,
                            action = { }
                        ),
                    ),
                    navController = navController
                )
            }

            Spacer(Modifier.height(24.dp))

            SettingsSection(title = stringResource(Strings.setting_Card_title2)) {
                SettingCard(
                    items = listOf(
                        SettingItem(stringResource(Strings.title_follow_us),
                            R.drawable.setting_follow_us,
                            followUs,
                            action = {showFollowUseDialog = true}
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
    val contentColor = MaterialTheme.colorScheme.onTertiary.copy(0.8f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !item.isLocked, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Box
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(item.iconTint.copy(alpha = if (item.isLocked) 0.05f else 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(item.icon),
                contentDescription = null,
                tint = if (item.isLocked) item.iconTint.copy(0.4f) else item.iconTint,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(Modifier.width(16.dp))

        // Title
        Text(
            text = item.title,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium,
                color = if (item.isLocked) contentColor.copy(0.4f) else contentColor
            ),
            modifier = Modifier.weight(1f)
        )

        // Right Action (Arrow yoki Coming Soon Badge)
        if (item.isLocked) {
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = stringResource(Strings.coming_soon),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        } else {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline.copy(0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}