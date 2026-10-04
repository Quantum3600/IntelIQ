package com.techbrains.inteliq.presentation.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.styleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import inteliq.app.shared.generated.resources.Res
import inteliq.app.shared.generated.resources.fredoka_variable_font_wdth_wght
import org.jetbrains.compose.resources.Font
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techbrains.inteliq.InteliqLogo
import com.techbrains.inteliq.presentation.theme.ComponentStyles
import com.techbrains.inteliq.presentation.theme.DarkThemeMode
import com.techbrains.inteliq.presentation.theme.IntelIQTheme

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
    isDarkThemeMode: DarkThemeMode = DarkThemeMode.LIGHT,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .statusBarsPadding()
            .navigationBarsPadding()
            .fillMaxHeight()
            .width(264.dp)
            .styleable(
                style = ComponentStyles.navDrawerStyle
            ),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top: Full App Branding
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    InteliqLogo(
                        modifier = Modifier.fillMaxWidth(0.8f),
                        isDarkTheme = isDarkThemeMode == DarkThemeMode.DARK
                    )
                    Text(
                        text = "Dynamic Knowledge Tracing",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Expanded Navigation Items (excluding Profile which is anchored at bottom)
            mainNavItems
                .filter { it.destination != ScreenDestination.Main.Profile }
                .forEach { item ->
                    val isSelected = currentTab == item.destination
                    val interactionSource = remember { MutableInteractionSource() }
                    val itemShape = if(!isSelected) {
                        when(item.destination) {
                            ScreenDestination.Main.Home -> RoundedCornerShape(
                                topStart = 32.dp,
                                bottomStart = 32.dp,
                                topEnd = 8.dp,
                                bottomEnd = 8.dp
                            )
                            ScreenDestination.Main.Practice -> RoundedCornerShape(
                                topStart = 8.dp,
                                bottomStart = 24.dp,
                                topEnd = 8.dp,
                                bottomEnd = 24.dp
                            )
                            else -> RoundedCornerShape(8.dp)
                        }
                    } else {
                        RoundedCornerShape(24.dp)
                    }

                    val fontWidth by animateFloatAsState(
                        targetValue = if (isSelected) 125f else 100f,
                        animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
                        label = "FontWidthAnimation"
                    )

                    val itemFontFamily = FontFamily(
                        Font(
                            resource = Res.font.fredoka_variable_font_wdth_wght,
                            weight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            style = FontStyle.Normal,
                            variationSettings = FontVariation.Settings(
                                FontVariation.width(fontWidth),
                                FontVariation.weight(if (isSelected) 700 else 500)
                            )
                        )
                    )

                    val itemStyle = if (isSelected) {
                        ComponentStyles.selectedNavItemStyle(shape = itemShape)
                    } else {
                        ComponentStyles.unselectedNavItemStyle(shape = itemShape)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 4.dp, end = 5.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .styleable(style = itemStyle)
                                .padding(6.dp)
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null,
                                    onClick = { onTabSelected(item.destination) }
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = item.title,
                                fontSize = 14.sp,
                                fontFamily = itemFontFamily,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
        }

        // Bottom: Profile Card + Full-Width Log Out Button
        val isProfileSelected = currentTab == ScreenDestination.Main.Profile
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 5.dp, bottom = 5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .styleable(
                            style = if (isProfileSelected) ComponentStyles.selectedNavItemStyle else ComponentStyles.profileCardStyle
                        )
                        .clickable { onTabSelected(ScreenDestination.Main.Profile) }
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Top Row: Avatar + User Name
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "User Avatar",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = userName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Separate Row under Avatar & Name: User Email
                        Text(
                            text = userEmail,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Max-width Log Out Button with Text & Icon & Neobrutalist Shadow
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 5.dp, bottom = 5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .styleable(style = ComponentStyles.logoutButtonStyle)
                        .clickable(onClick = onLogout),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Log Out",
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Log Out",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}
@Preview
@Composable
fun NavigationDrawerPreview() {
    IntelIQTheme(
        darkTheme = true
    ) {
        AdaptiveNavigationDrawer(
            currentTab = ScreenDestination.Main.Home,
            onTabSelected = {},
            onLogout = {},
            userName = "John Doe",
            isDarkThemeMode = DarkThemeMode.DARK,
            userEmail = "william.henry.harrison@example-pet-store.com"
        )
    }
}
