package com.techbrains.inteliq

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.techbrains.inteliq.presentation.navigation.AdaptiveNavigationLayout
import com.techbrains.inteliq.presentation.navigation.ScreenDestination
import com.techbrains.inteliq.presentation.navigation.rememberNavigator
import com.techbrains.inteliq.presentation.profile.ProfileScreen
import com.techbrains.inteliq.presentation.progress.ProgressScreen
import com.techbrains.inteliq.presentation.quiz.QuizScreen
import com.techbrains.inteliq.presentation.splash.SplashScreen
import com.techbrains.inteliq.presentation.theme.DarkThemeMode
import com.techbrains.inteliq.presentation.theme.IntelIQTheme
import com.techbrains.inteliq.presentation.theme.ThemeState
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

@Composable
fun App() {
    var themeState by remember { mutableStateOf(ThemeState()) }

    val isDark = when (themeState.darkThemeMode) {
        DarkThemeMode.SYSTEM -> isSystemInDarkTheme()
        DarkThemeMode.LIGHT -> false
        DarkThemeMode.DARK -> true
    }

    IntelIQTheme(
        seedColor = themeState.seedColor,
        darkTheme = isDark,
        style = themeState.paletteStyle
    ) {
        val navigator = rememberNavigator(initialDestination = ScreenDestination.Auth)

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

        val topLevelTarget = remember(navigator.currentDestination) {
            if (navigator.currentDestination is ScreenDestination.Main) "MAIN_SHELL"
            else navigator.currentDestination
        }

        AnimatedContent(
            targetState = topLevelTarget,
            transitionSpec = { fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(250)) },
            label = "ScreenTransition"
        ) { _ ->
            val destination = navigator.currentDestination
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
                            if ((intent is AuthIntent.SubmitAuth || intent is AuthIntent.DebugSkipAuth) && authState.authenticatedUser != null) {
                                navigator.navigateTo(ScreenDestination.Main.Home)
                            }
                        },
                        isDarkTheme = isDark,
                        onToggleDarkTheme = {
                            val nextMode = if (isDark) DarkThemeMode.LIGHT else DarkThemeMode.DARK
                            themeState = themeState.copy(darkThemeMode = nextMode)
                        }
                    )
                }

                else -> {
                    MainAppShell(
                        currentTab = navigator.currentMainTab,
                        userName = authState.authenticatedUser?.name ?: "Student",
                        userEmail = authState.authenticatedUser?.email ?: "student@inteliq.edu",
                        themeState = themeState,
                        onUpdateThemeState = { themeState = it },
                        onTabSelected = { tab ->
                            navigator.navigateTo(tab)
                        },
                        onLogout = {
                            authViewModel.onIntent(AuthIntent.ClearError)
                            navigator.navigateTo(ScreenDestination.Auth)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MainAppShell(
    currentTab: ScreenDestination.Main,
    userName: String,
    userEmail: String,
    themeState: ThemeState,
    onUpdateThemeState: (ThemeState) -> Unit,
    onTabSelected: (ScreenDestination.Main) -> Unit,
    onLogout: () -> Unit
) {
    val isDark = when (themeState.darkThemeMode) {
        DarkThemeMode.SYSTEM -> isSystemInDarkTheme()
        DarkThemeMode.LIGHT -> false
        DarkThemeMode.DARK -> true
    }

    AdaptiveNavigationLayout(
        currentTab = currentTab,
        onTabSelected = onTabSelected,
        onLogout = onLogout,
        userName = userName,
        userEmail = userEmail,
        isDarkTheme = isDark,
        onToggleDarkTheme = {
            val nextMode = if (isDark) DarkThemeMode.LIGHT else DarkThemeMode.DARK
            onUpdateThemeState(themeState.copy(darkThemeMode = nextMode))
        }
    ) {
        MainTabContent(
            currentTab = currentTab,
            themeState = themeState,
            onUpdateThemeState = onUpdateThemeState,
            onLogout = onLogout
        )
    }
}

private fun getTabOrder(tab: ScreenDestination.Main): Int = when (tab) {
    is ScreenDestination.Main.Home -> 0
    is ScreenDestination.Main.Learn -> 1
    is ScreenDestination.Main.Discover -> 2
    is ScreenDestination.Main.Practice -> 3
    is ScreenDestination.Main.Profile -> 4
}

@Composable
private fun MainTabContent(
    currentTab: ScreenDestination.Main,
    themeState: ThemeState,
    onUpdateThemeState: (ThemeState) -> Unit,
    onLogout: () -> Unit
) {
    AnimatedContent(
        targetState = currentTab,
        transitionSpec = {
            val isForward = getTabOrder(targetState) > getTabOrder(initialState)
            val directionMultiplier = if (isForward) 1 else -1

            val enterSlide = slideInHorizontally(
                animationSpec = tween(220, easing = FastOutSlowInEasing)
            ) { fullWidth -> directionMultiplier * (fullWidth / 6) }

            val exitSlide = slideOutHorizontally(
                animationSpec = tween(180, easing = FastOutSlowInEasing)
            ) { fullWidth -> -directionMultiplier * (fullWidth / 6) }

            (fadeIn(animationSpec = tween(220, easing = FastOutSlowInEasing)) + enterSlide)
                .togetherWith(
                    fadeOut(animationSpec = tween(180, easing = FastOutSlowInEasing)) + exitSlide
                )
        },
        label = "MainTabTransition"
    ) { tab ->
        when (tab) {
            ScreenDestination.Main.Home -> DashboardScreen()
            ScreenDestination.Main.Learn -> CoursesScreen()
            ScreenDestination.Main.Discover -> ProgressScreen()
            ScreenDestination.Main.Practice -> QuizScreen()
            ScreenDestination.Main.Profile -> ProfileScreen(
                themeState = themeState,
                onUpdateThemeState = onUpdateThemeState,
                onLogout = onLogout
            )
        }
    }
}
