package com.techbrains.inteliq.presentation.theme.styles

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.border
import androidx.compose.foundation.style.contentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationStyleApi::class)
object ChipStyles {

    /** Neobrutalist Filter / Action Chip Style with customizable shape */
    @Composable
    fun chipStyle(
        isSelected: Boolean = false,
        containerColor: Color? = null,
        shapeCorner: Dp = 100.dp,
        shape: Shape? = null
    ): Style {
        val colors = MaterialTheme.colorScheme
        val typography = MaterialTheme.typography
        val shadowColor = colors.onBackground
        val normalShape = shape ?: RoundedCornerShape(shapeCorner)
        val pressedShape = RoundedCornerShape(8.dp)

        return Style {
            val fill = containerColor ?: if (isSelected) colors.primaryContainer else if (state.isEnabled) colors.surface else colors.surfaceVariant
            val shadowOffset = if (state.isPressed) DpOffset(1.dp, 1.dp) else DpOffset(2.5.dp, 2.5.dp)

            dropShadow(Shadow(color = shadowColor, offset = shadowOffset, radius = 0.dp))
            background(fill)
            shape(if (state.isPressed) pressedShape else normalShape)
            border(1.5.dp, shadowColor)
            textStyle(typography.labelMedium)
            contentPadding(horizontal = 12.dp, vertical = 6.dp)
        }
    }
}
