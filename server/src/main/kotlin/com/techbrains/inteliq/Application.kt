package com.techbrains.inteliq

import com.techbrains.inteliq.auth.controller.AuthController
import com.techbrains.inteliq.auth.repository.InMemoryUserRepository
import com.techbrains.inteliq.auth.route.authRouting
import com.techbrains.inteliq.auth.service.AuthService
import com.techbrains.inteliq.config.JwtConfig
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    // 1. Install ContentNegotiation with Kotlinx JSON
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            prettyPrint = true
        })
    }

    // 2. Install StatusPages for exception handling
    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to (cause.message ?: "Invalid request"))
            )
        }
        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf("error" to (cause.message ?: "Internal server error"))
            )
        }
    }

    // 3. Install JWT Authentication
    install(Authentication) {
        jwt("auth-jwt") {
            realm = JwtConfig.ISSUER
            verifier(JwtConfig.verifier)
            validate { credential ->
                if (credential.payload.subject.isNotBlank()) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }

    // 4. Instantiate Dependencies (4-Layer: Route -> Controller -> Service -> Repository)
    val userRepository = InMemoryUserRepository()
    val authService = AuthService(userRepository)
    val authController = AuthController(authService)

    // 5. Configure Routing
    routing {
        get("/") {
            call.respondText("IntelIQ Ktor Backend Server is running!")
        }

        authRouting(authController)
    }
}
