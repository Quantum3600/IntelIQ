package com.techbrains.inteliq.domain

import com.techbrains.inteliq.domain.model.AuthState
import com.techbrains.inteliq.domain.model.User
import com.techbrains.inteliq.domain.repository.AuthRepository
import com.techbrains.inteliq.domain.usecase.GetAuthStateUseCase
import com.techbrains.inteliq.domain.usecase.LoginUseCase
import com.techbrains.inteliq.domain.usecase.LogoutUseCase
import com.techbrains.inteliq.domain.usecase.RegisterUseCase
import com.techbrains.inteliq.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FakeAuthRepository : AuthRepository {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    override val authState: StateFlow<AuthState> = _authState

    var loginResult: Result<User>? = null
    var registerResult: Result<User>? = null
    var logoutResult: Result<Unit>? = null

    override suspend fun login(email: String, password: String): Result<User> {
        return loginResult ?: Result.failure(IllegalStateException("No login result configured"))
    }

    override suspend fun register(
        email: String,
        password: String,
        name: String,
        role: UserRole
    ): Result<User> {
        return registerResult ?: Result.failure(IllegalStateException("No register result configured"))
    }

    override suspend fun logout(): Result<Unit> {
        return logoutResult ?: Result.success(Unit)
    }

    override suspend fun refreshToken(): Result<Unit> = Result.success(Unit)

    override suspend fun getAccessToken(): String? = null
}

class AuthUseCasesTest {

    @Test
    fun loginUseCaseValidatesInputs() = runTest {
        val fakeRepo = FakeAuthRepository()
        val loginUseCase = LoginUseCase(fakeRepo)

        val emptyEmailRes = loginUseCase("", "password")
        assertTrue(emptyEmailRes.isFailure)
        assertEquals("Email cannot be empty", emptyEmailRes.exceptionOrNull()?.message)

        val emptyPasswordRes = loginUseCase("user@test.com", "  ")
        assertTrue(emptyPasswordRes.isFailure)
        assertEquals("Password cannot be empty", emptyPasswordRes.exceptionOrNull()?.message)
    }

    @Test
    fun loginUseCaseDelegatesToRepositoryOnValidInputs() = runTest {
        val fakeRepo = FakeAuthRepository()
        val expectedUser = User("u123", "user@test.com", "Jane Doe", UserRole.STUDENT)
        fakeRepo.loginResult = Result.success(expectedUser)

        val loginUseCase = LoginUseCase(fakeRepo)
        val result = loginUseCase("user@test.com", "validPass123")

        assertTrue(result.isSuccess)
        assertEquals(expectedUser, result.getOrNull())
    }

    @Test
    fun registerUseCaseValidatesInputs() = runTest {
        val fakeRepo = FakeAuthRepository()
        val registerUseCase = RegisterUseCase(fakeRepo)

        val emptyNameRes = registerUseCase("user@test.com", "pass123", "")
        assertTrue(emptyNameRes.isFailure)
        assertEquals("Name cannot be empty", emptyNameRes.exceptionOrNull()?.message)
    }

    @Test
    fun logoutAndGetAuthStateUseCases() = runTest {
        val fakeRepo = FakeAuthRepository()
        val logoutUseCase = LogoutUseCase(fakeRepo)
        val getAuthStateUseCase = GetAuthStateUseCase(fakeRepo)

        assertEquals(AuthState.Unauthenticated, getAuthStateUseCase().value)

        val logoutRes = logoutUseCase()
        assertTrue(logoutRes.isSuccess)
    }
}
