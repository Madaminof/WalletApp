package dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.view.ExportBottomSheet
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.shareFile.shareFile
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.viewmodel.ExportResult
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.viewmodel.ExportViewModel
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencySelectionBottomSheet
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.language.LanguageSelectionDialog
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.numFormat.NumberFormatSelectionDialog
import dev.samandar.walletapp.wallet.presentation.ui.topbars.topbarScreen.CustomTopBar
import java.io.File


data class SettingItem(
    val title: String,
    val icon: Int,
    val iconTint: Color,
    val isLocked: Boolean = false,
    val route: String? = null,
    val action: (() -> Unit)? = null,
)

@Composable
fun SettingsScreen(navController: NavController, viewModel: ExportViewModel) {
    val scrollState = rememberScrollState()
    val context = androidx.compose.ui.platform.LocalContext.current // Contextni oldik

    var showThemeDialog by remember { mutableStateOf(false) }
    var showCurrencySheet by remember { mutableStateOf(false) }
    var showformatNumberDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showFollowUseDialog by remember { mutableStateOf(false) }
    var showExportSheet by remember { mutableStateOf(false) }

    val exportState by viewModel.exportState.collectAsStateWithLifecycle()
    LaunchedEffect(exportState) {
        when (val result = exportState) {
            is ExportResult.Success -> {
                val file: File = result.file

                // 1. PDFni ulashish (Share) funksiyasini chaqiramiz
                shareFile(context, file)

                // 2. Statni reset qilamiz
                viewModel.resetExportState()
            }

            is ExportResult.Error -> {
                // Foydalanuvchiga xatoni ko'rsatamiz
                android.widget.Toast.makeText(context, result.message, android.widget.Toast.LENGTH_LONG).show()
                viewModel.resetExportState()
            }

            is ExportResult.Loading -> {
                // Bu yerda xohlasang kichik Toast chiqar: "Eksport qilinmoqda..."
            }

            is ExportResult.Idle -> {}
        }
    }


    Scaffold(
        topBar = {
            CustomTopBar(
                title = stringResource(Strings.title_settings),
                onBackClick = { navController.popBackStack() },
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
                            action = { showLanguageDialog = true }
                        ),
                        SettingItem(
                            stringResource(Strings.export_title),
                            R.drawable.export_ic,
                            Export,
                            isLocked = false,
                            action = { showExportSheet = true }
                        ),
                        /*SettingItem(
                            stringResource(Strings.setting_currency_title),
                            R.drawable.currency_rates_icon,
                            CurrencyRates,
                            isLocked = false,
                            action = { showCurrencySheet = true }
                        ),*/

                    ),
                    navController = navController
                )
            }

            Spacer(Modifier.height(24.dp))

            SettingsSection(title = stringResource(Strings.setting_Card_title2)) {
                SettingCard(
                    items = listOf(
                        SettingItem(
                            stringResource(Strings.title_follow_us),
                            R.drawable.setting_follow_us,
                            followUs,
                            action = { showFollowUseDialog = true }
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
        if (showCurrencySheet) {
            CurrencySelectionBottomSheet(
                onDismiss = { showCurrencySheet = false }
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

        if (showExportSheet) {
            ExportBottomSheet(
                viewModel = viewModel,
                onDismiss = { showExportSheet = false },
                onExportClick = { config ->
                    // MANA BU JOYDA VIEWMODELNI CHAQIRISH KERAK!
                    viewModel.startExport(config)
                    // Sheetni yopish (ixtiyoriy, export tugaguncha kutsa ham bo'ladi)
                    showExportSheet = false
                }
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
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                0.6f
            )
        ),
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