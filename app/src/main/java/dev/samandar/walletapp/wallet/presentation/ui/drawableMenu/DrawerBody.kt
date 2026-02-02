package dev.samandar.walletapp.wallet.presentation.ui.drawableMenu

import androidx.compose.foundation.background
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
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.samandar.walletapp.R
import dev.samandar.walletapp.navigation.Screen
import dev.samandar.walletapp.ui.theme.CurrencyRates
import dev.samandar.walletapp.ui.theme.Home
import dev.samandar.walletapp.ui.theme.Records
import dev.samandar.walletapp.ui.theme.Settings
import dev.samandar.walletapp.ui.theme.Statistics
import dev.samandar.walletapp.ui.theme.budjets
import dev.samandar.walletapp.ui.theme.debts
import dev.samandar.walletapp.ui.theme.shoppingList
import dev.samandar.walletapp.utils.Strings
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencySelectionDialog


data class NavItem(
    val icon: Int,
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
                NavItem(R.drawable.home_icon, Strings.drawer_menu_title_home, Home, Screen.Home.route),
                NavItem(R.drawable.account_icon, Strings.title_account, Records, Screen.Wallet.route),
                NavItem(R.drawable.statistic_icon, Strings.title_statistics, Statistics, Screen.CategoryStatisticsScreen.route)
            ),
            listOf(
                NavItem(R.drawable.budget_ic, Strings.title_budgets, budjets, Screen.Budgets.route),
                NavItem(R.drawable.shopp_list_ic, Strings.title_shopping_lists, shoppingList, Screen.ShoppingLists.route),
                NavItem(R.drawable.debt_ic2, Strings.title_debts, debts, Screen.DebtsScreen.route),
            ),
            listOf(
                NavItem(R.drawable.setting_icon, Strings.title_settings, Settings, Screen.SettingScreen.route)
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onPrimaryContainer)
            .verticalScroll(scrollState)
            .padding(bottom = 24.dp)
    ) {
        DrawerHeader() // Header endi Column ichida

        menuGroups.forEachIndexed { index, group ->
            Column(modifier = Modifier.padding(horizontal = 16.dp).padding(top = 4.dp)) {
                group.forEach { item ->
                    val isSelected = currentRoute == item.route
                    val isLocked = item.labelRes == Strings.title_currency_rates // Locked logikasi

                    DrawerMenuItem(
                        item = item,
                        isSelected = isSelected,
                        isLocked = isLocked,
                        onClick = {
                            if (!isLocked) {
                                if (item.isDialog) {
                                    showCurrencyDialog = true
                                } else {
                                    item.route?.let { onNavigate(it) }
                                }
                                onDrawerClose()
                            }
                        }
                    )
                }
            }
            if (index < menuGroups.size - 1) {
                Divider(
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.1f)
                )
            }
        }
    }
}

@Composable
fun DrawerMenuItem(
    item: NavItem,
    isSelected: Boolean,
    isLocked: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        enabled = !isLocked,
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(0.08f) else Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(item.icon),
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else item.color,
                modifier = Modifier.size(26.dp).alpha(if (isLocked) 0.4f else 1f)
            )

            Spacer(Modifier.width(16.dp))

            Text(
                text = stringResource(item.labelRes),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                    fontSize = 15.sp
                ),
                color = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onTertiary.copy(if (isLocked) 0.3f else 0.8f),
                modifier = Modifier.weight(1f)
            )

            if (isLocked) {
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = stringResource(Strings.coming_soon),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 9.sp
                    )
                }
            }
        }
    }
}