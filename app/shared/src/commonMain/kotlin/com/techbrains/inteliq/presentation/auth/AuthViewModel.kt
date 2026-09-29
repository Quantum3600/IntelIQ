package com.techbrains.inteliq.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techbrains.inteliq.domain.model.AuthState
import com.techbrains.inteliq.domain.model.User
import com.techbrains.inteliq.model.UserRole
import com.techbrains.inteliq.domain.usecase.GetAuthStateUseCase
import com.techbrains.inteliq.domain.usecase.LoginUseCase
import com.techbrains.inteliq.domain.usecase.RegisterUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val getAuthStateUseCase: GetAuthStateUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<AuthSingleEvent>()
    val eventFlow: SharedFlow<AuthSingleEvent> = _eventFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            getAuthStateUseCase().collect { authState ->
                when (authState) {
                    is AuthState.Authenticated -> {
                        _uiState.update { it.copy(authenticatedUser = authState.user, isLoading = false) }
                        _eventFlow.emit(AuthSingleEvent.NavigateToHome(authState.user))
                    }
                    is AuthState.Unauthenticated -> {
                        _uiState.update { it.copy(authenticatedUser = null, isLoading = false) }
                    }
                    is AuthState.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    fun onIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.EmailChanged -> {
                _uiState.update { it.copy(email = intent.email, errorMessage = null) }
            }
            is AuthIntent.PasswordChanged -> {
                _uiState.update { it.copy(password = intent.password, errorMessage = null) }
            }
            is AuthIntent.NameChanged -> {
                _uiState.update { it.copy(name = intent.name, errorMessage = null) }
            }
            is AuthIntent.RoleChanged -> {
                _uiState.update { it.copy(role = intent.role, errorMessage = null) }
            }
            is AuthIntent.ToggleAuthMode -> {
                _uiState.update {
                    it.copy(
                        isRegistering = !it.isRegistering,
                        errorMessage = null
                    )
                }
            }
            is AuthIntent.SubmitAuth -> {
                submitAuth()
            }
            is AuthIntent.DebugSkipAuth -> {
                val debugUser = User(
                    id = "debug-001",
                    email = "student@inteliq.edu",
                    name = "Debug Student",
                    role = UserRole.STUDENT
                )
                _uiState.update { it.copy(isLoading = false, authenticatedUser = debugUser, errorMessage = null) }
                viewModelScope.launch {
                    _eventFlow.emit(AuthSingleEvent.NavigateToHome(debugUser))
                }
            }
            is AuthIntent.ClearError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
        }
    }

    private fun submitAuth() {
        val currentState = _uiState.value
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = if (currentState.isRegistering) {
                registerUseCase(
                    email = currentState.email,
                    password = currentState.password,
                    name = currentState.name,
                    role = currentState.role
                )
            } else {
                loginUseCase(
                    email = currentState.email,
                    password = currentState.password
                )
            }

            result.fold(
                onSuccess = { user ->
                    _uiState.update { it.copy(isLoading = false, authenticatedUser = user) }
                    _eventFlow.emit(AuthSingleEvent.NavigateToHome(user))
                },
                onFailure = { throwable ->
                    val errorMsg = throwable.message ?: "Authentication failed"
                    _uiState.update { it.copy(isLoading = false, errorMessage = errorMsg) }
                    _eventFlow.emit(AuthSingleEvent.ShowToast(errorMsg))
                }
            )
        }
    }
}
