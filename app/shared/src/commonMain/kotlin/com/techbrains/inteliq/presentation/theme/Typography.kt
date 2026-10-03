package com.techbrains.inteliq.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import inteliq.app.shared.generated.resources.Res
import inteliq.app.shared.generated.resources.fredoka_variable_font_wdth_wght
import inteliq.app.shared.generated.resources.momo_trust_display_regular
import org.jetbrains.compose.resources.Font

@Composable
fun momoFontFamily(): FontFamily {
    return FontFamily(
        Font(
            resource = Res.font.momo_trust_display_regular,
            weight = FontWeight.Normal,
            style = FontStyle.Normal
        ),
        Font(
            resource = Res.font.momo_trust_display_regular,
            weight = FontWeight.Bold,
            style = FontStyle.Normal
        )
    )
}

@Composable
fun fredokaFontFamily(): FontFamily {
    return FontFamily(
        Font(
            resource = Res.font.fredoka_variable_font_wdth_wght,
            weight = FontWeight.Normal,
            style = FontStyle.Normal
        ),
        Font(
            resource = Res.font.fredoka_variable_font_wdth_wght,
            weight = FontWeight.Medium,
            style = FontStyle.Normal
        ),
        Font(
            resource = Res.font.fredoka_variable_font_wdth_wght,
            weight = FontWeight.SemiBold,
            style = FontStyle.Normal
        ),
        Font(
            resource = Res.font.fredoka_variable_font_wdth_wght,
            weight = FontWeight.Bold,
            style = FontStyle.Normal
        )
    )
}

@Composable
fun fredokaDisplayFontFamily(): FontFamily {
    return FontFamily(
        Font(
            resource = Res.font.fredoka_variable_font_wdth_wght,
            weight = FontWeight.Bold,
            style = FontStyle.Normal,
            variationSettings = FontVariation.Settings(
                FontVariation.width(125f),
                FontVariation.weight(700)
            )
        )
    )
}

@Composable
fun appTypography(): Typography {
    val momo = momoFontFamily()
    val fredoka = fredokaFontFamily()
    val fredokaDisplay = fredokaDisplayFontFamily()

    val defaultTypography = Typography()

    return Typography(
        // Display styles -> Fredoka Bold & Wide (width axis)
        displayLarge = defaultTypography.displayLarge.copy(
            fontFamily = fredokaDisplay,
            fontWeight = FontWeight.Bold
        ),
        displayMedium = defaultTypography.displayMedium.copy(
            fontFamily = fredokaDisplay,
            fontWeight = FontWeight.Bold
        ),
        displaySmall = defaultTypography.displaySmall.copy(
            fontFamily = fredokaDisplay,
            fontWeight = FontWeight.Bold
        ),

        // Headline & Title styles (Heading) -> Momo
        headlineLarge = defaultTypography.headlineLarge.copy(
            fontFamily = momo
        ),
        headlineMedium = defaultTypography.headlineMedium.copy(
            fontFamily = momo
        ),
        headlineSmall = defaultTypography.headlineSmall.copy(
            fontFamily = momo
        ),
        titleLarge = defaultTypography.titleLarge.copy(
            fontFamily = momo
        ),
        titleMedium = defaultTypography.titleMedium.copy(
            fontFamily = momo
        ),
        titleSmall = defaultTypography.titleSmall.copy(
            fontFamily = momo
        ),

        // Body styles -> Fredoka
        bodyLarge = defaultTypography.bodyLarge.copy(
            fontFamily = fredoka
        ),
        bodyMedium = defaultTypography.bodyMedium.copy(
            fontFamily = fredoka
        ),
        bodySmall = defaultTypography.bodySmall.copy(
            fontFamily = fredoka
        ),

        // Label styles -> Fredoka
        labelLarge = defaultTypography.labelLarge.copy(
            fontFamily = fredoka
        ),
        labelMedium = defaultTypography.labelMedium.copy(
            fontFamily = fredoka
        ),
        labelSmall = defaultTypography.labelSmall.copy(
            fontFamily = fredoka
        )
    )
}
