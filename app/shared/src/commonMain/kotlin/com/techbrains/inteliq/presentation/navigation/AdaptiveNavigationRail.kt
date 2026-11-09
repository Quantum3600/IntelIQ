package com.techbrains.inteliq.presentation.navigation

import com.techbrains.inteliq.IqLogo

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.styleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techbrains.inteliq.presentation.theme.ComponentStyles
import com.techbrains.inteliq.presentation.theme.DarkThemeMode
import com.techbrains.inteliq.presentation.theme.IntelIQTheme
import com.techbrains.inteliq.presentation.theme.LocalHazeState
import com.techbrains.inteliq.presentation.theme.styles.NavLayoutType
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.glass.GlassStyle
import dev.chrisbanes.haze.glass.hazeGlass
import dev.chrisbanes.haze.glass.material3.material3
import io.github.quantum3600.stardust.stardustMeshAnimation

/**
 * Adaptive Navigation Rail for Tablet
 */
@OptIn(ExperimentalFoundationStyleApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun AdaptiveNavigationRail(
    currentTab: ScreenDestination.Main,
    onTabSelected: (ScreenDestination.Main) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    isDarkThemeMode: DarkThemeMode = DarkThemeMode.LIGHT,
    hazeState: HazeState? = LocalHazeState.current,
    onExpand: (() -> Unit)? = null,
    sharedTransitionScope: SharedTransitionScope? = LocalSharedTransitionScope.current,
    animatedVisibilityScope: AnimatedVisibilityScope? = LocalNavAnimatedVisibilityScope.current
) {
    val activeHaze = hazeState ?: LocalHazeState.current
    val railModifier = if (activeHaze != null) {
        modifier.hazeGlass(
            input = HazeInput.Backdrop(activeHaze),
            style = GlassStyle.material3()
        )
    } else modifier

    Column(
        modifier = railModifier
            .padding(8.dp)
            .statusBarsPadding()
            .navigationBarsPadding()
            .fillMaxHeight()
            .width(84.dp)
            .styleable(
                style = ComponentStyles.navRailStyle
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top: Expand Button & App Logo
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (onExpand != null) {
                val toggleModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                    with(sharedTransitionScope) {
                        Modifier.sharedBounds(
                            sharedContentState = rememberSharedContentState(key = "nav_toggle_button"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                    }
                } else Modifier

                IconButton(
                    onClick = onExpand,
                    modifier = Modifier
                        .then(toggleModifier)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Expand to Navigation Drawer",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            IqLogo(
                modifier = Modifier.size(44.dp),
                isDarkTheme = isDarkThemeMode == DarkThemeMode.DARK
            )
            Spacer(modifier = Modifier.height(if (onExpand != null) 36.dp else 56.dp))

            // Navigation Items (excluding Profile which is anchored at bottom)
            mainNavItems
                .filter { it.destination != ScreenDestination.Main.Profile }
                .forEach { item ->
                    val isSelected = currentTab == item.destination
                    val interactionSource = remember { MutableInteractionSource() }
                    val itemShape = ComponentStyles.rememberAnimatedNavItemShape(
                        destination = item.destination,
                        isSelected = isSelected,
                        layoutType = NavLayoutType.RAIL
                    )
                    val backgroundModifier = if (isSelected) {
                        Modifier.clip(itemShape)
                            .stardustMeshAnimation(
                                2, 2, colors = listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary,
                                    MaterialTheme.colorScheme.tertiary
                                ), 0.8f, 0.2f, 32
                            )
                    } else Modifier.clip(itemShape).background(MaterialTheme.colorScheme.surfaceVariant)
                    val navItemStyle = if (isSelected) {
                        ComponentStyles.selectedNavItemStyle(shape = itemShape)
                    } else {
                        ComponentStyles.unselectedNavItemStyle(shape = itemShape)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp, horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = backgroundModifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null,
                                    onClick = { onTabSelected(item.destination) }
                                )
                                .styleable(style = navItemStyle)
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val iconModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                                with(sharedTransitionScope) {
                                    Modifier.sharedElement(
                                        sharedContentState = rememberSharedContentState(key = "nav_item_icon_${item.title}"),
                                        animatedVisibilityScope = animatedVisibilityScope
                                    )
                                }
                            } else Modifier

                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .then(iconModifier)
                                    .size(22.dp)
                            )
                        }
                    }
                }
        }

        // Bottom: Profile Avatar Menu Button
        val isProfileSelected = currentTab == ScreenDestination.Main.Profile
        val avatarModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
            with(sharedTransitionScope) {
                Modifier.sharedElement(
                    sharedContentState = rememberSharedContentState(key = "nav_profile_avatar"),
                    animatedVisibilityScope = animatedVisibilityScope
                )
            }
        } else Modifier

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .then(avatarModifier)
                    .size(48.dp)
                    .clip(CircleShape)
                    .clickable { onTabSelected(ScreenDestination.Main.Profile) }
                    .background(
                        if (isProfileSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.primaryContainer
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = if (isProfileSelected) MaterialTheme.colorScheme.onPrimary
                           else MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
@Preview
@Composable
fun NavigationRailPreview() {
    IntelIQTheme(
        darkTheme = false
    ) {
        AdaptiveNavigationRail(
            currentTab = ScreenDestination.Main.Home,
            isDarkThemeMode = DarkThemeMode.LIGHT,
            onTabSelected = {},
            onLogout = {}
        )
    }
}
