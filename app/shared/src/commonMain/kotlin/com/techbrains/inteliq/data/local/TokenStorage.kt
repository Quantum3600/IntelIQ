package com.techbrains.inteliq.data.local

interface TokenStorage {
    suspend fun getAccessToken(): String?
    suspend fun saveAccessToken(token: String)
    suspend fun getRefreshToken(): String?
    suspend fun saveRefreshToken(token: String)
    suspend fun clear()
}

class InMemoryTokenStorage : TokenStorage {
    private var accessToken: String? = null
    private var refreshToken: String? = null

    override suspend fun getAccessToken(): String? = accessToken

    override suspend fun saveAccessToken(token: String) {
        accessToken = token
    }

    override suspend fun getRefreshToken(): String? = refreshToken

    override suspend fun saveRefreshToken(token: String) {
        refreshToken = token
    }

    override suspend fun clear() {
        accessToken = null
        refreshToken = null
    }
}
