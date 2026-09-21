package com.techbrains.inteliq.data

import com.techbrains.inteliq.data.api.AuthApi
import com.techbrains.inteliq.data.api.model.AuthResponseDto
import com.techbrains.inteliq.data.api.model.LoginRequestDto
import com.techbrains.inteliq.data.api.model.RefreshTokenRequestDto
import com.techbrains.inteliq.data.api.model.RegisterRequestDto
import com.techbrains.inteliq.data.api.model.UserDto
import com.techbrains.inteliq.data.local.InMemoryTokenStorage
import com.techbrains.inteliq.data.local.InMemoryUserSessionLocalDataSource
import com.techbrains.inteliq.data.repository.AuthRepositoryImpl
import com.techbrains.inteliq.domain.model.AuthState
import com.techbrains.inteliq.model.UserRole
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FakeAuthApi : AuthApi {
    var loginResult: Result<AuthResponseDto>? = null
    var registerResult: Result<AuthResponseDto>? = null
    var refreshResult: Result<AuthResponseDto>? = null
    var loggedOut = false

    override suspend fun login(request: LoginRequestDto): AuthResponseDto {
        return loginResult?.getOrThrow() ?: throw IllegalStateException("Login fake failed")
    }

    override suspend fun register(request: RegisterRequestDto): AuthResponseDto {
        return registerResult?.getOrThrow() ?: throw IllegalStateException("Register fake failed")
    }

    override suspend fun refresh(request: RefreshTokenRequestDto): AuthResponseDto {
        return refreshResult?.getOrThrow() ?: throw IllegalStateException("Refresh fake failed")
    }

    override suspend fun logout(token: String) {
        loggedOut = true
    }
}

class AuthRepositoryTest {

    @Test
    fun loginSuccessSavesTokensAndUpdatesAuthState() = runTest {
        val fakeApi = FakeAuthApi()
        val tokenStorage = InMemoryTokenStorage()
        val sessionDataSource = InMemoryUserSessionLocalDataSource()
        val repository = AuthRepositoryImpl(fakeApi, tokenStorage, sessionDataSource)

        val userDto = UserDto("u1", "test@test.com", "Test User", UserRole.STUDENT)
        fakeApi.loginResult = Result.success(
            AuthResponseDto("access_token_123", "refresh_token_456", userDto)
        )

        val result = repository.login("test@test.com", "password")

        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertEquals("u1", user?.id)
        assertEquals("test@test.com", user?.email)
        assertEquals("access_token_123", tokenStorage.getAccessToken())
        assertEquals("refresh_token_456", tokenStorage.getRefreshToken())
        assertEquals(userDto, sessionDataSource.currentUser.value)
        assertTrue(repository.authState.value is AuthState.Authenticated)
    }

    @Test
    fun loginFailureReturnsErrorResult() = runTest {
        val fakeApi = FakeAuthApi()
        val tokenStorage = InMemoryTokenStorage()
        val sessionDataSource = InMemoryUserSessionLocalDataSource()
        val repository = AuthRepositoryImpl(fakeApi, tokenStorage, sessionDataSource)

        fakeApi.loginResult = Result.failure(RuntimeException("Invalid credentials"))

        val result = repository.login("test@test.com", "wrong_password")

        assertTrue(result.isFailure)
        assertNull(tokenStorage.getAccessToken())
        assertTrue(repository.authState.value is AuthState.Unauthenticated)
    }

    @Test
    fun logoutClearsTokensAndSession() = runTest {
        val fakeApi = FakeAuthApi()
        val tokenStorage = InMemoryTokenStorage()
        val sessionDataSource = InMemoryUserSessionLocalDataSource()
        val repository = AuthRepositoryImpl(fakeApi, tokenStorage, sessionDataSource)

        tokenStorage.saveAccessToken("access_token_123")
        tokenStorage.saveRefreshToken("refresh_token_456")

        val logoutResult = repository.logout()

        assertTrue(logoutResult.isSuccess)
        assertTrue(fakeApi.loggedOut)
        assertNull(tokenStorage.getAccessToken())
        assertNull(tokenStorage.getRefreshToken())
        assertNull(sessionDataSource.currentUser.value)
        assertTrue(repository.authState.value is AuthState.Unauthenticated)
    }
}
