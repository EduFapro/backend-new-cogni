package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class RecordingFile(
    val id: Int? = null,
    val taskInstanceId: Int,
    val filePath: String
)

fun RecordingFile.validateRequiredFields(): List<String> {
    val missing = mutableListOf<String>()
    if (taskInstanceId <= 0) missing.add("taskInstanceId")
    if (filePath.isBlank()) missing.add("filePath")
    return missing
}
