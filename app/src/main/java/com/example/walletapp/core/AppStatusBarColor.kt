package com.example.walletapp.core

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun AppStatusBarColor(color: Color, darkIcons: Boolean = false) {
    val systemUi = rememberSystemUiController()
    val darkTheme = isSystemInDarkTheme()
    SideEffect {
        systemUi.setStatusBarColor(
            color = color,
            darkIcons = !darkTheme
        )

    }
    SideEffect {
        systemUi.setNavigationBarColor(
            color = color,
            darkIcons = !darkTheme
        )
    }
}
