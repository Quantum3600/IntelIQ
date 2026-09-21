package com.techbrains.inteliq.domain.usecase

import com.techbrains.inteliq.domain.model.User
import com.techbrains.inteliq.domain.repository.AuthRepository
import com.techbrains.inteliq.model.UserRole

class RegisterUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        name: String,
        role: UserRole = UserRole.STUDENT
    ): Result<User> {
        if (email.isBlank()) {
            return Result.failure(IllegalArgumentException("Email cannot be empty"))
        }
        if (password.isBlank()) {
            return Result.failure(IllegalArgumentException("Password cannot be empty"))
        }
        if (name.isBlank()) {
            return Result.failure(IllegalArgumentException("Name cannot be empty"))
        }
        return authRepository.register(
            email = email.trim(),
            password = password,
            name = name.trim(),
            role = role
        )
    }
}
