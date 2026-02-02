package dev.samandar.walletapp.core.onBoarding

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.samandar.walletapp.wallet.data.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.content.Context
import dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.currency.CurrencyManager

enum class OnboardingStep { LANGUAGE, CURRENCY, PAGER }

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingManager: OnboardingManager,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val isOnboardingRequired = onboardingManager.isOnboardingRequired
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    var currentStep by mutableStateOf(OnboardingStep.LANGUAGE)
        private set

    fun onLanguageSelected(langCode: String) {
        viewModelScope.launch {
            settingsRepository.saveLanguage(langCode)
            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(langCode)
            AppCompatDelegate.setApplicationLocales(appLocale)

            currentStep = OnboardingStep.CURRENCY
        }
    }

    fun onCurrencySelected(context: Context, currencyCode: String) {
        CurrencyManager.saveCurrency(context, currencyCode)

        currentStep = OnboardingStep.PAGER
    }

    fun completeOnboarding(onFinish: () -> Unit) {
        viewModelScope.launch {
            onboardingManager.saveOnboardingCompleted()
            onFinish()
        }
    }


    // OnboardingViewModel ichida
    fun navigateBack() {
        currentStep = when (currentStep) {
            OnboardingStep.CURRENCY -> OnboardingStep.LANGUAGE
            OnboardingStep.PAGER -> OnboardingStep.CURRENCY
            else -> OnboardingStep.LANGUAGE
        }
    }
}