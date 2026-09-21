package com.techbrains.inteliq.domain.usecase

import com.techbrains.inteliq.domain.model.AuthState
import com.techbrains.inteliq.domain.repository.AuthRepository
import kotlinx.coroutines.flow.StateFlow

class GetAuthStateUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): StateFlow<AuthState> {
        return authRepository.authState
    }
}
