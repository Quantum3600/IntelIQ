package com.techbrains.inteliq.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techbrains.inteliq.presentation.theme.styles.ChipStyles

/**
 * Custom Neobrutalist Filter & Action Chip (`NeobrutalistChip.kt`).
 */
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun NeobrutalistChip(
    label: String,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    enabled: Boolean = true,
    shape: Shape? = null,
    containerColor: Color? = null,
    contentColor: Color? = null,
    leadingIcon: (@Composable () -> Unit)? = null,
    shadowClearance: Dp = 4.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = rememberUpdatedStyleState(interactionSource) {
        it.isEnabled = enabled
    }

    val colors = MaterialTheme.colorScheme
    val resolvedContentColor = contentColor ?: if (isSelected) colors.onPrimaryContainer else colors.onSurface
    val chipStyle = ChipStyles.chipStyle(isSelected = isSelected, containerColor = containerColor, shape = shape)

    Box(
        modifier = modifier.padding(end = shadowClearance, bottom = shadowClearance)
    ) {
        Row(
            modifier = Modifier
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled && onClick != null,
                    onClick = { onClick?.invoke() }
                )
                .styleable(
                    styleState = styleState,
                    style = chipStyle
                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompositionLocalProvider(LocalContentColor provides resolvedContentColor) {
                if (leadingIcon != null) {
                    leadingIcon()
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = resolvedContentColor
                )
            }
        }
    }
}
