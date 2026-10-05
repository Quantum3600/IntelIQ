package com.techbrains.inteliq.questions

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList

class QuestionRepository(
    database: MongoDatabase,
    collectionName: String
) {

    private val collection =
        database.getCollection<Question>(collectionName)

    suspend fun getAll(): List<Question> {
        return collection
            .find()
            .toList()
    }

    suspend fun getBySkill(skillId: Int): List<Question> {
        return collection
            .find(
                Filters.eq(
                    Question::skillId.name,
                    skillId
                )
            )
            .toList()
    }

    suspend fun searchByQuestion(text: String): List<Question> {
        return collection
            .find(
                Filters.regex(
                    Question::question.name,
                    text,
                    "i"
                )
            )
            .toList()
    }

    suspend fun getById(id: String): Question? {
        return collection
            .find(
                Filters.eq(
                    Question::id.name,
                    id
                )
            )
            .firstOrNull()
    }
}