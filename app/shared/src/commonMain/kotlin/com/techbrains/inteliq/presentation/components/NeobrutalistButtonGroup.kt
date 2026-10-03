package com.techbrains.inteliq.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techbrains.inteliq.presentation.theme.styles.ButtonGroupPosition
import com.techbrains.inteliq.presentation.theme.styles.ButtonStyles

/**
 * Custom Neobrutalist Horizontal Segmented Button Group (`NeobrutalistButtonGroup.kt`).
 */
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun NeobrutalistButtonGroup(
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shadowClearance: Dp = 5.dp
) {
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = modifier.padding(end = shadowClearance, bottom = shadowClearance)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            options.forEachIndexed { index, option ->
                val isSelected = index == selectedIndex
                val position = when {
                    options.size == 1 -> ButtonGroupPosition.Single
                    index == 0 -> ButtonGroupPosition.Start
                    index == options.size - 1 -> ButtonGroupPosition.End
                    else -> ButtonGroupPosition.Middle
                }

                val interactionSource = remember { MutableInteractionSource() }
                val styleState = rememberUpdatedStyleState(interactionSource) {
                    it.isEnabled = enabled
                }

                val segmentStyle = ButtonStyles.buttonGroupStyle(
                    position = position,
                    isSelected = isSelected
                )

                val contentColor = if (isSelected) colors.onPrimaryContainer else colors.onSurface

                Box(
                    modifier = Modifier
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            enabled = enabled,
                            onClick = { onOptionSelected(index) }
                        )
                        .styleable(
                            styleState = styleState,
                            style = segmentStyle
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    CompositionLocalProvider(LocalContentColor provides contentColor) {
                        Text(
                            text = option,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = contentColor
                        )
                    }
                }
            }
        }
    }
}
