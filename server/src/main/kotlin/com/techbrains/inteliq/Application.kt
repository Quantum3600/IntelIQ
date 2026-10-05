package com.techbrains.inteliq

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import com.techbrains.inteliq.questions.QuestionRepository
import com.techbrains.inteliq.questions.QuestionService
import com.techbrains.inteliq.questions.questionRoutes
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun main() {
    embeddedServer(
        Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {

    // -------------------------
    // MongoDB configuration
    // -------------------------

    val mongoUri = environment.config
        .property("mongodb.uri")
        .getString()

    val databaseName = environment.config
        .property("mongodb.database")
        .getString()

    val questionsCollection = environment.config
        .property("mongodb.questionsCollection")
        .getString()

    val mongoClient = MongoClient.create(mongoUri)

    val database: MongoDatabase =
        mongoClient.getDatabase(databaseName)

    // -------------------------
    // Question dependencies
    // -------------------------

    val questionRepository =
        QuestionRepository(
            database = database,
            collectionName = questionsCollection
        )

    val questionService =
        QuestionService(questionRepository)

    // -------------------------
    // JSON
    // -------------------------

    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                ignoreUnknownKeys = true
            }
        )
    }

    // -------------------------
    // Routes
    // -------------------------

    routing {
        questionRoutes(questionService)
    }

    // -------------------------
    // MongoDB cleanup
    // -------------------------

    monitor.subscribe(ApplicationStopped) {
        mongoClient.close()
    }
}