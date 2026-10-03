package com.techbrains.inteliq.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.materialkolor.DynamicMaterialTheme
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicMaterialThemeState

// Default brand / seed color
val DefaultSeedColor = Color(0xFF10B981) // Green seed color per DESIGN.md §2.1

// Static Fallback Color Tokens
val Ink = Color(0xFF171B2E)
val Paper = Color(0xFFEEF0F5)
val PaperRaised = Color(0xFFFFFFFF)
val Signal = Color(0xFF10B981)
val SignalDim = Color(0xFFD1FAE5)
val Flame = Color(0xFFFF6B4A)

// Mastery Scale Colors (DESIGN.md Section 2.1) - Fixed functional thresholds
val MasteryClay = Color(0xFFD95550)      // Needs Attention (0.00-0.30)
val MasteryAmber = Color(0xFFE1943D)     // Developing (0.30-0.50)
val MasteryOchre = Color(0xFFD9B23C)     // Intermediate (0.50-0.70)
val MasteryMoss = Color(0xFF4C9F70)      // Strong (0.70-0.85)
val MasteryPine = Color(0xFF1F6F54)      // Mastered (0.85-1.00)

@Composable
fun IntelIQTheme(
    seedColor: Color = DefaultSeedColor,
    darkTheme: Boolean = isSystemInDarkTheme(),
    style: PaletteStyle = PaletteStyle.Vibrant,
    content: @Composable () -> Unit
) {
    val dynamicThemeState = rememberDynamicMaterialThemeState(
        seedColor = seedColor,
        isDark = darkTheme,
        style = style,
        specVersion = ColorSpec.SpecVersion.SPEC_2025
    )

    val typography = appTypography()

    DynamicMaterialTheme(
        state = dynamicThemeState,
        typography = typography,
        animate = true
    ) {
        CompositionLocalProvider(
            LocalTextStyle provides typography.bodyMedium,
            content = content
        )
    }
}
