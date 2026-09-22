package com.techbrains.inteliq.auth.model

import com.techbrains.inteliq.model.UserRole

data class UserEntity(
    val id: String,
    val email: String,
    val passwordHash: String,
    val name: String,
    val role: UserRole = UserRole.STUDENT,
    val createdAt: Long = System.currentTimeMillis()
)
