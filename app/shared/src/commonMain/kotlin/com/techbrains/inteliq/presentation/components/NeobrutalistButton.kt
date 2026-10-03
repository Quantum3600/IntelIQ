package com.techbrains.inteliq.presentation.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.techbrains.inteliq.presentation.theme.styles.ButtonStyles

/**
 * Button variants mapping to standard Material 3 color schemes & contrast roles.
 */
enum class NeobrutalistButtonVariant {
    Primary,
    Secondary,
    Surface
}

/**
 * Custom Neobrutalist Button Component (`NeobrutalistButton.kt`).
 *
 * Key features:
 * - Customizable [Shape] (defaults to animated spring corner morphing if null).
 * - Buttery smooth spring corner morphing & tactile shadow press animation.
 * - Fills parent width/height when passed layout modifiers (`Modifier.fillMaxWidth()`).
 * - Allocated shadow clearance space so hard offset dropShadows are NEVER clipped by parent layouts.
 * - Dynamic contrast guarantee: automatically provides [LocalContentColor] matching the variant's `onColor`.
 */
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun NeobrutalistButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape? = null,
    variant: NeobrutalistButtonVariant = NeobrutalistButtonVariant.Primary,
    shadowClearance: Dp = 6.dp,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val styleState = rememberUpdatedStyleState(interactionSource) {
        it.isEnabled = enabled
    }

    val animatedCornerRadius by animateDpAsState(
        targetValue = if (isPressed) 8.dp else 24.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "NeobrutalistButtonCornerRadius"
    )

    val animatedShadowOffset by animateDpAsState(
        targetValue = if (isPressed) 1.dp else 4.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "NeobrutalistButtonShadowOffset"
    )

    val buttonStyle = when (variant) {
        NeobrutalistButtonVariant.Primary -> ButtonStyles.primaryButtonStyle(
            cornerRadius = animatedCornerRadius,
            shape = shape,
            shadowOffsetDp = animatedShadowOffset
        )
        NeobrutalistButtonVariant.Secondary -> ButtonStyles.debugButtonStyle(
            cornerRadius = animatedCornerRadius,
            shape = shape,
            shadowOffsetDp = animatedShadowOffset
        )
        NeobrutalistButtonVariant.Surface -> ButtonStyles.primaryButtonStyle(
            cornerRadius = animatedCornerRadius,
            shape = shape,
            shadowOffsetDp = animatedShadowOffset
        )
    }

    val colors = MaterialTheme.colorScheme
    val contentColor: Color = when (variant) {
        NeobrutalistButtonVariant.Primary -> if (enabled) colors.onPrimary else colors.onSurfaceVariant
        NeobrutalistButtonVariant.Secondary -> if (enabled) colors.onSecondary else colors.onSurfaceVariant
        NeobrutalistButtonVariant.Surface -> if (enabled) colors.onSurface else colors.onSurfaceVariant
    }

    // Outer padding provides dedicated clearance space so the +4.dp dropShadow is never clipped
    Box(
        modifier = modifier.padding(end = shadowClearance, bottom = shadowClearance),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    onClick = onClick
                )
                .styleable(
                    styleState = styleState,
                    style = buttonStyle
                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompositionLocalProvider(
                LocalContentColor provides contentColor,
                LocalTextStyle provides MaterialTheme.typography.labelLarge.copy(color = contentColor)
            ) {
                content()
            }
        }
    }
}
