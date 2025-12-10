package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class TaskInstance(
    val id: Int? = null,
    val taskId: Int,
    val moduleInstanceId: Int,
    val status: Int = 1, // 1=Pending, 2=InProgress, 3=Completed
    val executionDuration: String? = null
)

fun TaskInstance.validateRequiredFields(): List<String> {
    val missing = mutableListOf<String>()
    if (taskId <= 0) missing.add("taskId")
    if (moduleInstanceId <= 0) missing.add("moduleInstanceId")
    if (status !in 1..3) missing.add("status")
    return missing
}
