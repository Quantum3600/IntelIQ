package com.techbrains.inteliq.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.styleable
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techbrains.inteliq.presentation.theme.ComponentStyles
import com.techbrains.inteliq.presentation.theme.IntelIQTheme

/**
 * Neobrutalist Floating Navigation Bar for Mobile with animated expanding tabs, hard shadow & border
 */
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun FloatingNavigationBar(
    currentTab: ScreenDestination.Main,
    onTabSelected: (ScreenDestination.Main) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .wrapContentSize()
            .styleable(
                style = ComponentStyles.floatingNavBarStyle
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.wrapContentSize().clip(RoundedCornerShape(32.dp)),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            mainNavItems.forEach { item ->
                val isSelected = currentTab == item.destination
                val interactionSource = remember { MutableInteractionSource() }

                val containerColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
                    label = "ContainerColorAnimation",
                )
                val contentColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                    animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
                    label = "ContentColorAnimation",
                )
                val shadowElevation by animateDpAsState(
                    targetValue = if (isSelected) 4.dp else 0.dp,
                    animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
                    label = "ShadowElevationAnimation"
                )
                val borderColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else Color.Transparent,
                    animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
                    label = "BorderColorAnimation"
                )
                val borderWidth by animateDpAsState(
                    targetValue = if (isSelected) 2.dp else 0.dp,
                    animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
                    label = "BorderWidthAnimation"
                )

                val itemShape = remember(item.destination) {
                    when (item.destination) {
                        ScreenDestination.Main.Home -> RoundedCornerShape(
                            topStart = 32.dp,
                            bottomStart = 32.dp,
                            topEnd = 8.dp,
                            bottomEnd = 8.dp
                        )
                        ScreenDestination.Main.Profile -> RoundedCornerShape(
                            topStart = 8.dp,
                            bottomStart = 8.dp,
                            topEnd = 32.dp,
                            bottomEnd = 32.dp
                        )
                        else -> RoundedCornerShape(8.dp)
                    }
                }

                Box(
                    modifier = Modifier
                        .height(56.dp)
                        .then(
                            if (shadowElevation > 0.dp) Modifier.shadow(elevation = shadowElevation, shape = itemShape)
                            else Modifier
                        )
                        .clip(itemShape)
                        .background(containerColor)
                        .then(
                            if (borderWidth > 0.dp && borderColor != Color.Transparent) {
                                Modifier.border(width = borderWidth, color = borderColor, shape = itemShape)
                            } else Modifier
                        )
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = { onTabSelected(item.destination) }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = contentColor,
                            modifier = Modifier.size(24.dp)
                        )
                        AnimatedVisibility(
                            visible = isSelected,
                            enter = fadeIn(
                                animationSpec = tween(
                                    durationMillis = 250,
                                    delayMillis = 30,
                                    easing = FastOutSlowInEasing
                                )
                            ) + expandHorizontally(
                                expandFrom = Alignment.Start,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioNoBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            ),
                            exit = fadeOut(
                                animationSpec = tween(
                                    durationMillis = 150,
                                    easing = FastOutSlowInEasing
                                )
                            ) + shrinkHorizontally(
                                shrinkTowards = Alignment.Start,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioNoBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clipToBounds()
                            ) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = item.title,
                                    color = contentColor,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun FloatingNavigationBarPreview() {
    IntelIQTheme(
        darkTheme = false
    ) {
        FloatingNavigationBar(
            currentTab = ScreenDestination.Main.Home,
            onTabSelected = {}
        )
    }
}
