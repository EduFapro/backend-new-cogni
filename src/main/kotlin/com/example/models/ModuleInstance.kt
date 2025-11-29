package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class ModuleInstance(
    val id: Int? = null,
    val moduleId: Int,
    val evaluationId: Int,
    val status: Int = 1 // 1=Pending, 2=InProgress, 3=Completed
)

fun ModuleInstance.validateRequiredFields(): List<String> {
    val missing = mutableListOf<String>()
    if (moduleId <= 0) missing.add("moduleId")
    if (evaluationId <= 0) missing.add("evaluationId")
    if (status !in 1..3) missing.add("status")
    return missing
}
