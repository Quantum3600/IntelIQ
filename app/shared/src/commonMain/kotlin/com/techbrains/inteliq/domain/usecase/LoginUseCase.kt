package com.techbrains.inteliq.domain.usecase

import com.techbrains.inteliq.domain.model.User
import com.techbrains.inteliq.domain.repository.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        if (email.isBlank()) {
            return Result.failure(IllegalArgumentException("Email cannot be empty"))
        }
        if (password.isBlank()) {
            return Result.failure(IllegalArgumentException("Password cannot be empty"))
        }
        return authRepository.login(email = email.trim(), password = password)
    }
}
