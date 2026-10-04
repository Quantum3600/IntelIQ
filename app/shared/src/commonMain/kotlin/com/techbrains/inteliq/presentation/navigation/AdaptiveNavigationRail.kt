package com.techbrains.inteliq.presentation.navigation

import com.techbrains.inteliq.IqLogo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.styleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techbrains.inteliq.presentation.theme.ComponentStyles
import com.techbrains.inteliq.presentation.theme.DarkThemeMode
import com.techbrains.inteliq.presentation.theme.IntelIQTheme

/**
 * Adaptive Navigation Rail for Tablet
 */
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun AdaptiveNavigationRail(
    currentTab: ScreenDestination.Main,
    onTabSelected: (ScreenDestination.Main) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    isDarkThemeMode: DarkThemeMode = DarkThemeMode.LIGHT
) {
    Column(
        modifier = modifier
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
        // Top: App Logo
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            IqLogo(
                modifier = Modifier.size(44.dp),
                isDarkTheme = isDarkThemeMode == DarkThemeMode.DARK
            )
            Spacer(modifier = Modifier.height(56.dp))

            // Navigation Items (excluding Profile which is anchored at bottom)
            mainNavItems
                .filter { it.destination != ScreenDestination.Main.Profile }
                .forEach { item ->
                    val isSelected = currentTab == item.destination
                    val interactionSource = remember { MutableInteractionSource() }
                    val itemShape = when(item.destination) {
                        ScreenDestination.Main.Home -> RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp, bottomStart = 8.dp, bottomEnd = 8.dp)
                        ScreenDestination.Main.Practice -> RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp, topStart = 8.dp, topEnd = 8.dp)
                        else -> RoundedCornerShape(8.dp)
                    }

                    val navItemStyle = if (isSelected) {
                        ComponentStyles.selectedNavItemStyle(shape = itemShape)
                    } else {
                        ComponentStyles.unselectedNavItemStyle(shape = itemShape)
                    }

                    Box(
                        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp, end = 5.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .styleable(style = navItemStyle)
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null,
                                    onClick = { onTabSelected(item.destination) }
                                ),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
        }

        // Bottom: Profile Avatar Menu Button
        val isProfileSelected = currentTab == ScreenDestination.Main.Profile
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (isProfileSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.primaryContainer
                    )
                    .clickable { onTabSelected(ScreenDestination.Main.Profile) },
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
