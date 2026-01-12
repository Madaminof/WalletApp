package dev.samandar.walletapp.wallet.presentation.ui.otherScreens.settings.items.themes

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.stringResource
import androidx.core.content.edit
import dev.samandar.walletapp.R
import dev.samandar.walletapp.utils.Strings


object ThemeManager {
    private const val PREFS_NAME = "theme_prefs"
    private const val KEY_THEME = "current_theme"

    enum class ThemeOption(val label: Int, val nightMode: Int) {
        LIGHT(R.string.light, AppCompatDelegate.MODE_NIGHT_NO),
        DARK(R.string.dark, AppCompatDelegate.MODE_NIGHT_YES),
        SYSTEM(R.string.system, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
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