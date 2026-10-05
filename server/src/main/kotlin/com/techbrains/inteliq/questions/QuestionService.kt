package com.techbrains.inteliq.questions

class QuestionService(
    private val repository: QuestionRepository
) {

    suspend fun getAllQuestions(): List<Question> {
        return repository.getAll()
    }

    suspend fun getQuestionsBySkill(skillId: Int): List<Question> {
        return repository.getBySkill(skillId)
    }

    suspend fun searchQuestions(text: String): List<Question> {
        return repository.searchByQuestion(text)
    }

    suspend fun getQuestionById(id: String): Question? {
        return repository.getById(id)
    }
}