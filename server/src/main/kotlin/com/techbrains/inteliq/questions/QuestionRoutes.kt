package com.techbrains.inteliq.questions

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.questionRoutes(
    service: QuestionService
) {

    route("/api/v1/questions") {

        // GET all questions
        get {
            val questions = service.getAllQuestions()
            call.respond(questions)
        }

        // Search questions by question text
        get("/search") {
            val text = call.request.queryParameters["text"]

            if (text.isNullOrBlank()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "text parameter is required")
                )
                return@get
            }

            val questions = service.searchQuestions(text)
            call.respond(questions)
        }

        // Get questions by skill
        get("/skill/{skillId}") {
            val skillIdText = call.parameters["skillId"]

            val skillId = skillIdText?.toIntOrNull()

            if (skillId == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "skillId must be an integer")
                )
                return@get
            }

            val questions = service.getQuestionsBySkill(skillId)
            call.respond(questions)
        }

        // Get question by ID
        get("/{questionId}") {
            val questionId = call.parameters["questionId"]

            if (questionId.isNullOrBlank()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "questionId is required")
                )
                return@get
            }

            val question = service.getQuestionById(questionId)

            if (question == null) {
                call.respond(
                    HttpStatusCode.NotFound,
                    mapOf("error" to "Question not found")
                )
                return@get
            }

            call.respond(question)
        }
    }
}