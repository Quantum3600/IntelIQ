package com.techbrains.inteliq.presentation.theme

import androidx.compose.ui.graphics.Color
import com.materialkolor.PaletteStyle

enum class DarkThemeMode(val displayName: String) {
    SYSTEM("System Default"),
    LIGHT("Light Mode"),
    DARK("Dark Mode")
}

data class SeedColorOption(
    val name: String,
    val color: Color
)

val PresetSeedColors = listOf(
    SeedColorOption("Emerald Green", Color(0xFF10B981)),
    SeedColorOption("Signal Indigo", Color(0xFF4C5FD5)),
    SeedColorOption("Flame Orange", Color(0xFFFF6B4A)),
    SeedColorOption("Royal Purple", Color(0xFF8B5CF6)),
    SeedColorOption("Ocean Cyan", Color(0xFF0EA5E9)),
    SeedColorOption("Amber Gold", Color(0xFFF59E0B)),
    SeedColorOption("Rose Pink", Color(0xFFEC4899))
)

data class ThemeState(
    val seedColor: Color = Color(0xFF10B981),
    val darkThemeMode: DarkThemeMode = DarkThemeMode.SYSTEM,
    val paletteStyle: PaletteStyle = PaletteStyle.Vibrant
)

/**
 * Formats a Compose [Color] into a hex string `#RRGGBB`.
 */
fun Color.toHex(): String {
    val r = (red * 255).toInt().coerceIn(0, 255).toString(16).padStart(2, '0').uppercase()
    val g = (green * 255).toInt().coerceIn(0, 255).toString(16).padStart(2, '0').uppercase()
    val b = (blue * 255).toInt().coerceIn(0, 255).toString(16).padStart(2, '0').uppercase()
    return "#$r$g$b"
}

/**
 * Parses a hex string `#RRGGBB` or `RRGGBB` or `#AARRGGBB` into a Compose [Color].
 * Returns null if the format is invalid.
 */
fun parseHexColor(hex: String): Color? {
    val cleanHex = hex.trim().removePrefix("#")
    return try {
        when (cleanHex.length) {
            6 -> {
                val colorInt = cleanHex.toLong(16)
                Color(0xFF000000 or colorInt)
            }
            8 -> {
                val colorInt = cleanHex.toLong(16)
                Color(colorInt)
            }
            else -> null
        }
    } catch (_: Exception) {
        null
    }
}
