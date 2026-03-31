package dev.samandar.walletapp.core.onBoarding.onboardingScreen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import dev.samandar.walletapp.core.onBoarding.OnboardingStep
import dev.samandar.walletapp.core.onBoarding.OnboardingViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onFinish: () -> Unit
) {
    val context = LocalContext.current
    val currentStep = viewModel.currentStep

    // Orqaga qaytishni boshqarish uchun (Android Back Button)
    BackHandler(enabled = currentStep != OnboardingStep.LANGUAGE) {
        viewModel.navigateBack()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        AnimatedContent(
            targetState = currentStep,
            transitionSpec = {
                // Agar targetState (yangi holat) ning indeksi eski holatdan katta bo'lsa - OLG'A
                if (targetState.ordinal > initialState.ordinal) {
                    (slideInHorizontally { width -> width } + fadeIn(tween(400))) with
                            (slideOutHorizontally { width -> -width } + fadeOut(tween(400)))
                } else {
                    // Aks holda (kichik bo'lsa) - ORQAGA
                    (slideInHorizontally { width -> -width } + fadeIn(tween(400))) with
                            (slideOutHorizontally { width -> width } + fadeOut(tween(400)))
                }
            },
            label = "onboarding_step_transition"
        ) { step ->
            when (step) {
                OnboardingStep.LANGUAGE -> {
                    LanguageSelectionStep(
                        onLanguageSelected = { langCode ->
                            viewModel.onLanguageSelected(langCode)
                        }
                    )
                }
                /*OnboardingStep.CURRENCY -> {
                    CurrencySelectionStep(
                        onCurrencySelected = { currencyCode ->
                            viewModel.onCurrencySelected(context, currencyCode)
                        },
                        onBack = { viewModel.navigateBack() } // UI dagi orqaga tugmasi uchun
                    )
                }*/
                OnboardingStep.PAGER -> {
                    OnboardingPagerStep(
                        onFinish = {
                            viewModel.completeOnboarding(onFinish)
                        },
                        onBack = { viewModel.navigateBack() }
                    )
                }
            }
        }
    }
}