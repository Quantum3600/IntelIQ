package com.techbrains.inteliq.domain.model

import com.techbrains.inteliq.model.UserRole

data class User(
    val id: String,
    val email: String,
    val name: String,
    val role: UserRole
)
