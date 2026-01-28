package dev.samandar.walletapp.core.onBoarding.onboardingScreen

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
import androidx.hilt.navigation.compose.hiltViewModel
import dev.samandar.walletapp.core.onBoarding.OnboardingViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onFinish: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        AnimatedContent(
            targetState = viewModel.showLanguageStep,
            transitionSpec = {
                if (!targetState) {
                    (slideInHorizontally { width -> width } + fadeIn(tween(400))) with
                            (slideOutHorizontally { width -> -width } + fadeOut(tween(400)))
                } else {
                    fadeIn(tween(400)) with fadeOut(tween(400))
                }
            },
            label = "onboarding_step_transition"
        ) { isLanguageStep ->
            if (isLanguageStep) {
                LanguageSelectionStep(
                    onLanguageSelected = { langCode ->
                        viewModel.onLanguageSelected(langCode)
                    }
                )
            } else {
                OnboardingPagerStep(
                    onFinish = {
                        viewModel.completeOnboarding(onFinish)
                    }
                )
            }
        }
    }
}