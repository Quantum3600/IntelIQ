package com.techbrains.inteliq.presentation.theme.styles

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kyant.capsule.ContinuousCapsule
import com.kyant.capsule.ContinuousRoundedRectangle
import com.techbrains.inteliq.presentation.navigation.ScreenDestination

enum class NavLayoutType {
    DRAWER,
    RAIL,
    FLOATING_BAR
}

data class NavShapeQuad(
    val topStart: Dp,
    val topEnd: Dp,
    val bottomStart: Dp,
    val bottomEnd: Dp
)

@OptIn(ExperimentalFoundationStyleApi::class)
object NavigationStyles {

    /** Floating Navigation Bar Container Style */
    val floatingNavBarStyle: Style
        @Composable get() {
            val colors = MaterialTheme.colorScheme
            return Style {
                background(Color.Transparent)
                shape(ContinuousCapsule())
                contentPadding(6.dp)
            }
        }

    /** Inner Animated Selection Indicator Style */
    val navIndicatorStyle: Style
        @Composable get() {
            val colors = MaterialTheme.colorScheme
            val shapes = MaterialTheme.shapes
            return Style {
                background(colors.primaryContainer)
                shape(shapes.medium)
            }
        }

    /** Floating Navigation Rail Style */
    val navRailStyle: Style
        @Composable get() {
            val colors = MaterialTheme.colorScheme
            return Style {
                background(colors.surface.copy(alpha = 0.70f))
                shape(ContinuousRoundedRectangle(24.dp))
                contentPadding(vertical = 16.dp, horizontal = 8.dp)
            }
        }

    /** Floating Navigation Drawer Style */
    val navDrawerStyle: Style
        @Composable get() {
            val colors = MaterialTheme.colorScheme
            return Style {
                background(colors.surface.copy(alpha = 0.70f))
                shape(ContinuousRoundedRectangle(32.dp))
                contentPadding(16.dp)
            }
        }

    /** Navigation Item Style */
    val navItemStyle: Style
        @Composable get() {
            val colors = MaterialTheme.colorScheme
            val shapes = MaterialTheme.shapes
            return Style {
                shape(shapes.medium)
                contentPadding(4.dp)
                if (state.isPressed) {
                    background(colors.surfaceVariant)
                }
            }
        }

    /** Selected Navigation Item Style */
    @Composable
    fun selectedNavItemStyle(shape: Shape? = null): Style {
        val effectiveShape = shape ?: RoundedCornerShape(24.dp)
        return Style {
            shape(effectiveShape)
            contentPadding(horizontal = 12.dp, vertical = 8.dp)
        }
    }

    val selectedNavItemStyle: Style
        @Composable get() = selectedNavItemStyle()

    /** Unselected Navigation Item Style */
    @Composable
    fun unselectedNavItemStyle(shape: Shape? = null): Style {
        val effectiveShape = shape ?: RoundedCornerShape(16.dp)
        return Style {
            shape(effectiveShape)
            contentPadding(horizontal = 12.dp, vertical = 8.dp)
        }
    }

    val unselectedNavItemStyle: Style
        @Composable get() = unselectedNavItemStyle()

    /** Profile Card in Drawer/Rail Style */
    val profileCardStyle: Style
        @Composable get() {
            val colors = MaterialTheme.colorScheme
            return Style {
                background(colors.surfaceVariant)
                shape(RoundedCornerShape(24.dp))
                contentPadding(12.dp)
            }
        }

    /** Log Out Button Style */
    val logoutButtonStyle: Style
        @Composable get() {
            val colors = MaterialTheme.colorScheme
            return Style {
                background(colors.errorContainer)
                shape(RoundedCornerShape(24.dp))
                contentPadding(horizontal = 16.dp, vertical = 10.dp)
            }
        }

    /**
     * Remembers a [Shape] (ContinuousRoundedRectangle) that smoothly animates its 4 corner radii
     * during selection transitions between unselected state and selected state.
     */
    @Composable
    fun rememberAnimatedNavItemShape(
        destination: ScreenDestination.Main,
        isSelected: Boolean,
        layoutType: NavLayoutType = NavLayoutType.DRAWER,
        selectedCornerRadius: Dp = 28.dp,
        customUnselectedCorners: NavShapeQuad? = null,
        animationSpec: AnimationSpec<Dp> = tween(durationMillis = 220, easing = FastOutSlowInEasing)
    ): Shape {
        val (unselectedTopStart, unselectedTopEnd, unselectedBottomStart, unselectedBottomEnd) = remember(destination, layoutType, customUnselectedCorners) {
            customUnselectedCorners ?: when (layoutType) {
                NavLayoutType.DRAWER -> when (destination) {
                    ScreenDestination.Main.Home -> NavShapeQuad(32.dp, 32.dp, 8.dp, 8.dp)
                    ScreenDestination.Main.Practice -> NavShapeQuad(8.dp, 8.dp, 24.dp, 24.dp)
                    ScreenDestination.Main.Profile -> NavShapeQuad(24.dp, 24.dp, 24.dp, 24.dp)
                    else -> NavShapeQuad(8.dp, 8.dp, 8.dp, 8.dp)
                }
                NavLayoutType.RAIL -> when (destination) {
                    ScreenDestination.Main.Home -> NavShapeQuad(32.dp, 32.dp, 8.dp, 8.dp)
                    ScreenDestination.Main.Practice -> NavShapeQuad(8.dp, 8.dp, 32.dp, 32.dp)
                    ScreenDestination.Main.Profile -> NavShapeQuad(24.dp, 24.dp, 24.dp, 24.dp)
                    else -> NavShapeQuad(8.dp, 8.dp, 8.dp, 8.dp)
                }
                NavLayoutType.FLOATING_BAR -> when (destination) {
                    ScreenDestination.Main.Home -> NavShapeQuad(32.dp, 8.dp, 32.dp, 8.dp)
                    ScreenDestination.Main.Profile -> NavShapeQuad(8.dp, 32.dp, 8.dp, 32.dp)
                    else -> NavShapeQuad(8.dp, 8.dp, 8.dp, 8.dp)
                }
            }
        }

        val destinationName = remember(destination) { destination::class.simpleName ?: "NavItem" }

        val topStart by animateDpAsState(
            targetValue = if (isSelected) selectedCornerRadius else unselectedTopStart,
            animationSpec = animationSpec,
            label = "NavItemShape_TopStart_$destinationName"
        )
        val topEnd by animateDpAsState(
            targetValue = if (isSelected) selectedCornerRadius else unselectedTopEnd,
            animationSpec = animationSpec,
            label = "NavItemShape_TopEnd_$destinationName"
        )
        val bottomStart by animateDpAsState(
            targetValue = if (isSelected) selectedCornerRadius else unselectedBottomStart,
            animationSpec = animationSpec,
            label = "NavItemShape_BottomStart_$destinationName"
        )
        val bottomEnd by animateDpAsState(
            targetValue = if (isSelected) selectedCornerRadius else unselectedBottomEnd,
            animationSpec = animationSpec,
            label = "NavItemShape_BottomEnd_$destinationName"
        )

        return remember(topStart, topEnd, bottomStart, bottomEnd) {
            ContinuousRoundedRectangle(
                topStart = topStart,
                topEnd = topEnd,
                bottomStart = bottomStart,
                bottomEnd = bottomEnd
            )
        }
    }
}
