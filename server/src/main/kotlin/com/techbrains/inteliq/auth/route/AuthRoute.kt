package com.techbrains.inteliq.auth.route

import com.techbrains.inteliq.auth.controller.AuthController
import com.techbrains.inteliq.auth.dto.LoginRequest
import com.techbrains.inteliq.auth.dto.RefreshTokenRequest
import com.techbrains.inteliq.auth.dto.RegisterRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.authRouting(authController: AuthController) {
    route("/api/v1/auth") {
        post("/register") {
            val request = call.receive<RegisterRequest>()
            val response = authController.register(request)
            call.respond(HttpStatusCode.Created, response)
        }

        post("/login") {
            val request = call.receive<LoginRequest>()
            val response = authController.login(request)
            call.respond(HttpStatusCode.OK, response)
        }

        post("/refresh") {
            val request = call.receive<RefreshTokenRequest>()
            val response = authController.refresh(request)
            call.respond(HttpStatusCode.OK, response)
        }

        post("/logout") {
            call.respond(HttpStatusCode.OK, mapOf("message" to "Logged out successfully"))
        }
    }
}
