package com.techbrains.inteliq

import com.techbrains.inteliq.auth.repository.InMemoryUserRepository
import com.techbrains.inteliq.auth.service.AuthService
import com.techbrains.inteliq.model.UserRole
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AuthServiceTest {

    @Test
    fun registerNewUserSucceedsAndHashesPassword() {
        runBlocking {
            val repository = InMemoryUserRepository()
            val authService = AuthService(repository)

            val tokens = authService.register(
                email = "student@inteliq.com",
                password = "SecretPassword123",
                name = "Jane Student",
                role = UserRole.STUDENT
            )

            assertNotNull(tokens.accessToken)
            assertNotNull(tokens.refreshToken)
            assertEquals("student@inteliq.com", tokens.user.email)
            assertEquals("Jane Student", tokens.user.name)
            assertEquals(UserRole.STUDENT, tokens.user.role)

            val savedUser = repository.findByEmail("student@inteliq.com")
            assertNotNull(savedUser)
            assertTrue(savedUser.passwordHash != "SecretPassword123")
        }
    }

    @Test
    fun duplicateRegistrationFails() {
        runBlocking {
            val repository = InMemoryUserRepository()
            val authService = AuthService(repository)

            authService.register("student@inteliq.com", "pass123", "User One")

            assertFailsWith<IllegalArgumentException> {
                authService.register("student@inteliq.com", "pass456", "User Two")
            }
        }
    }

    @Test
    fun loginWithCorrectPasswordSucceeds() {
        runBlocking {
            val repository = InMemoryUserRepository()
            val authService = AuthService(repository)

            authService.register("student@inteliq.com", "SecretPassword123", "Jane Student")

            val loginTokens = authService.login("student@inteliq.com", "SecretPassword123")

            assertNotNull(loginTokens.accessToken)
            assertNotNull(loginTokens.refreshToken)
            assertEquals("student@inteliq.com", loginTokens.user.email)
        }
    }

    @Test
    fun loginWithWrongPasswordFails() {
        runBlocking {
            val repository = InMemoryUserRepository()
            val authService = AuthService(repository)

            authService.register("student@inteliq.com", "SecretPassword123", "Jane Student")

            assertFailsWith<IllegalArgumentException> {
                authService.login("student@inteliq.com", "WrongPassword")
            }
        }
    }
}
