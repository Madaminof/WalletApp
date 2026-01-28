package dev.samandar.walletapp.core

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import dev.samandar.walletapp.core.onBoarding.OnboardingViewModel
import dev.samandar.walletapp.navigation.NavGraphMain
import dev.samandar.walletapp.navigation.Screen
import dev.samandar.walletapp.ui.theme.WalletAppTheme
import dev.samandar.walletapp.wallet.presentation.ui.budjets.BudgetViewModel
import dev.samandar.walletapp.wallet.presentation.ui.charts.categoryStatistic.viewmodel.CategoryStatisticsViewModel
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.SoundManager
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.SettingsViewModel
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencyManager
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.numFormat.NumberFormatManager
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.themes.ThemeManager
import dev.samandar.walletapp.wallet.presentation.viewmodel.*
import java.util.Locale

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        SoundManager.init(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        ThemeManager.applyTheme(this)
        CurrencyManager.initialize(applicationContext)
        NumberFormatManager.initialize(applicationContext)


        setContent {
            val context = LocalContext.current

            val onboardingViewModel: OnboardingViewModel = hiltViewModel()
            val settingsViewModel: SettingsViewModel = hiltViewModel()

            val langCode by settingsViewModel.currentLanguageCode.collectAsStateWithLifecycle()
            val isOnboardingRequired by onboardingViewModel.isOnboardingRequired.collectAsStateWithLifecycle()

            LaunchedEffect(langCode) {
                updateLocale(context, langCode)
            }

            WalletAppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    isOnboardingRequired?.let { required ->
                        val startRoute = remember {
                            if (required) Screen.Onboarding.route else Screen.Splash.route
                        }

                        NavGraphMain(
                            viewModel = hiltViewModel(),
                            addAccountViewModel = hiltViewModel(),
                            budgetViewModel = hiltViewModel(),
                            categoryViewModel = hiltViewModel(),
                            startDestination = startRoute
                        )
                    }
                }
            }
        }

    }
    override fun onDestroy() {
        super.onDestroy()
        SoundManager.release() // Ilova yopilganda xotirani tozalash
    }

    private fun updateLocale(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources = context.resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        resources.updateConfiguration(configuration, resources.displayMetrics)
    }
}