package com.techbrains.inteliq.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

sealed interface ScreenDestination {
    data object Splash : ScreenDestination
    data object Auth : ScreenDestination

    sealed interface Main : ScreenDestination {
        data object Home : Main          // dashboard
        data object Learn : Main         // courses / mastery map
        data object Discover : Main      // recommendations / analytics / explore
        data object Practice : Main      // quiz / arena
        data object Profile : Main       // profile / settings
    }

    data class CourseDetail(val courseId: String) : ScreenDestination
    data class TopicDetail(val topicId: String) : ScreenDestination
    data class Question(val questionId: String) : ScreenDestination
    data class AnswerFeedback(val attemptId: String) : ScreenDestination
}

class Navigator(initialDestination: ScreenDestination = ScreenDestination.Auth) {
    var currentDestination by mutableStateOf<ScreenDestination>(initialDestination)
        private set

    var currentMainTab by mutableStateOf<ScreenDestination.Main>(ScreenDestination.Main.Home)
        private set

    fun navigateTo(destination: ScreenDestination) {
        if (destination is ScreenDestination.Main) {
            currentMainTab = destination
        }
        currentDestination = destination
    }
}

@Composable
fun rememberNavigator(initialDestination: ScreenDestination = ScreenDestination.Auth): Navigator {
    return remember { Navigator(initialDestination) }
}
