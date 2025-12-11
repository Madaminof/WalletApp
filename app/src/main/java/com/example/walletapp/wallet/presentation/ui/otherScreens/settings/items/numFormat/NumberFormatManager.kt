package com.example.walletapp.wallet.presentation.ui.otherScreens.settings.items.numFormat

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import java.util.Locale

object NumberFormatManager {
    private const val PREFS_NAME = "wallet_app_settings"
    private const val KEY_FORMAT_STYLE = "selected_number_format_style"

    enum class FormatStyle(val localeCode: String, val example: String, val title: String) {
        SPACE_DOT("fr_FR", "1 000 000", "Space Separator"),

        COMMA_DOT("en_US", "1,000,000", "Comma Separator"),

        DOT_COMMA("de_DE", "1.000.000", "Dot Separator")
    }

    private val _currentStyle = mutableStateOf(FormatStyle.SPACE_DOT) // Default: US Style
    val currentStyle: State<FormatStyle> = _currentStyle

    fun initialize(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedCode = prefs.getString(KEY_FORMAT_STYLE, FormatStyle.COMMA_DOT.localeCode)

        _currentStyle.value = FormatStyle.entries.find { it.localeCode == savedCode } ?: FormatStyle.COMMA_DOT
    }

    fun saveStyle(context: Context, newStyle: FormatStyle) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_FORMAT_STYLE, newStyle.localeCode).apply()

        _currentStyle.value = newStyle
    }

    /**
     * Joriy FormatStyle ga asoslangan Locale ni qaytaradi.
     */
    fun getCurrentLocale(): Locale {
        return when (_currentStyle.value) {
            FormatStyle.SPACE_DOT -> Locale.FRANCE
            FormatStyle.COMMA_DOT -> Locale.US
            FormatStyle.DOT_COMMA -> Locale.GERMANY
        }
    }
}