package com.techbrains.inteliq.presentation.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.styleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.techbrains.inteliq.presentation.theme.styles.SwitchStyles

/**
 * Custom Neobrutalist Toggle Switch (`NeobrutalistSwitch.kt`).
 */
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun NeobrutalistSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shadowClearance: Dp = 4.dp
) {
    val interactionSource = remember { MutableInteractionSource() }

    val thumbOffset by animateDpAsState(
        targetValue = if (checked) 24.dp else 2.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "NeobrutalistSwitchThumbOffset"
    )

    Box(
        modifier = modifier.padding(end = shadowClearance, bottom = shadowClearance)
    ) {
        Box(
            modifier = Modifier
                .width(52.dp)
                .height(28.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    onClick = { onCheckedChange?.invoke(!checked) }
                )
                .styleable(style = SwitchStyles.switchTrackStyle(checked)),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .offset(x = thumbOffset)
                    .size(20.dp)
                    .styleable(style = SwitchStyles.switchThumbStyle(checked))
            )
        }
    }
}
