package com.techbrains.inteliq.presentation.auth

import com.techbrains.inteliq.domain.model.User
import com.techbrains.inteliq.model.UserRole

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val role: UserRole = UserRole.STUDENT,
    val isRegistering: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val authenticatedUser: User? = null
)

sealed interface AuthIntent {
    data class EmailChanged(val email: String) : AuthIntent
    data class PasswordChanged(val password: String) : AuthIntent
    data class NameChanged(val name: String) : AuthIntent
    data class RoleChanged(val role: UserRole) : AuthIntent
    data object ToggleAuthMode : AuthIntent
    data object SubmitAuth : AuthIntent
    data object ClearError : AuthIntent
}

sealed interface AuthSingleEvent {
    data class NavigateToHome(val user: User) : AuthSingleEvent
    data class ShowToast(val message: String) : AuthSingleEvent
}
