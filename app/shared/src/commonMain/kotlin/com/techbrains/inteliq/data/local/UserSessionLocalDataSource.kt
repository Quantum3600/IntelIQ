package com.techbrains.inteliq.data.local

import com.techbrains.inteliq.data.api.model.UserDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface UserSessionLocalDataSource {
    val currentUser: StateFlow<UserDto?>
    suspend fun saveUser(user: UserDto)
    suspend fun clearSession()
}

class InMemoryUserSessionLocalDataSource : UserSessionLocalDataSource {
    private val _currentUser = MutableStateFlow<UserDto?>(null)
    override val currentUser: StateFlow<UserDto?> = _currentUser.asStateFlow()

    override suspend fun saveUser(user: UserDto) {
        _currentUser.value = user
    }

    override suspend fun clearSession() {
        _currentUser.value = null
    }
}
