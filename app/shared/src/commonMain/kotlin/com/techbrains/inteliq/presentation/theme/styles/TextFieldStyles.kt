package com.techbrains.inteliq.presentation.theme.styles

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.border
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationStyleApi::class)
object TextFieldStyles {

    /** Neobrutalist Text Field Style with focused border shift and hard offset drop shadow */
    val textFieldStyle: Style
        @Composable get() = textFieldStyle(shape = RoundedCornerShape(12.dp))

    @Composable
    fun textFieldStyle(shape: Shape = RoundedCornerShape(12.dp)): Style {
        val colors = MaterialTheme.colorScheme
        val shadowColor = colors.onBackground

        return Style {
            val shadowOffset = if (state.isFocused) DpOffset(4.dp, 4.dp) else DpOffset(3.dp, 3.dp)
            val borderColor = if (state.isFocused) colors.primary else colors.onSurface

            dropShadow(Shadow(color = shadowColor, offset = shadowOffset, radius = 0.dp))
            background(colors.surface)
            shape(shape)
            border(if (state.isFocused) 2.5.dp else 2.dp, borderColor)
        }
    }
}
