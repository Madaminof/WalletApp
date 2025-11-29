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
import androidx.compose.material.icons.filled.ExitToApp
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
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.walletapp.auth.presentation.AuthViewModel
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
import kotlinx.coroutines.launch

data class NavItem(val icon: ImageVector, val label: String,val color: Color,val route: String? = null)


@Composable
fun DrawerBody(
    navController: NavController,
    onDrawerClose: () -> Unit,
    onBottomBarScreenSelected: (Int) -> Unit,
    authViewModel: AuthViewModel
    ) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val items = listOf(
        // Asosiy
        NavItem(Icons.Default.Home, "Home", Home, Screen.Home.route),
        NavItem(Icons.Default.AccountBalance, "Hisobotlar", Records),
        NavItem(Icons.Default.TrendingUp, "Investitsiyalar", Investments),
        NavItem(Icons.Default.BarChart, "Statistikalar", Statistics,Screen.Charts.route),

        // Moliyaviy Vositalar
        NavItem(Icons.Default.AccountBalanceWallet, "Budjetlar", budjets,Screen.Budgets.route),
        NavItem(Icons.Default.MoneyOff, "Qarzdorlik", debts),
        NavItem(Icons.Default.TrackChanges, "Maqsadlar", goals,Screen.Goals.route),
        NavItem(Icons.Default.ShoppingCart, "Xaridlar ro‘yxati", shoppingList,Screen.ShoppingLists.route),
        NavItem(Icons.Default.CurrencyExchange, "Valyuta kurslari", CurrencyRates),

        // Qo'shimcha
        NavItem(Icons.Default.Share, "Do‘stlarni taklif qilish", Investments),
        NavItem(Icons.Default.Public, "Bizni kuzatish", Follow),
        NavItem(Icons.Default.Help, "Yordam", Help),
        NavItem(Icons.Default.Settings, "Yordam", Settings,Screen.Settings.route),
        )

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.onBackground)
            .verticalScroll(scrollState)
    ) {
        items.forEach { item ->
            if (item.label== "Budjetlar" || item.label == "Do‘stlarni taklif qilish"){
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }
            NavigationDrawerItem(
                label = { Text(item.label) },
                icon = { Icon(item.icon, contentDescription = null, tint = item.color) },
                selected = false,
                onClick = {
                    item.route?.let { route ->
                        when (route) {
                            Screen.Home.route -> onBottomBarScreenSelected(0)
                            Screen.Charts.route -> onBottomBarScreenSelected(1)
                            Screen.Category.route -> onBottomBarScreenSelected(2)
                            Screen.Settings.route -> onBottomBarScreenSelected(3)

                            else -> navController.navigate(route) {
                                navController.graph.startDestinationRoute?.let { popUpTo(it) }
                                launchSingleTop = true
                            }
                        }
                    }
                    onDrawerClose()
                },
                modifier = Modifier.padding(horizontal = 12.dp),
            )
        }
        NavigationDrawerItem(
            label = { Text("Chiqish") },
            icon = { Icon(Icons.Default.ExitToApp, contentDescription = null) },
            selected = false,
            onClick = {
                authViewModel.signOut()
                scope.launch { drawerState.close()
                } },
            colors = NavigationDrawerItemDefaults.colors(
                unselectedTextColor = MaterialTheme.colorScheme.error,
                unselectedIconColor = MaterialTheme.colorScheme.error
            ),
            modifier = Modifier.padding(horizontal = 12.dp).padding(top = 16.dp, bottom = 32.dp)
        )
    }
}