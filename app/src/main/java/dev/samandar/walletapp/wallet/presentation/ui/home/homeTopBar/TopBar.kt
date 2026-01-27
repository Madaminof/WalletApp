package dev.samandar.walletapp.wallet.presentation.ui.home.homeTopBar

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.samandar.walletapp.R
import dev.samandar.walletapp.navigation.Screen
import dev.samandar.walletapp.ui.theme.Settings
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.presentation.ui.topbars.topbarScreen.PremiumButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    onMenuClick: () -> Unit,
    navController: NavController
) {

    TopAppBar(
        title = {
            Text(
                text = stringResource(Strings.title_home),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onTertiary.copy(0.9f),
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        },
        navigationIcon = {
            PremiumButton(
                onClick = onMenuClick,
                icon = R.drawable.menu_icon2,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
            )
        },
        actions = {
            PremiumButton(
                onClick = {navController.navigate(Screen.SettingScreen.route)},
                icon = R.drawable.setting_icon,
                color = Settings,
                modifier = Modifier.padding(end = 16.dp)
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        )
    )
}
