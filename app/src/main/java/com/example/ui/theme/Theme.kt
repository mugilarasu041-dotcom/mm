package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = White,
    onPrimary = Black100,
    primaryContainer = Black80,
    onPrimaryContainer = White,
    secondary = LightGray,
    onSecondary = Black100,
    background = Black90,
    onBackground = White,
    surface = Black80,
    onSurface = White,
    surfaceVariant = DarkGray,
    onSurfaceVariant = LightGray,
    outline = LightGray,
    outlineVariant = DarkGray
)

private val LightColorScheme = lightColorScheme(
    primary = Black100,
    onPrimary = White,
    primaryContainer = OffWhite,
    onPrimaryContainer = Black100,
    secondary = DarkGray,
    onSecondary = White,
    background = White,
    onBackground = Black100,
    surface = White,
    onSurface = Black100,
    surfaceVariant = OffWhite,
    onSurfaceVariant = Black80,
    outline = Black100,
    outlineVariant = LightGray
)

@Composable
fun WinterArcTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
