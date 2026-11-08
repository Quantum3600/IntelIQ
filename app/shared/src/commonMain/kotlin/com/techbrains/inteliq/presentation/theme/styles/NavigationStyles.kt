package com.techbrains.inteliq.presentation.theme.styles

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.contentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.kyant.capsule.ContinuousCapsule
import com.kyant.capsule.ContinuousRoundedRectangle

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
}
