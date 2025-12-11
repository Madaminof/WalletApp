package com.example.walletapp.core

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import com.example.walletapp.ui.theme.WalletAppTheme
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import com.example.walletapp.navigation.NavGraph
import com.example.walletapp.wallet.presentation.ui.budjets.BudgetViewModel
import com.example.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencyManager
import com.example.walletapp.wallet.presentation.ui.otherScreens.settings.items.numFormat.NumberFormatManager
import com.example.walletapp.wallet.presentation.ui.otherScreens.settings.items.themes.ThemeManager
import com.example.walletapp.wallet.presentation.viewmodel.AccountViewModel
import com.example.walletapp.wallet.presentation.viewmodel.AddTransactionViewModel
import com.example.walletapp.wallet.presentation.viewmodel.DebtsViewModel
import com.example.walletapp.wallet.presentation.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        ThemeManager.applyTheme(this)
        CurrencyManager.initialize(applicationContext)
        NumberFormatManager.initialize(applicationContext)

        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val themeChangeTrigger by ThemeManager.isThemeChanged

            WalletAppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val homeViewModel: HomeViewModel = hiltViewModel()
                    val addTransactionViewModel: AddTransactionViewModel = hiltViewModel()
                    val addAccountViewModel: AccountViewModel = hiltViewModel()
                    val budgetViewModel: BudgetViewModel = hiltViewModel()
                    val debtsViewModel: DebtsViewModel = hiltViewModel()

                    NavGraph(
                        viewModel = homeViewModel,
                        viewModel1 = addTransactionViewModel,
                        addAccountViewModel = addAccountViewModel,
                        budgetViewModel = budgetViewModel,
                        debtsViewModel = debtsViewModel,
                    )
                }
            }
        }
    }
}
