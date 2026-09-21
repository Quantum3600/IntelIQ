package com.techbrains.inteliq.data.api

import com.techbrains.inteliq.data.api.model.AuthResponseDto
import com.techbrains.inteliq.data.api.model.LoginRequestDto
import com.techbrains.inteliq.data.api.model.RefreshTokenRequestDto
import com.techbrains.inteliq.data.api.model.RegisterRequestDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

interface AuthApi {
    suspend fun login(request: LoginRequestDto): AuthResponseDto
    suspend fun register(request: RegisterRequestDto): AuthResponseDto
    suspend fun refresh(request: RefreshTokenRequestDto): AuthResponseDto
    suspend fun logout(token: String)
}

class AuthApiImpl(
    private val client: HttpClient,
    private val baseUrl: String = "http://localhost:8080"
) : AuthApi {

    override suspend fun login(request: LoginRequestDto): AuthResponseDto {
        return client.post("$baseUrl/api/v1/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun register(request: RegisterRequestDto): AuthResponseDto {
        return client.post("$baseUrl/api/v1/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun refresh(request: RefreshTokenRequestDto): AuthResponseDto {
        return client.post("$baseUrl/api/v1/auth/refresh") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun logout(token: String) {
        client.post("$baseUrl/api/v1/auth/logout") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
    }
}
