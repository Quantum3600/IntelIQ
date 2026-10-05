package com.techbrains.inteliq.questions

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Question(
    val id: String,

    @SerialName("skill_id")
    val skillId: Int,

    @SerialName("skill_name")
    val skillName: String,

    val difficulty: Double,
    val type: String,
    val question: String,
    val options: Map<String, String>,

    @SerialName("correct_answer")
    val correctAnswer: String,

    val explanation: String
)