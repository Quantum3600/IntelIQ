package com.techbrains.inteliq.data.api.model

import com.techbrains.inteliq.model.UserRole
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequestDto(
    val email: String,
    val password: String,
    val name: String,
    val role: UserRole = UserRole.STUDENT
)

@Serializable
data class RefreshTokenRequestDto(
    val refreshToken: String
)

@Serializable
data class UserDto(
    val id: String,
    val email: String,
    val name: String,
    val role: UserRole
)

@Serializable
data class AuthResponseDto(
    val token: String,
    val refreshToken: String,
    val user: UserDto
)
