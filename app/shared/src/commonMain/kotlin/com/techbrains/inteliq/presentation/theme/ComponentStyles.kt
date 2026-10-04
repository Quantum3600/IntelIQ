package com.techbrains.inteliq.presentation.theme

import androidx.compose.foundation.style.Style
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.techbrains.inteliq.presentation.theme.styles.ButtonGroupPosition
import com.techbrains.inteliq.presentation.theme.styles.ButtonStyles
import com.techbrains.inteliq.presentation.theme.styles.CardStyles
import com.techbrains.inteliq.presentation.theme.styles.ChipStyles
import com.techbrains.inteliq.presentation.theme.styles.ListItemPosition
import com.techbrains.inteliq.presentation.theme.styles.NavigationStyles
import com.techbrains.inteliq.presentation.theme.styles.SwitchStyles
import com.techbrains.inteliq.presentation.theme.styles.TextFieldStyles

/** Re-export position enums for backward compatibility */
typealias ListItemPosition = ListItemPosition
typealias ButtonGroupPosition = ButtonGroupPosition

/**
 * Unified facade object delegating to modular style files in `presentation/theme/styles/`.
 *
 * Direct style files for quick editing & debugging:
 * - [ButtonStyles]    -> `presentation/theme/styles/ButtonStyles.kt`
 * - [NavigationStyles]-> `presentation/theme/styles/NavigationStyles.kt`
 * - [CardStyles]      -> `presentation/theme/styles/CardStyles.kt`
 * - [TextFieldStyles] -> `presentation/theme/styles/TextFieldStyles.kt`
 * - [SwitchStyles]    -> `presentation/theme/styles/SwitchStyles.kt`
 * - [ChipStyles]      -> `presentation/theme/styles/ChipStyles.kt`
 */
object ComponentStyles {

    val floatingNavBarStyle: Style
        @Composable get() = NavigationStyles.floatingNavBarStyle

    val navIndicatorStyle: Style
        @Composable get() = NavigationStyles.navIndicatorStyle

    val navRailStyle: Style
        @Composable get() = NavigationStyles.navRailStyle

    val navDrawerStyle: Style
        @Composable get() = NavigationStyles.navDrawerStyle

    val navItemStyle: Style
        @Composable get() = NavigationStyles.navItemStyle

    val selectedNavItemStyle: Style
        @Composable get() = NavigationStyles.selectedNavItemStyle

    @Composable
    fun selectedNavItemStyle(shape: Shape): Style =
        NavigationStyles.selectedNavItemStyle(shape)

    val unselectedNavItemStyle: Style
        @Composable get() = NavigationStyles.unselectedNavItemStyle

    @Composable
    fun unselectedNavItemStyle(shape: Shape): Style =
        NavigationStyles.unselectedNavItemStyle(shape)

    val profileCardStyle: Style
        @Composable get() = NavigationStyles.profileCardStyle

    val logoutButtonStyle: Style
        @Composable get() = NavigationStyles.logoutButtonStyle

    val primaryButtonStyle: Style
        @Composable get() = ButtonStyles.primaryButtonStyle

    @Composable
    fun primaryButtonStyle(shape: Shape): Style =
        ButtonStyles.primaryButtonStyle(shape = shape)

    val debugButtonStyle: Style
        @Composable get() = ButtonStyles.debugButtonStyle

    @Composable
    fun debugButtonStyle(shape: Shape): Style =
        ButtonStyles.debugButtonStyle(shape = shape)

    val listItemCardStyle: Style
        @Composable get() = CardStyles.listItemCardStyle

    @Composable
    fun cardStyle(shape: Shape): Style =
        CardStyles.cardStyle(shape = shape)

    val textFieldStyle: Style
        @Composable get() = TextFieldStyles.textFieldStyle

    @Composable
    fun textFieldStyle(shape: Shape): Style =
        TextFieldStyles.textFieldStyle(shape)

    @Composable
    fun switchTrackStyle(isChecked: Boolean): Style =
        SwitchStyles.switchTrackStyle(isChecked)

    @Composable
    fun switchThumbStyle(isChecked: Boolean): Style =
        SwitchStyles.switchThumbStyle(isChecked)

    @Composable
    fun chipStyle(isSelected: Boolean = false, containerColor: Color? = null, shapeCorner: Dp = 100.dp): Style =
        ChipStyles.chipStyle(isSelected, containerColor, shapeCorner)

    @Composable
    fun groupedListItemStyle(position: ListItemPosition): Style =
        CardStyles.groupedListItemStyle(position)

    @Composable
    fun buttonGroupStyle(position: ButtonGroupPosition, isSelected: Boolean = false): Style =
        ButtonStyles.buttonGroupStyle(position, isSelected)
}
