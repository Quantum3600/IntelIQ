package com.techbrains.inteliq.auth.repository

import com.techbrains.inteliq.auth.model.UserEntity
import java.util.concurrent.ConcurrentHashMap

interface UserRepository {
    suspend fun findByEmail(email: String): UserEntity?
    suspend fun findById(id: String): UserEntity?
    suspend fun save(user: UserEntity): UserEntity
}

class InMemoryUserRepository : UserRepository {
    private val usersById = ConcurrentHashMap<String, UserEntity>()
    private val usersByEmail = ConcurrentHashMap<String, UserEntity>()

    override suspend fun findByEmail(email: String): UserEntity? {
        return usersByEmail[email.lowercase()]
    }

    override suspend fun findById(id: String): UserEntity? {
        return usersById[id]
    }

    override suspend fun save(user: UserEntity): UserEntity {
        usersById[user.id] = user
        usersByEmail[user.email.lowercase()] = user
        return user
    }
}
