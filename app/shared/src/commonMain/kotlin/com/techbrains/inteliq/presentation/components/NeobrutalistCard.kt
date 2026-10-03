package com.techbrains.inteliq.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.techbrains.inteliq.presentation.theme.styles.CardStyles

/**
 * Custom Neobrutalist Card Component (`NeobrutalistCard.kt`).
 */
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun NeobrutalistCard(
    modifier: Modifier = Modifier,
    shape: Shape? = null,
    style: Style? = null,
    shadowClearance: Dp = 6.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val effectiveStyle = style ?: CardStyles.cardStyle(shape = shape)

    Box(
        modifier = modifier.padding(end = shadowClearance, bottom = shadowClearance)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .styleable(style = effectiveStyle)
        ) {
            CompositionLocalProvider(LocalContentColor provides colors.onSurface) {
                content()
            }
        }
    }
}
