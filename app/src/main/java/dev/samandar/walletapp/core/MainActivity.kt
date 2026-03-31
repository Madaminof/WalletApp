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
import dev.samandar.walletapp.wallet.presentation.ui.home.addTransaction.premiumAddTransaction.SoundManager
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.exportData.viewmodel.ExportViewModel
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.SettingsViewModel
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencyManager
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.numFormat.NumberFormatManager
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.themes.ThemeManager
import java.util.Locale
import dev.samandar.walletapp.wallet.data.currencyManagerApi.currencySyncWorker.setupCurrencySync // Worker funksiyasi

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

            // 1. ViewModel'larni chaqirganda ularga aniq 'key' beramiz.
            // Bu 'Multiple entries with same key' xatosini 100% yo'qotadi.
            val onboardingViewModel: OnboardingViewModel = hiltViewModel(key = "onboarding_vm")
            val settingsViewModel: SettingsViewModel = hiltViewModel(key = "settings_vm")
            val exportViewModel: ExportViewModel = hiltViewModel(key = "export_vm")

            val langCode by settingsViewModel.currentLanguageCode.collectAsStateWithLifecycle()
            val isOnboardingRequired by onboardingViewModel.isOnboardingRequired.collectAsStateWithLifecycle()

            LaunchedEffect(langCode) {
                updateLocale(context, langCode)
            }

            WalletAppTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    isOnboardingRequired?.let { required ->
                        val startRoute = remember(required) {
                            if (required) Screen.Onboarding.route else Screen.Splash.route
                        }

                        // 2. NavGraphMain ichidagi hiltViewModel() larni ham key bilan ta'minlaymiz
                        NavGraphMain(
                            viewModel = hiltViewModel(key = "main_vm"),
                            addAccountViewModel = hiltViewModel(key = "add_account_vm"),
                            budgetViewModel = hiltViewModel(key = "budget_vm"),
                            categoryViewModel = hiltViewModel(key = "category_vm"),
                            startDestination = startRoute,
                            exportViewModel = exportViewModel
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