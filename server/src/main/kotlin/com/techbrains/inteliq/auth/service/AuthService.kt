package com.techbrains.inteliq.auth.service

import at.favre.lib.crypto.bcrypt.BCrypt
import com.techbrains.inteliq.auth.model.UserEntity
import com.techbrains.inteliq.auth.repository.UserRepository
import com.techbrains.inteliq.config.JwtConfig
import com.techbrains.inteliq.model.UserRole
import java.util.UUID

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
    val user: UserEntity
)

class AuthService(
    private val userRepository: UserRepository
) {

    suspend fun register(
        email: String,
        password: String,
        name: String,
        role: UserRole = UserRole.STUDENT
    ): AuthTokens {
        val trimmedEmail = email.trim().lowercase()
        val existing = userRepository.findByEmail(trimmedEmail)
        if (existing != null) {
            throw IllegalArgumentException("Email already registered: $trimmedEmail")
        }

        val hashedPassword = BCrypt.withDefaults().hashToString(12, password.toCharArray())
        val newUser = UserEntity(
            id = UUID.randomUUID().toString(),
            email = trimmedEmail,
            passwordHash = hashedPassword,
            name = name.trim(),
            role = role
        )

        val savedUser = userRepository.save(newUser)
        val accessToken = JwtConfig.generateAccessToken(savedUser.id, savedUser.email, savedUser.role)
        val refreshToken = JwtConfig.generateRefreshToken(savedUser.id)

        return AuthTokens(
            accessToken = accessToken,
            refreshToken = refreshToken,
            user = savedUser
        )
    }

    suspend fun login(email: String, password: String): AuthTokens {
        val trimmedEmail = email.trim().lowercase()
        val user = userRepository.findByEmail(trimmedEmail)
            ?: throw IllegalArgumentException("Invalid credentials")

        val result = BCrypt.verifyer().verify(password.toCharArray(), user.passwordHash)
        if (!result.verified) {
            throw IllegalArgumentException("Invalid credentials")
        }

        val accessToken = JwtConfig.generateAccessToken(user.id, user.email, user.role)
        val refreshToken = JwtConfig.generateRefreshToken(user.id)

        return AuthTokens(
            accessToken = accessToken,
            refreshToken = refreshToken,
            user = user
        )
    }

    suspend fun refreshToken(refreshToken: String): AuthTokens {
        val userId = JwtConfig.verifyRefreshToken(refreshToken)
            ?: throw IllegalArgumentException("Invalid or expired refresh token")

        val user = userRepository.findById(userId)
            ?: throw IllegalArgumentException("User not found")

        val newAccessToken = JwtConfig.generateAccessToken(user.id, user.email, user.role)
        val newRefreshToken = JwtConfig.generateRefreshToken(user.id)

        return AuthTokens(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken,
            user = user
        )
    }
}
