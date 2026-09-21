package com.techbrains.inteliq.data.repository

import com.techbrains.inteliq.data.api.AuthApi
import com.techbrains.inteliq.data.api.model.LoginRequestDto
import com.techbrains.inteliq.data.api.model.RefreshTokenRequestDto
import com.techbrains.inteliq.data.api.model.RegisterRequestDto
import com.techbrains.inteliq.data.api.model.UserDto
import com.techbrains.inteliq.data.local.TokenStorage
import com.techbrains.inteliq.data.local.UserSessionLocalDataSource
import com.techbrains.inteliq.domain.model.AuthState
import com.techbrains.inteliq.domain.model.User
import com.techbrains.inteliq.domain.repository.AuthRepository
import com.techbrains.inteliq.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val tokenStorage: TokenStorage,
    private val userSessionLocalDataSource: UserSessionLocalDataSource
) : AuthRepository {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    override suspend fun login(email: String, password: String): Result<User> {
        return runCatching {
            val response = authApi.login(LoginRequestDto(email = email, password = password))
            tokenStorage.saveAccessToken(response.token)
            tokenStorage.saveRefreshToken(response.refreshToken)
            userSessionLocalDataSource.saveUser(response.user)
            val user = response.user.toDomain()
            _authState.value = AuthState.Authenticated(user)
            user
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        name: String,
        role: UserRole
    ): Result<User> {
        return runCatching {
            val response = authApi.register(
                RegisterRequestDto(email = email, password = password, name = name, role = role)
            )
            tokenStorage.saveAccessToken(response.token)
            tokenStorage.saveRefreshToken(response.refreshToken)
            userSessionLocalDataSource.saveUser(response.user)
            val user = response.user.toDomain()
            _authState.value = AuthState.Authenticated(user)
            user
        }
    }

    override suspend fun logout(): Result<Unit> {
        return runCatching {
            val currentToken = tokenStorage.getAccessToken()
            if (currentToken != null) {
                runCatching { authApi.logout(currentToken) }
            }
            tokenStorage.clear()
            userSessionLocalDataSource.clearSession()
            _authState.value = AuthState.Unauthenticated
        }
    }

    override suspend fun refreshToken(): Result<Unit> {
        return runCatching {
            val refreshToken = tokenStorage.getRefreshToken()
                ?: throw IllegalStateException("No refresh token available")
            val response = authApi.refresh(RefreshTokenRequestDto(refreshToken))
            tokenStorage.saveAccessToken(response.token)
            tokenStorage.saveRefreshToken(response.refreshToken)
        }
    }

    override suspend fun getAccessToken(): String? {
        return tokenStorage.getAccessToken()
    }

    private fun UserDto.toDomain(): User {
        return User(
            id = id,
            email = email,
            name = name,
            role = role
        )
    }
}
