package com.techbrains.inteliq.presentation.navigation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techbrains.inteliq.presentation.theme.ComponentStyles
import com.techbrains.inteliq.presentation.theme.Flame
import com.techbrains.inteliq.presentation.theme.Ink
import com.techbrains.inteliq.presentation.theme.PaperRaised
import com.techbrains.inteliq.presentation.theme.Signal
import com.techbrains.inteliq.presentation.theme.SignalDim
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.rememberUpdatedStyleState
import androidx.compose.foundation.style.styleable

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
    NavigationNavItem("Path", Icons.Default.Map, ScreenDestination.Main.Path),
    NavigationNavItem("Practice", Icons.Default.FitnessCenter, ScreenDestination.Main.Practice),
    NavigationNavItem("Progress", Icons.Default.BarChart, ScreenDestination.Main.Progress),
    NavigationNavItem("Profile", Icons.Default.Person, ScreenDestination.Main.Profile)
)

@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun AdaptiveNavigationLayout(
    currentTab: ScreenDestination.Main,
    onTabSelected: (ScreenDestination.Main) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    userName: String = "Student",
    userEmail: String = "student@inteliq.edu",
    content: @Composable () -> Unit
) {
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
                    topBar = { TopHeaderAppLogoAndStats() }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        content()

                        // Floating Navigation Bar anchored at bottom
                        FloatingNavigationBar(
                            currentTab = currentTab,
                            onTabSelected = onTabSelected,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(horizontal = 20.dp, vertical = 16.dp)
                        )
                    }
                }
            }

            AdaptiveWindowSizeClass.MEDIUM -> {
                // Tablet Layout: Side Navigation Rail + Main Header & Content
                Row(modifier = Modifier.fillMaxSize()) {
                    AdaptiveNavigationRail(
                        currentTab = currentTab,
                        onTabSelected = onTabSelected,
                        onLogout = onLogout
                    )

                    Scaffold(
                        topBar = { TopHeaderAppLogoAndStats() }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            content()
                        }
                    }
                }
            }

            AdaptiveWindowSizeClass.EXPANDED -> {
                // Desktop Layout: Expanded Navigation Drawer + Main Header & Content
                Row(modifier = Modifier.fillMaxSize()) {
                    AdaptiveNavigationDrawer(
                        currentTab = currentTab,
                        onTabSelected = onTabSelected,
                        onLogout = onLogout,
                        userName = userName,
                        userEmail = userEmail
                    )

                    Scaffold(
                        topBar = { TopHeaderAppLogoAndStats() }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            content()
                        }
                    }
                }
            }
        }
    }
}

/**
 * Top Header containing Logo and Streak/XP Stats
 */
@Composable
fun TopHeaderAppLogoAndStats() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = PaperRaised,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Signal,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "IQ",
                            color = PaperRaised,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "IntelIQ",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Ink
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

                Spacer(modifier = Modifier.width(12.dp))

                // XP Pill
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SignalDim
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
                            color = Signal
                        )
                    }
                }
            }
        }
    }
}

/**
 * Floating Navigation Bar for Mobile with smooth animated inner indicator
 */
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun FloatingNavigationBar(
    currentTab: ScreenDestination.Main,
    onTabSelected: (ScreenDestination.Main) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedIndex = mainNavItems.indexOfFirst { it.destination == currentTab }.coerceAtLeast(0)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .styleable(
                style = ComponentStyles.floatingNavBarStyle
            )
    ) {
        val totalWidth = maxWidth - 24.dp // accounting for content padding
        val itemWidth = totalWidth / mainNavItems.size

        // Smooth sliding animation for inner indicator pill
        val indicatorOffset by animateDpAsState(
            targetValue = itemWidth * selectedIndex,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            label = "NavIndicatorOffset"
        )

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterStart
        ) {
            // Animated Selection Indicator Pill
            Box(
                modifier = Modifier
                    .offset(x = indicatorOffset)
                    .width(itemWidth)
                    .height(44.dp)
                    .styleable(
                        style = ComponentStyles.navIndicatorStyle
                    )
            )

            // Navigation Items Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                mainNavItems.forEachIndexed { index, item ->
                    val isSelected = index == selectedIndex
                    val interactionSource = remember { MutableInteractionSource() }
                    val styleState = rememberUpdatedStyleState(interactionSource)

                    Column(
                        modifier = Modifier
                            .width(itemWidth)
                            .height(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null,
                                onClick = { onTabSelected(item.destination) }
                            )
                            .styleable(
                                styleState = styleState,
                                style = ComponentStyles.navItemStyle
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = if (isSelected) Signal else Ink.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.title,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Signal else Ink.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Adaptive Navigation Rail for Tablet
 */
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun AdaptiveNavigationRail(
    currentTab: ScreenDestination.Main,
    onTabSelected: (ScreenDestination.Main) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(80.dp)
            .styleable(
                style = ComponentStyles.navRailStyle
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top: App Logo Placeholder
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Signal,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "IQ",
                        color = PaperRaised,
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Navigation Items
            mainNavItems.forEach { item ->
                val isSelected = currentTab == item.destination
                val interactionSource = remember { MutableInteractionSource() }
                val styleState = rememberUpdatedStyleState(interactionSource)

                Column(
                    modifier = Modifier
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = { onTabSelected(item.destination) }
                        )
                        .styleable(
                            styleState = styleState,
                            style = if (isSelected) ComponentStyles.selectedNavItemStyle else ComponentStyles.navItemStyle
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (isSelected) Signal else Ink.copy(alpha = 0.7f),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Bottom: Profile Avatar Menu Button
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(SignalDim)
                    .clickable { onTabSelected(ScreenDestination.Main.Profile) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = Signal,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

/**
 * Adaptive Navigation Drawer for Desktop
 */
@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun AdaptiveNavigationDrawer(
    currentTab: ScreenDestination.Main,
    onTabSelected: (ScreenDestination.Main) -> Unit,
    onLogout: () -> Unit,
    userName: String,
    userEmail: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(260.dp)
            .styleable(
                style = ComponentStyles.navDrawerStyle
            ),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top: Full App Branding
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Signal,
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "IQ",
                            color = PaperRaised,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "IntelIQ",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Ink
                    )
                    Text(
                        text = "Dynamic Knowledge Tracing",
                        fontSize = 11.sp,
                        color = Ink.copy(alpha = 0.6f)
                    )
                }
            }

            // Expanded Navigation Items
            mainNavItems.forEach { item ->
                val isSelected = currentTab == item.destination
                val interactionSource = remember { MutableInteractionSource() }
                val styleState = rememberUpdatedStyleState(interactionSource)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = { onTabSelected(item.destination) }
                        )
                        .styleable(
                            styleState = styleState,
                            style = if (isSelected) ComponentStyles.selectedNavItemStyle else ComponentStyles.navItemStyle
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (isSelected) Signal else Ink.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = item.title,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Signal else Ink.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Bottom: Expanded Profile Card with Name & Sign Out Button
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .styleable(
                        style = ComponentStyles.profileCardStyle
                    )
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(SignalDim),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "User Avatar",
                                    tint = Signal,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = userName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Ink
                                )
                                Text(
                                    text = userEmail,
                                    fontSize = 11.sp,
                                    color = Ink.copy(alpha = 0.6f)
                                )
                            }
                        }

                        IconButton(
                            onClick = onLogout,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Sign Out",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
