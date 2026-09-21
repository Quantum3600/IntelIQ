package com.techbrains.inteliq.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Ink = Color(0xFF171B2E)
val Paper = Color(0xFFEEF0F5)
val PaperRaised = Color(0xFFFFFFFF)
val Signal = Color(0xFF4C5FD5)
val SignalDim = Color(0xFFDDE1F7)
val Flame = Color(0xFFFF6B4A)

// Mastery Scale Colors (DESIGN.md Section 2.1)
val MasteryClay = Color(0xFFD95550)      // Needs Attention (0.00-0.30)
val MasteryAmber = Color(0xFFE1943D)     // Developing (0.30-0.50)
val MasteryOchre = Color(0xFFD9B23C)     // Intermediate (0.50-0.70)
val MasteryMoss = Color(0xFF4C9F70)      // Strong (0.70-0.85)
val MasteryPine = Color(0xFF1F6F54)      // Mastered (0.85-1.00)

// Dark Theme Colors
val DarkBackground = Color(0xFF0F1220)
val DarkText = Color(0xFFEAEBF2)
val DarkSignal = Color(0xFF7C8AEF)

private val LightColorScheme = lightColorScheme(
    primary = Signal,
    onPrimary = PaperRaised,
    primaryContainer = SignalDim,
    onPrimaryContainer = Ink,
    secondary = Flame,
    background = Paper,
    onBackground = Ink,
    surface = PaperRaised,
    onSurface = Ink,
    surfaceVariant = SignalDim,
    onSurfaceVariant = Ink
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkSignal,
    onPrimary = DarkBackground,
    primaryContainer = Signal,
    onPrimaryContainer = DarkText,
    secondary = Flame,
    background = DarkBackground,
    onBackground = DarkText,
    surface = Color(0xFF1B1E30),
    onSurface = DarkText,
    surfaceVariant = Color(0xFF252940),
    onSurfaceVariant = DarkText
)

@Composable
fun IntelIQTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
