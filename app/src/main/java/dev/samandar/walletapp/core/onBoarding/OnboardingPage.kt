package dev.samandar.walletapp.core.onBoarding

import dev.samandar.walletapp.R

data class OnboardingPage(
    val titleRes: Int,
    val descRes: Int,
    val imageRes: Int
)

val onboardingPages = listOf(
    OnboardingPage(R.string.ob_title_1, R.string.ob_desc_1, R.drawable.budget_onboarding_img),
    OnboardingPage(R.string.ob_title_2, R.string.ob_desc_2, R.drawable.shopp_list_onboarding_img),
    OnboardingPage(R.string.ob_title_split, R.string.ob_desc_split, R.drawable.splitbill3),
    OnboardingPage(R.string.ob_title_3, R.string.ob_desc_3, R.drawable.debt_onboarding_img),
    OnboardingPage(R.string.ob_title_4, R.string.ob_desc_4, R.drawable.scanner_onboarding_img)
)