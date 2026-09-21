package com.techbrains.inteliq.domain.model

sealed interface AuthState {
    data object Unauthenticated : AuthState
    data object Loading : AuthState
    data class Authenticated(val user: User) : AuthState
}
