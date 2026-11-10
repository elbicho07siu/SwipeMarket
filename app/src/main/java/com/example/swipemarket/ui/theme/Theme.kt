package com.example.swipemarket.ui.theme

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

private val LightColorScheme = lightColorScheme(
    primary = NavyBlue,
    onPrimary = Color.White,
    primaryContainer = AccentBlueContainer,
    onPrimaryContainer = NavyBlue,
    secondary = AccentBlue,
    onSecondary = Color.White,
    secondaryContainer = AccentBlueContainer,
    onSecondaryContainer = NavyBlue,
    tertiary = ValidGreen,
    onTertiary = Color.White,
    tertiaryContainer = ValidGreenContainer,
    onTertiaryContainer = Color(0xFF06371B),
    error = ErrorRed,
    onError = Color.White,
    errorContainer = ErrorContainer,
    onErrorContainer = Color(0xFF410002),
    background = AppBackground,
    onBackground = NavyBlue,
    surface = AppSurface,
    onSurface = NavyBlue,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = MutedText,
    outline = OutlineLight,
    outlineVariant = Color(0xFFE1E7F0),
    scrim = NavyBlue
)

private val DarkColorScheme = darkColorScheme(
    primary = AccentBlueLight,
    onPrimary = NavyBlue,
    primaryContainer = AccentBlue,
    onPrimaryContainer = Color(0xFFD8E5F8),
    secondary = AccentBlueLight,
    onSecondary = NavyBlue,
    secondaryContainer = Color(0xFF173A66),
    onSecondaryContainer = Color(0xFFD8E5F8),
    tertiary = Color(0xFF7CDDA5),
    onTertiary = Color(0xFF00391C),
    tertiaryContainer = Color(0xFF0B5A30),
    onTertiaryContainer = ValidGreenContainer,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = ErrorContainer,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = Color(0xFF2B3853),
    scrim = Color.Black
)

/**
 * Tema base de SwipeMarket.
 *
 * El color dinámico está desactivado por defecto para garantizar que la marca
 * mantenga siempre su paleta oficial en todos los dispositivos.
 */
@Composable
fun SwipeMarketTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
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
