package com.example.walletapp.wallet.presentation.ui.drawableMenu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.walletapp.navigation.Screen
import com.example.walletapp.ui.theme.CurrencyRates
import com.example.walletapp.ui.theme.Follow
import com.example.walletapp.ui.theme.Help
import com.example.walletapp.ui.theme.Home
import com.example.walletapp.ui.theme.Investments
import com.example.walletapp.ui.theme.Records
import com.example.walletapp.ui.theme.Settings
import com.example.walletapp.ui.theme.Statistics
import com.example.walletapp.ui.theme.budjets
import com.example.walletapp.ui.theme.debts
import com.example.walletapp.ui.theme.goals
import com.example.walletapp.ui.theme.shoppingList

data class NavItem(val icon: ImageVector, val label: String,val color: Color,val route: String? = null)


@Composable
fun DrawerBody(
    navController: NavController,
    onDrawerClose: () -> Unit,
    onNavigate: (String) -> Unit,
) {
    val scrollState = rememberScrollState()

    val items = listOf(
        // Asosiy Navigatsiya
        NavItem(Icons.Default.Home, "Home", Home, Screen.Home.route),
        NavItem(Icons.Default.AccountBalance, "Accounts", Records, Screen.Wallet.route),
        NavItem(Icons.Default.BarChart, "Statistics", Statistics, Screen.Charts.route),

// Financial Tools (Moliyaviy Vositalar)
        NavItem(Icons.Default.AccountBalanceWallet, "Budgets", budjets, Screen.Budgets.route),
        NavItem(Icons.Default.ShoppingCart, "Shopping List", shoppingList, Screen.ShoppingLists.route),
        NavItem(Icons.Default.MoneyOff, "Debts", debts,Screen.DebtsScreen.route),
        NavItem(Icons.Default.TrackChanges, "Goals", goals, Screen.Goals.route),
        NavItem(Icons.Default.CurrencyExchange, "Currency Rates", CurrencyRates),

// Additional (Qo'shimcha)
        NavItem(Icons.Default.Share, "Invite Friends", Investments),
        NavItem(Icons.Default.Public, "Follow Us", Follow),
        NavItem(Icons.Default.Help, "Help", Help),
        NavItem(Icons.Default.Settings, "Settings", Settings,Screen.SettingScreen.route),

        )

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.onPrimaryContainer)
            .verticalScroll(scrollState)
    ) {
        items.forEach { item ->
            if (item.label== "Budgets" || item.label == "Invite Friends"){
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }
            NavigationDrawerItem(
                label = { Text(item.label) },
                icon = { Icon(item.icon, contentDescription = null, tint = item.color) },
                selected = false,
                onClick = {
                    item.route?.let { route ->
                        onNavigate(route)
                    }
                    onDrawerClose()
                },
                modifier = Modifier.padding(horizontal = 12.dp),
            )
        }
    }
}