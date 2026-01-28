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

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingManager: OnboardingManager,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    // MainActivity uchun: Onboarding kerakmi yoki yo'qligini kuzatuvchi Flow
    val isOnboardingRequired = onboardingManager.isOnboardingRequired
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null // Ma'lumot o'qilguncha null bo'lib turadi
        )

    var showLanguageStep by mutableStateOf(true)
        private set

    fun onLanguageSelected(langCode: String) {
        viewModelScope.launch {
            // Tilni saqlash
            settingsRepository.saveLanguage(langCode)

            // Tizim tilini o'zgartirish
            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(langCode)
            AppCompatDelegate.setApplicationLocales(appLocale)

            showLanguageStep = false
        }
    }

    fun completeOnboarding(onFinish: () -> Unit) {
        viewModelScope.launch {
            onboardingManager.saveOnboardingCompleted()
            onFinish()
        }
    }
}