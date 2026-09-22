package com.techbrains.inteliq.auth.controller

import com.techbrains.inteliq.auth.dto.AuthResponse
import com.techbrains.inteliq.auth.dto.LoginRequest
import com.techbrains.inteliq.auth.dto.RefreshTokenRequest
import com.techbrains.inteliq.auth.dto.RegisterRequest
import com.techbrains.inteliq.auth.dto.UserResponse
import com.techbrains.inteliq.auth.service.AuthService

class AuthController(
    private val authService: AuthService
) {

    suspend fun register(request: RegisterRequest): AuthResponse {
        if (request.email.isBlank()) throw IllegalArgumentException("Email is required")
        if (request.password.isBlank()) throw IllegalArgumentException("Password is required")
        if (request.name.isBlank()) throw IllegalArgumentException("Name is required")

        val authTokens = authService.register(
            email = request.email,
            password = request.password,
            name = request.name,
            role = request.role
        )

        return AuthResponse(
            token = authTokens.accessToken,
            refreshToken = authTokens.refreshToken,
            user = UserResponse(
                id = authTokens.user.id,
                email = authTokens.user.email,
                name = authTokens.user.name,
                role = authTokens.user.role
            )
        )
    }

    suspend fun login(request: LoginRequest): AuthResponse {
        if (request.email.isBlank()) throw IllegalArgumentException("Email is required")
        if (request.password.isBlank()) throw IllegalArgumentException("Password is required")

        val authTokens = authService.login(
            email = request.email,
            password = request.password
        )

        return AuthResponse(
            token = authTokens.accessToken,
            refreshToken = authTokens.refreshToken,
            user = UserResponse(
                id = authTokens.user.id,
                email = authTokens.user.email,
                name = authTokens.user.name,
                role = authTokens.user.role
            )
        )
    }

    suspend fun refresh(request: RefreshTokenRequest): AuthResponse {
        if (request.refreshToken.isBlank()) throw IllegalArgumentException("Refresh token is required")

        val authTokens = authService.refreshToken(request.refreshToken)

        return AuthResponse(
            token = authTokens.accessToken,
            refreshToken = authTokens.refreshToken,
            user = UserResponse(
                id = authTokens.user.id,
                email = authTokens.user.email,
                name = authTokens.user.name,
                role = authTokens.user.role
            )
        )
    }
}
