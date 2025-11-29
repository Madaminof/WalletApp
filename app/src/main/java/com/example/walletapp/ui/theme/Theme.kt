package com.example.walletapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4759C1),
    primaryContainer = Color(0xFF212A34), // topbarContainer va background color default(0xFF1E262F)
    onPrimaryContainer = Color(0xFF1B2228),// cardContainer  0xFF212A34
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = Color(0xFF212A34),
    onBackground = Color(0xFF171C23),
    surface = Color(0xFF1A1A1A),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onSurface = Color(0xFF1C1B1F),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4759C1),
    primaryContainer = Color(0xFFE6EAEE),// topbarContainer va background color 0xFFECF3F6, 0xFFE3EDF1
    onPrimaryContainer = Color(0xFFFDFDFD),// cardContainer
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Color(0xFFE6EAEE),
    onBackground = Color(0xFFDBE1E2),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color(0xFF1A1A1A),
    onSurface = Color(0xFF2C2A31),
)

@Composable
fun WalletAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }


    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}