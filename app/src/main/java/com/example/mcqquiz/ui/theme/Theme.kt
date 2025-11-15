package com.example.mcqquiz.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColorScheme(
    primary = DarkButton,
    background = DarkBackground,
    surface = DarkButton,
    onPrimary = TextWhite,
    onSecondary = TextWhite,
    onBackground = TextWhite,
    onSurface = TextWhite,
)

private val LightColorPalette = lightColorScheme(
    primary = LightButton,
    background = LightBackground,
    surface = LightButton,
    onPrimary = TextBlack,
    onSecondary = TextBlack,
    onBackground = TextBlack,
    onSurface = TextBlack,
)

@Composable
fun McqQuizTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
