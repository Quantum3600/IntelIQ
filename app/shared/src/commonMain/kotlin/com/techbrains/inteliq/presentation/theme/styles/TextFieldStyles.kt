package com.techbrains.inteliq.presentation.theme.styles

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationStyleApi::class)
object TextFieldStyles {

    /** Text Field Style */
    val textFieldStyle: Style
        @Composable get() = textFieldStyle(shape = RoundedCornerShape(12.dp))

    @Composable
    fun textFieldStyle(shape: Shape = RoundedCornerShape(12.dp)): Style {
        val colors = MaterialTheme.colorScheme

        return Style {
            background(colors.surface)
            shape(shape)
        }
    }
}
