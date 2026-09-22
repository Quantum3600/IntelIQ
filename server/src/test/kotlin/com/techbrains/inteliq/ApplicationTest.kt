package com.techbrains.inteliq

import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApplicationTest {

    @Test
    fun testRoot() = testApplication {
        application {
            module()
        }
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("IntelIQ Ktor Backend Server is running!", response.bodyAsText())
    }

    @Test
    fun testRegisterAndLoginApiEndpoints() = testApplication {
        application {
            module()
        }

        // 1. Register
        val registerResponse = client.post("/api/v1/auth/register") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"student@inteliq.com","password":"SecretPassword123","name":"Jane Student"}""")
        }
        assertEquals(HttpStatusCode.Created, registerResponse.status)
        val registerBody = registerResponse.bodyAsText()
        assertTrue(registerBody.contains("token"))
        assertTrue(registerBody.contains("student@inteliq.com"))

        // 2. Login
        val loginResponse = client.post("/api/v1/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"student@inteliq.com","password":"SecretPassword123"}""")
        }
        assertEquals(HttpStatusCode.OK, loginResponse.status)
        val loginBody = loginResponse.bodyAsText()
        assertTrue(loginBody.contains("token"))
        assertTrue(loginBody.contains("refreshToken"))
    }
}
