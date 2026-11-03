package com.techbrains.inteliq.presentation.theme.styles

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class ButtonGroupPosition {
    Start, Middle, End, Single
}

@OptIn(ExperimentalFoundationStyleApi::class)
object ButtonStyles {

    /** Neobrutalist Primary Action Button Style with smooth animated parameters or custom shape */
    @Composable
    fun primaryButtonStyle(
        cornerRadius: Dp = 24.dp,
        shape: Shape? = null,
        shadowOffsetDp: Dp = 4.dp
    ): Style {
        val colors = MaterialTheme.colorScheme
        val typography = MaterialTheme.typography
        val effectiveShape = shape ?: RoundedCornerShape(cornerRadius)

        return Style {
            background(if (state.isEnabled) colors.primary else colors.surfaceVariant)
            shape(effectiveShape)
            textStyle(typography.labelLarge)
            contentPadding(horizontal = 16.dp, vertical = 12.dp)
        }
    }

    /** Primary button style property for default static access */
    val primaryButtonStyle: Style
        @Composable get() = primaryButtonStyle()

    /** Debug Backend Bypass Button Style with smooth animated parameters or custom shape */
    @Composable
    fun debugButtonStyle(
        cornerRadius: Dp = 24.dp,
        shape: Shape? = null,
        shadowOffsetDp: Dp = 3.dp
    ): Style {
        val colors = MaterialTheme.colorScheme
        val typography = MaterialTheme.typography
        val effectiveShape = shape ?: RoundedCornerShape(cornerRadius)

        return Style {
            background(if (state.isEnabled) colors.secondary else colors.surfaceVariant)
            shape(effectiveShape)
            textStyle(typography.labelMedium)
            contentPadding(horizontal = 16.dp, vertical = 10.dp)
        }
    }

    /** Debug button style property for default static access */
    val debugButtonStyle: Style
        @Composable get() = debugButtonStyle()

    /** Neobrutalist Button Group Segment Style with smooth animated parameters */
    @Composable
    fun buttonGroupStyle(
        position: ButtonGroupPosition,
        isSelected: Boolean = false,
        startCorner: Dp = when (position) {
            ButtonGroupPosition.Start, ButtonGroupPosition.Single -> 100.dp
            else -> 0.dp
        },
        endCorner: Dp = when (position) {
            ButtonGroupPosition.End, ButtonGroupPosition.Single -> 100.dp
            else -> 0.dp
        },
        shadowOffsetDp: Dp = 3.dp
    ): Style {
        val colors = MaterialTheme.colorScheme
        val typography = MaterialTheme.typography

        val segmentShape = RoundedCornerShape(
            topStart = startCorner,
            bottomStart = startCorner,
            topEnd = endCorner,
            bottomEnd = endCorner
        )

        return Style {
            background(if (isSelected) colors.primaryContainer else if (state.isEnabled) colors.surface else colors.surfaceVariant)
            shape(segmentShape)
            textStyle(typography.labelLarge)
            contentPadding(horizontal = 16.dp, vertical = 10.dp)
        }
    }
}
