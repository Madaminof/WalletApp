package com.example.walletapp.wallet.presentation.ui.otherScreens.settings.items.themes

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.edit

object ThemeManager {
    private const val PREFS_NAME = "theme_prefs"
    private const val KEY_THEME = "current_theme"

    enum class ThemeOption(val label: String, val nightMode: Int) {
        LIGHT("Light", AppCompatDelegate.MODE_NIGHT_NO),
        DARK("Dark", AppCompatDelegate.MODE_NIGHT_YES),
        SYSTEM("System", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
    val isThemeChanged = mutableStateOf(false)

    fun applyTheme(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedThemeName = prefs.getString(KEY_THEME, ThemeOption.SYSTEM.name) ?: ThemeOption.SYSTEM.name
        val theme = ThemeOption.entries.find { it.name == savedThemeName } ?: ThemeOption.SYSTEM

        AppCompatDelegate.setDefaultNightMode(theme.nightMode)
    }

    fun saveAndApplyTheme(context: Context, theme: ThemeOption) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putString(KEY_THEME, theme.name)
        }

        AppCompatDelegate.setDefaultNightMode(theme.nightMode)

        isThemeChanged.value = !isThemeChanged.value
    }

    fun getCurrentThemeOption(context: Context): ThemeOption {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedThemeName = prefs.getString(KEY_THEME, ThemeOption.SYSTEM.name) ?: ThemeOption.SYSTEM.name
        return ThemeOption.entries.find { it.name == savedThemeName } ?: ThemeOption.SYSTEM
    }
}