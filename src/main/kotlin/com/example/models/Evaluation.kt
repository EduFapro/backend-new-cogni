package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Evaluation(
    val id: Int? = null,
    val evaluatorId: Int,
    val participantId: Int,
    val evaluationDate: String, // ISO datetime format
    val status: Int = 1, // 1=Pending, 2=InProgress, 3=Completed
    val language: Int
)

fun Evaluation.validateRequiredFields(): List<String> {
    val missing = mutableListOf<String>()
    if (evaluatorId <= 0) missing.add("evaluatorId")
    if (participantId <= 0) missing.add("participantId")
    if (status !in 1..3) missing.add("status")
    return missing
}
