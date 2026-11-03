package com.techbrains.inteliq.presentation.theme.styles

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class ListItemPosition {
    Leading, Middle, Trailing, Single
}

@OptIn(ExperimentalFoundationStyleApi::class)
object CardStyles {

    /** Standalone Card / List Item Style */
    val listItemCardStyle: Style
        @Composable get() = cardStyle()

    /** Custom Card Style with customizable shape */
    @Composable
    fun cardStyle(shape: Shape? = null): Style {
        val colors = MaterialTheme.colorScheme
        val shapes = MaterialTheme.shapes
        val effectiveShape = shape ?: shapes.medium

        return Style {
            background(colors.surface)
            shape(effectiveShape)
            contentPadding(horizontal = 16.dp, vertical = 12.dp)
        }
    }

    /** Grouped List Item Style based on position */
    @Composable
    fun groupedListItemStyle(
        position: ListItemPosition,
        cornerSize: Dp = 16.dp,
        innerCornerSize: Dp = 2.dp
    ): Style {
        val colors = MaterialTheme.colorScheme
        val normalShape = when (position) {
            ListItemPosition.Leading -> RoundedCornerShape(
                topStart = cornerSize,
                topEnd = cornerSize,
                bottomStart = innerCornerSize,
                bottomEnd = innerCornerSize
            )
            ListItemPosition.Middle -> RectangleShape
            ListItemPosition.Trailing -> RoundedCornerShape(
                topStart = innerCornerSize,
                topEnd = innerCornerSize,
                bottomStart = cornerSize,
                bottomEnd = cornerSize
            )
            ListItemPosition.Single -> RoundedCornerShape(cornerSize)
        }
        val pressedShape = RoundedCornerShape(cornerSize)

        return Style {
            background(if (state.isEnabled) colors.surface else colors.surfaceVariant)
            shape(if (state.isPressed) pressedShape else normalShape)
            contentPadding(horizontal = 16.dp, vertical = 12.dp)
        }
    }
}
