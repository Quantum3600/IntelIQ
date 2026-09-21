package com.techbrains.inteliq.model

import kotlinx.serialization.Serializable

@Serializable
enum class UserRole {
    STUDENT,
    TEACHER,
    ADMIN
}
