package com.techbrains.inteliq.presentation.navigation

import com.techbrains.inteliq.IqLogo
import com.techbrains.inteliq.presentation.theme.Flame
import com.techbrains.inteliq.presentation.theme.LocalHazeState
import dev.chrisbanes.haze.HazeInput
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.rememberHazeState
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.glass.GlassStyle
import dev.chrisbanes.haze.glass.hazeGlass
import dev.chrisbanes.haze.glass.material3.material3



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techbrains.inteliq.presentation.theme.DarkThemeMode

@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope = staticCompositionLocalOf<SharedTransitionScope?> { null }
val LocalNavAnimatedVisibilityScope = staticCompositionLocalOf<AnimatedVisibilityScope?> { null }

/**
 * Adaptive Window Size Classification (per official Material 3 / @adaptive skill)
 */
enum class AdaptiveWindowSizeClass {
    COMPACT,   // < 600.dp (Mobile)
    MEDIUM,    // 600.dp .. 840.dp (Tablet)
    EXPANDED   // >= 840.dp (Desktop)
}

data class NavigationNavItem(
    val title: String,
    val icon: ImageVector,
    val destination: ScreenDestination.Main
)

val mainNavItems = listOf(
    NavigationNavItem("Home", Icons.Default.Home, ScreenDestination.Main.Home),
    NavigationNavItem("Learn", Icons.Default.AutoStories, ScreenDestination.Main.Learn),
    NavigationNavItem("Discover", Icons.Default.Explore, ScreenDestination.Main.Discover),
    NavigationNavItem("Practice", Icons.Default.FitnessCenter, ScreenDestination.Main.Practice),
    NavigationNavItem("Profile", Icons.Default.Person, ScreenDestination.Main.Profile)
)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AdaptiveNavigationLayout(
    currentTab: ScreenDestination.Main,
    onTabSelected: (ScreenDestination.Main) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    userName: String = "Student",
    userEmail: String = "student@inteliq.edu",
    isDarkTheme: Boolean = false,
    onToggleDarkTheme: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val showTopHeader = currentTab != ScreenDestination.Main.Profile && currentTab != ScreenDestination.Main.Discover
    val hazeState = LocalHazeState.current ?: rememberHazeState()
    var isDrawerExpanded by rememberSaveable { mutableStateOf(true) }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val sizeClass = when {
            maxWidth < 600.dp -> AdaptiveWindowSizeClass.COMPACT
            maxWidth < 840.dp -> AdaptiveWindowSizeClass.MEDIUM
            else -> AdaptiveWindowSizeClass.EXPANDED
        }

        when (sizeClass) {
            AdaptiveWindowSizeClass.COMPACT -> {
                // Mobile Layout: Top Header + Main Content + Floating Nav Bar at Bottom
                Scaffold(
                    topBar = {
                        if (showTopHeader) {
                            TopHeaderAppLogoAndStats(
                                isDarkTheme = isDarkTheme,
                                onToggleDarkTheme = onToggleDarkTheme,
                                hazeState = hazeState
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .hazeSource(hazeState)
                        ) {
                            content()
                        }

                        // Floating Navigation Bar anchored at bottom with shadow clearance space
                        FloatingNavigationBar(
                            currentTab = currentTab,
                            onTabSelected = onTabSelected,
                            hazeState = hazeState,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .navigationBarsPadding()
                                .padding(start = 12.dp, end = 16.dp)
                        )
                    }
                }
            }

            AdaptiveWindowSizeClass.MEDIUM -> {
                // Tablet Layout: Floating Side Navigation Rail + Main Header & Content
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    AdaptiveNavigationRail(
                        currentTab = currentTab,
                        onTabSelected = onTabSelected,
                        onLogout = onLogout,
                        isDarkThemeMode = if (isDarkTheme) DarkThemeMode.DARK else DarkThemeMode.LIGHT,
                        hazeState = hazeState
                    )

                    Scaffold(
                        topBar = {
                            if (showTopHeader) {
                                TopHeaderAppLogoAndStats(
                                    isDarkTheme = isDarkTheme,
                                    onToggleDarkTheme = onToggleDarkTheme,
                                    hazeState = hazeState
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .hazeSource(hazeState)
                                .padding(innerPadding)
                        ) {
                            content()
                        }
                    }
                }
            }

            AdaptiveWindowSizeClass.EXPANDED -> {
                // Desktop Layout: Collapsible Navigation Drawer / Rail + Main Header & Content
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    SharedTransitionLayout {
                        AnimatedContent(
                            targetState = isDrawerExpanded,
                            transitionSpec = {
                                (fadeIn(animationSpec = tween(220, delayMillis = 60)) togetherWith
                                    fadeOut(animationSpec = tween(160)))
                                    .using(SizeTransform(clip = false))
                            },
                            label = "DrawerRailTransition"
                        ) { expanded ->
                            CompositionLocalProvider(
                                LocalSharedTransitionScope provides this@SharedTransitionLayout,
                                LocalNavAnimatedVisibilityScope provides this@AnimatedContent
                            ) {
                                if (expanded) {
                                    AdaptiveNavigationDrawer(
                                        currentTab = currentTab,
                                        onTabSelected = onTabSelected,
                                        onLogout = onLogout,
                                        userName = userName,
                                        userEmail = userEmail,
                                        isDarkThemeMode = if (isDarkTheme) DarkThemeMode.DARK else DarkThemeMode.LIGHT,
                                        hazeState = hazeState,
                                        onCollapse = { isDrawerExpanded = false }
                                    )
                                } else {
                                    AdaptiveNavigationRail(
                                        currentTab = currentTab,
                                        onTabSelected = onTabSelected,
                                        onLogout = onLogout,
                                        isDarkThemeMode = if (isDarkTheme) DarkThemeMode.DARK else DarkThemeMode.LIGHT,
                                        hazeState = hazeState,
                                        onExpand = { isDrawerExpanded = true }
                                    )
                                }
                            }
                        }
                    }

                    Scaffold(
                        topBar = {
                            if (showTopHeader) {
                                TopHeaderAppLogoAndStats(
                                    isDarkTheme = isDarkTheme,
                                    onToggleDarkTheme = onToggleDarkTheme,
                                    hazeState = hazeState
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .hazeSource(hazeState)
                            ) {
                                content()
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Top Header containing Logo, Streak/XP Stats, and Dark/Light Mode Toggle
 */
@Composable
fun TopHeaderAppLogoAndStats(
    isDarkTheme: Boolean = false,
    onToggleDarkTheme: () -> Unit = {},
    modifier: Modifier = Modifier,
    hazeState: HazeState? = LocalHazeState.current
) {
    val activeHaze = hazeState ?: LocalHazeState.current
    val topBarModifier = if (activeHaze != null) {
        modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .hazeGlass(
                input = HazeInput.Backdrop(activeHaze),
                style = GlassStyle.material3()
            )
    } else {
        modifier
            .fillMaxWidth()
            .statusBarsPadding()
    }

    Surface(
        modifier = topBarModifier,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f),
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IqLogo(
                    modifier = Modifier.size(32.dp),
                    isDarkTheme = isDarkTheme
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "IntelIQ",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Streak Pill
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Flame.copy(alpha = 0.12f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🔥", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "5 days",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Flame
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // XP Pill
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⚡", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "1,250 XP",
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Dark / Light Mode Toggle Button
                IconButton(
                    onClick = onToggleDarkTheme,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = if (isDarkTheme) "Switch to Light Mode" else "Switch to Dark Mode",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
