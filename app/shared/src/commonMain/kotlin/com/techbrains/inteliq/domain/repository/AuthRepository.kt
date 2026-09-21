package com.techbrains.inteliq.domain.repository

import com.techbrains.inteliq.domain.model.AuthState
import com.techbrains.inteliq.domain.model.User
import com.techbrains.inteliq.model.UserRole
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val authState: StateFlow<AuthState>
    
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(email: String, password: String, name: String, role: UserRole): Result<User>
    suspend fun logout(): Result<Unit>
    suspend fun refreshToken(): Result<Unit>
    suspend fun getAccessToken(): String?
}
