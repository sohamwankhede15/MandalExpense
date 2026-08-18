package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = OrangePrimaryDark,
    onPrimary = Color.White,
    primaryContainer = OrangeContainer,
    onPrimaryContainer = OrangePrimaryDark,
    secondary = OrangePrimary,
    onSecondary = Color.White,
    secondaryContainer = OrangeContainer,
    onSecondaryContainer = OrangePrimaryDark,
    tertiary = MarigoldSecondary,
    onTertiary = Color.White,
    background = OrangeBackground,
    onBackground = TextPrimary,
    surface = WhiteCard,
    onSurface = TextPrimary,
    surfaceVariant = OrangeContainer,
    onSurfaceVariant = TextSecondary,
    outline = OrangeBorder,
    error = RedExpense,
    onError = Color.White,
    errorContainer = RedContainer,
    onErrorContainer = RedExpense
)

@Composable
fun MandalAccountsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // We maintain a clean, crisp Light-Orange + White palette throughout the mandal app
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
