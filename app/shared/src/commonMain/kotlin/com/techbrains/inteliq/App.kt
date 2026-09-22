package com.techbrains.inteliq

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techbrains.inteliq.data.api.AuthApiImpl
import com.techbrains.inteliq.data.local.InMemoryTokenStorage
import com.techbrains.inteliq.data.local.InMemoryUserSessionLocalDataSource
import com.techbrains.inteliq.data.repository.AuthRepositoryImpl
import com.techbrains.inteliq.domain.usecase.GetAuthStateUseCase
import com.techbrains.inteliq.domain.usecase.LoginUseCase
import com.techbrains.inteliq.domain.usecase.LogoutUseCase
import com.techbrains.inteliq.domain.usecase.RegisterUseCase
import com.techbrains.inteliq.presentation.auth.AuthIntent
import com.techbrains.inteliq.presentation.auth.AuthScreen
import com.techbrains.inteliq.presentation.auth.AuthViewModel
import com.techbrains.inteliq.presentation.courses.CoursesScreen
import com.techbrains.inteliq.presentation.dashboard.DashboardScreen
import com.techbrains.inteliq.presentation.navigation.ScreenDestination
import com.techbrains.inteliq.presentation.navigation.rememberNavigator
import com.techbrains.inteliq.presentation.profile.ProfileScreen
import com.techbrains.inteliq.presentation.progress.ProgressScreen
import com.techbrains.inteliq.presentation.quiz.QuizScreen
import com.techbrains.inteliq.presentation.splash.SplashScreen
import com.techbrains.inteliq.presentation.theme.Flame
import com.techbrains.inteliq.presentation.theme.Ink
import com.techbrains.inteliq.presentation.theme.IntelIQTheme
import com.techbrains.inteliq.presentation.theme.Paper
import com.techbrains.inteliq.presentation.theme.PaperRaised
import com.techbrains.inteliq.presentation.theme.Signal
import com.techbrains.inteliq.presentation.theme.SignalDim
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

@Composable
fun App() {
    IntelIQTheme {
        val navigator = rememberNavigator(initialDestination = ScreenDestination.Splash)

        // Setup Dependencies
        val httpClient = remember {
            HttpClient(CIO) {
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        prettyPrint = true
                    })
                }
            }
        }
        val authApi = remember { AuthApiImpl(client = httpClient, baseUrl = getPlatform().defaultBaseUrl) }
        val tokenStorage = remember { InMemoryTokenStorage() }
        val sessionLocalDataSource = remember { InMemoryUserSessionLocalDataSource() }
        val authRepository = remember {
            AuthRepositoryImpl(
                authApi = authApi,
                tokenStorage = tokenStorage,
                userSessionLocalDataSource = sessionLocalDataSource
            )
        }

        val loginUseCase = remember { LoginUseCase(authRepository) }
        val registerUseCase = remember { RegisterUseCase(authRepository) }
        val logoutUseCase = remember { LogoutUseCase(authRepository) }
        val getAuthStateUseCase = remember { GetAuthStateUseCase(authRepository) }

        val authViewModel = remember {
            AuthViewModel(loginUseCase, registerUseCase, getAuthStateUseCase)
        }

        val authState by authViewModel.uiState.collectAsState()

        AnimatedContent(
            targetState = navigator.currentDestination,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "ScreenTransition"
        ) { destination ->
            when (destination) {
                is ScreenDestination.Splash -> {
                    SplashScreen(
                        onSplashFinished = {
                            navigator.navigateTo(ScreenDestination.Auth)
                        }
                    )
                }

                is ScreenDestination.Auth -> {
                    AuthScreen(
                        state = authState,
                        onIntent = { intent ->
                            authViewModel.onIntent(intent)
                            if (intent is AuthIntent.SubmitAuth && authState.authenticatedUser != null) {
                                navigator.navigateTo(ScreenDestination.Main.Home)
                            }
                        }
                    )
                }

                is ScreenDestination.Main -> {
                    MainAppShell(
                        currentTab = navigator.currentMainTab,
                        onTabSelected = { tab ->
                            navigator.navigateTo(tab)
                        },
                        onLogout = {
                            authViewModel.onIntent(AuthIntent.ClearError)
                            navigator.navigateTo(ScreenDestination.Auth)
                        }
                    )
                }

                else -> {
                    MainAppShell(
                        currentTab = ScreenDestination.Main.Home,
                        onTabSelected = { tab -> navigator.navigateTo(tab) },
                        onLogout = { navigator.navigateTo(ScreenDestination.Auth) }
                    )
                }
            }
        }
    }
}

@Composable
fun MainAppShell(
    currentTab: ScreenDestination.Main,
    onTabSelected: (ScreenDestination.Main) -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBarWithStreakAndXP()
        },
        bottomBar = {
            BottomNavigationBar(
                currentTab = currentTab,
                onTabSelected = onTabSelected
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                is ScreenDestination.Main.Home -> DashboardScreen()
                is ScreenDestination.Main.Path -> CoursesScreen()
                is ScreenDestination.Main.Practice -> QuizScreen()
                is ScreenDestination.Main.Progress -> ProgressScreen()
                is ScreenDestination.Main.Profile -> ProfileScreen(onLogout = onLogout)
            }
        }
    }
}

@Composable
fun TopAppBarWithStreakAndXP() {
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
            Text(
                text = "IntelIQ",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Signal
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Streak Pill (Flame)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Flame.copy(alpha = 0.12f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🔥",
                            fontSize = 14.sp
                        )
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

                // XP Pill (Monospace figures per DESIGN.md §2.2)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = SignalDim
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "⚡",
                            fontSize = 14.sp
                        )
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

@Composable
fun BottomNavigationBar(
    currentTab: ScreenDestination.Main,
    onTabSelected: (ScreenDestination.Main) -> Unit
) {
    val items = remember {
        listOf(
            NavigationTabItem("Home", "🏠", ScreenDestination.Main.Home),
            NavigationTabItem("Path", "🗺️", ScreenDestination.Main.Path),
            NavigationTabItem("Practice", "🎯", ScreenDestination.Main.Practice),
            NavigationTabItem("Progress", "📊", ScreenDestination.Main.Progress),
            NavigationTabItem("Profile", "👤", ScreenDestination.Main.Profile)
        )
    }

    NavigationBar(
        containerColor = PaperRaised,
        contentColor = Ink
    ) {
        items.forEach { item ->
            val isSelected = currentTab == item.destination
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(item.destination) },
                icon = {
                    Text(
                        text = item.icon,
                        fontSize = 18.sp
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Signal,
                    selectedTextColor = Signal,
                    indicatorColor = SignalDim,
                    unselectedIconColor = Ink.copy(alpha = 0.6f),
                    unselectedTextColor = Ink.copy(alpha = 0.6f)
                )
            )
        }
    }
}

private data class NavigationTabItem(
    val title: String,
    val icon: String,
    val destination: ScreenDestination.Main
)
