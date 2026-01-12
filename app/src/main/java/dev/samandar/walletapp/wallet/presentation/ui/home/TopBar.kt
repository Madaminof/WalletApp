package dev.samandar.walletapp.wallet.presentation.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.samandar.walletapp.navigation.Screen
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
                icon = Icons.Outlined.Menu,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
            )
        },
        actions = {
            PremiumButton(
                onClick = {navController.navigate(Screen.SettingScreen.route)},
                icon = Icons.Default.Settings,
                color = MaterialTheme.colorScheme.onTertiary.copy(0.8f),
                modifier = Modifier.padding(end = 16.dp)
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        )
    )
}