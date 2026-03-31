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

// Currency qadami butunlay olib tashlandi yoki izohga olindi
enum class OnboardingStep { LANGUAGE, PAGER }

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

            // Til tanlangandan keyin to'g'ridan-to'g'ri Pager-ga o'tamiz
            currentStep = OnboardingStep.PAGER
        }
    }

    /* Kelajak uchun: Valyuta tanlash qadami
    fun onCurrencySelected(context: Context, currencyCode: String) {
        CurrencyManager.saveCurrency(context, currencyCode)
        currentStep = OnboardingStep.PAGER
    }
    */

    fun completeOnboarding(onFinish: () -> Unit) {
        viewModelScope.launch {
            onboardingManager.saveOnboardingCompleted()
            onFinish()
        }
    }

    // Orqaga qaytish mantiqini ham yangi qadamlarga moslab qo'yamiz
    fun navigateBack() {
        currentStep = when (currentStep) {
            OnboardingStep.PAGER -> OnboardingStep.LANGUAGE
            else -> OnboardingStep.LANGUAGE
        }
    }
}