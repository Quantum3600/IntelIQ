package com.techbrains.inteliq.presentation

import com.techbrains.inteliq.domain.FakeAuthRepository
import com.techbrains.inteliq.domain.model.User
import com.techbrains.inteliq.domain.usecase.GetAuthStateUseCase
import com.techbrains.inteliq.domain.usecase.LoginUseCase
import com.techbrains.inteliq.domain.usecase.RegisterUseCase
import com.techbrains.inteliq.model.UserRole
import com.techbrains.inteliq.presentation.auth.AuthIntent
import com.techbrains.inteliq.presentation.auth.AuthViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun intentEmailAndPasswordChangedUpdatesUiState() = runTest {
        val fakeRepo = FakeAuthRepository()
        val viewModel = AuthViewModel(
            LoginUseCase(fakeRepo),
            RegisterUseCase(fakeRepo),
            GetAuthStateUseCase(fakeRepo)
        )

        viewModel.onIntent(AuthIntent.EmailChanged("student@inteliq.com"))
        viewModel.onIntent(AuthIntent.PasswordChanged("secret123"))

        assertEquals("student@inteliq.com", viewModel.uiState.value.email)
        assertEquals("secret123", viewModel.uiState.value.password)
    }

    @Test
    fun toggleAuthModeSwitchesMode() = runTest {
        val fakeRepo = FakeAuthRepository()
        val viewModel = AuthViewModel(
            LoginUseCase(fakeRepo),
            RegisterUseCase(fakeRepo),
            GetAuthStateUseCase(fakeRepo)
        )

        assertFalse(viewModel.uiState.value.isRegistering)
        viewModel.onIntent(AuthIntent.ToggleAuthMode)
        assertTrue(viewModel.uiState.value.isRegistering)
    }

    @Test
    fun submitAuthFailureSetsErrorMessage() = runTest {
        val fakeRepo = FakeAuthRepository()
        fakeRepo.loginResult = Result.failure(RuntimeException("Bad credentials"))

        val viewModel = AuthViewModel(
            LoginUseCase(fakeRepo),
            RegisterUseCase(fakeRepo),
            GetAuthStateUseCase(fakeRepo)
        )

        viewModel.onIntent(AuthIntent.EmailChanged("student@inteliq.com"))
        viewModel.onIntent(AuthIntent.PasswordChanged("wrong"))
        viewModel.onIntent(AuthIntent.SubmitAuth)

        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("Bad credentials", viewModel.uiState.value.errorMessage)
        assertNull(viewModel.uiState.value.authenticatedUser)
    }

    @Test
    fun submitAuthSuccessUpdatesUser() = runTest {
        val fakeRepo = FakeAuthRepository()
        val expectedUser = User("s123", "student@inteliq.com", "Student One", UserRole.STUDENT)
        fakeRepo.loginResult = Result.success(expectedUser)

        val viewModel = AuthViewModel(
            LoginUseCase(fakeRepo),
            RegisterUseCase(fakeRepo),
            GetAuthStateUseCase(fakeRepo)
        )

        viewModel.onIntent(AuthIntent.EmailChanged("student@inteliq.com"))
        viewModel.onIntent(AuthIntent.PasswordChanged("validPass"))
        viewModel.onIntent(AuthIntent.SubmitAuth)

        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(expectedUser, viewModel.uiState.value.authenticatedUser)
        assertNull(viewModel.uiState.value.errorMessage)
    }
}
