package com.techbrains.inteliq.config

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.techbrains.inteliq.model.UserRole
import java.util.Date

object JwtConfig {
    const val ISSUER = "inteliq-backend"
    const val AUDIENCE = "inteliq-client"
    private const val SECRET = "dev-secret-key-inteliq-dkt-platform"
    private val algorithm = Algorithm.HMAC256(SECRET)

    // Token Validity: Access Token 1 Hour, Refresh Token 7 Days
    private const val ACCESS_TOKEN_VALIDITY_MS = 3600_000L
    private const val REFRESH_TOKEN_VALIDITY_MS = 86400_000L * 7

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(ISSUER)
        .withAudience(AUDIENCE)
        .build()

    fun generateAccessToken(userId: String, email: String, role: UserRole): String {
        return JWT.create()
            .withSubject(userId)
            .withIssuer(ISSUER)
            .withAudience(AUDIENCE)
            .withClaim("email", email)
            .withClaim("role", role.name)
            .withExpiresAt(Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_MS))
            .sign(algorithm)
    }

    fun generateRefreshToken(userId: String): String {
        return JWT.create()
            .withSubject(userId)
            .withIssuer(ISSUER)
            .withAudience(AUDIENCE)
            .withClaim("type", "refresh")
            .withExpiresAt(Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY_MS))
            .sign(algorithm)
    }

    fun verifyRefreshToken(token: String): String? {
        return try {
            val jwt = verifier.verify(token)
            if (jwt.getClaim("type").asString() == "refresh") jwt.subject else null
        } catch (e: Exception) {
            null
        }
    }
}
