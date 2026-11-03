package com.techbrains.inteliq.presentation.theme.styles

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@OptIn(ExperimentalFoundationStyleApi::class)
object SwitchStyles {

    /** Switch Track Style */
    @Composable
    fun switchTrackStyle(isChecked: Boolean): Style {
        val colors = MaterialTheme.colorScheme
        val shape = CircleShape

        return Style {
            background(if (isChecked) colors.primaryContainer else colors.surfaceVariant)
            shape(shape)
        }
    }

    /** Switch Thumb Style */
    @Composable
    fun switchThumbStyle(isChecked: Boolean): Style {
        val colors = MaterialTheme.colorScheme
        val shape = CircleShape

        return Style {
            background(if (isChecked) colors.primary else colors.surface)
            shape(shape)
        }
    }
}
