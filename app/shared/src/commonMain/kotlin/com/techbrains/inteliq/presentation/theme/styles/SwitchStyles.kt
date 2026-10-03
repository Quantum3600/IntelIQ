package com.techbrains.inteliq.presentation.theme.styles

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.border
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationStyleApi::class)
object SwitchStyles {

    /** Neobrutalist Switch Track Style */
    @Composable
    fun switchTrackStyle(isChecked: Boolean): Style {
        val colors = MaterialTheme.colorScheme
        val shadowColor = colors.onBackground
        val shape = CircleShape

        return Style {
            dropShadow(Shadow(color = shadowColor, offset = DpOffset(2.dp, 2.dp), radius = 0.dp))
            background(if (isChecked) colors.primaryContainer else colors.surfaceVariant)
            shape(shape)
            border(2.dp, shadowColor)
        }
    }

    /** Neobrutalist Switch Thumb Style */
    @Composable
    fun switchThumbStyle(isChecked: Boolean): Style {
        val colors = MaterialTheme.colorScheme
        val shadowColor = colors.onBackground
        val shape = CircleShape

        return Style {
            background(if (isChecked) colors.primary else colors.surface)
            shape(shape)
            border(1.5.dp, shadowColor)
        }
    }
}
