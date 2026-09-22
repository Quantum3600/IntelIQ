package com.techbrains.inteliq.auth.dto

import com.techbrains.inteliq.model.UserRole
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    val role: UserRole = UserRole.STUDENT
)

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
)

@Serializable
data class UserResponse(
    val id: String,
    val email: String,
    val name: String,
    val role: UserRole
)

@Serializable
data class AuthResponse(
    val token: String,
    val refreshToken: String,
    val user: UserResponse
)
