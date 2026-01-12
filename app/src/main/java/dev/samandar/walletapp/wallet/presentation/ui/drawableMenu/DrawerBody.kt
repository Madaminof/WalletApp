package dev.samandar.walletapp.wallet.presentation.ui.drawableMenu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material.icons.rounded.Money
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.samandar.walletapp.navigation.Screen
import dev.samandar.walletapp.ui.theme.CurrencyRates
import dev.samandar.walletapp.ui.theme.Follow
import dev.samandar.walletapp.ui.theme.Help
import dev.samandar.walletapp.ui.theme.Home
import dev.samandar.walletapp.ui.theme.Investments
import dev.samandar.walletapp.ui.theme.Records
import dev.samandar.walletapp.ui.theme.Settings
import dev.samandar.walletapp.ui.theme.Statistics
import dev.samandar.walletapp.ui.theme.budjets
import dev.samandar.walletapp.ui.theme.debts
import dev.samandar.walletapp.ui.theme.goals
import dev.samandar.walletapp.ui.theme.shoppingList
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencySelectionDialog


data class NavItem(
    val icon: ImageVector,
    val labelRes: Int,
    val color: Color,
    val route: String? = null,
    val isDialog: Boolean = false
)

@Composable
fun DrawerBody(
    currentRoute: String?,
    onDrawerClose: () -> Unit,
    onNavigate: (String) -> Unit,
) {
    val scrollState = rememberScrollState()
    var showCurrencyDialog by remember { mutableStateOf(false) }

    val menuGroups = remember {
        listOf(
            listOf(
                NavItem(Icons.Default.Home, Strings.drawer_menu_title_home, Home, Screen.Home.route),
                NavItem(Icons.Default.AccountBalance, Strings.title_account, Records, Screen.Wallet.route),
                NavItem(Icons.Default.BarChart, Strings.title_statistics, Statistics, Screen.Charts.route)
            ),
            listOf(
                NavItem(Icons.Default.AccountBalanceWallet, Strings.title_budgets, budjets, Screen.Budgets.route),
                NavItem(Icons.Default.ShoppingCart, Strings.title_shopping_lists, shoppingList, Screen.ShoppingLists.route),
                NavItem(Icons.Rounded.Money, Strings.title_debts, debts, Screen.DebtsScreen.route),
                NavItem(Icons.Default.CurrencyExchange, Strings.title_currency_rates, CurrencyRates, isDialog = true)
            ),
            listOf(
                NavItem(Icons.Default.Settings, Strings.title_settings, Settings, Screen.SettingScreen.route)
            )
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onPrimaryContainer)
            .verticalScroll(scrollState)
            .padding(vertical = 12.dp)
    ) {
        menuGroups.forEachIndexed { index, group ->
            group.forEach { item ->
                val labelString = stringResource(item.labelRes)
                val isSelected = currentRoute == item.route

                NavigationDrawerItem(
                    label = {
                        Text(
                            text = labelString,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 15.sp
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = if (isSelected) MaterialTheme.colorScheme.primary else item.color,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    selected = isSelected,
                    onClick = {
                        if (item.isDialog) {
                            showCurrencyDialog = true
                            onDrawerClose()
                        } else {
                            item.route?.let { route ->
                                if (currentRoute != route) {
                                    onNavigate(route)
                                }
                            }
                            onDrawerClose()
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 2.dp),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        unselectedContainerColor = Color.Transparent,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedTextColor = MaterialTheme.colorScheme.onTertiary.copy(0.8f)
                    )
                )
            }

            if (index < menuGroups.size - 1) {
                Divider(
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 12.dp),
                    thickness = 0.6.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                )
            }
        }
    }

    if (showCurrencyDialog) {
        CurrencySelectionDialog(
            onDismiss = { showCurrencyDialog = false },
        )
    }
}