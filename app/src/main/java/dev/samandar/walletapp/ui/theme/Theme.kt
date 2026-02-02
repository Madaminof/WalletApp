package dev.samandar.walletapp.ui.theme

import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4759C1),
    primaryContainer = Color(0xFF161B1F), // topbarContainer va background color default(0xFF1E262F)
    onPrimaryContainer = Color(0xFF1B2228),// cardContainer  0xFF212A34
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = Color(0xFF161B1F),
    onBackground = Color(0xFF171C23),
    surface = Color(0xFF1A1A1A),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onSurface = Color(0xFF1C1B1F),
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4759C1),
    primaryContainer = Color(0xFFF2F2F5),// topbarContainer va background color 0xFFECF3F6, 0xFFE3EDF1
    onPrimaryContainer = Color(0xFFFCFCFC),// cardContainer
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Color(0xFFF2F2F5),
    onBackground = Color(0xFFDBE1E2),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color(0xFF1A1A1A),
    onSurface = Color(0xFF2C2A31),
)


@Composable
fun isNightModeActive(): Boolean {
    val configuration = LocalContext.current.resources.configuration
    val uiMode = remember(configuration) { configuration.uiMode }
    val isSystemDark =
        uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

    return when (AppCompatDelegate.getDefaultNightMode()) {
        AppCompatDelegate.MODE_NIGHT_YES -> true
        AppCompatDelegate.MODE_NIGHT_NO -> false
        else -> isSystemDark
    }
}


@Composable
fun WalletAppTheme(
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val darkTheme = isNightModeActive()

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val systemUiController = rememberSystemUiController()
    val statusAndNavBarColor = colorScheme.primaryContainer

    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusAndNavBarColor,
            darkIcons = !darkTheme
        )
        systemUiController.setNavigationBarColor(
            color = statusAndNavBarColor,
            darkIcons = !darkTheme
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}