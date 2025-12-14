package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Participant(
    val id: Int? = null,
    val name: String,
    val surname: String,
    val birthDate: String, // ISO format yyyy-MM-dd
    val sex: Int, // 1=Male, 2=Female, 3=Other
    val educationLevel: Int,
    val laterality: Int, // 1=Right, 2=Left, 3=Both
    val evaluatorId: Int,
    val creationDate: String? = null
)

fun Participant.validateRequiredFields(): List<String> {
    val missing = mutableListOf<String>()
    if (name.isBlank()) missing.add("name")
    if (surname.isBlank()) missing.add("surname")
    if (birthDate.isBlank()) missing.add("birthDate")
    if (sex !in 1..3) missing.add("sex")
    if (laterality !in 1..3) missing.add("laterality")
    return missing
}
