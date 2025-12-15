package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Evaluator(
    val id: String? = null,
    val name: String,
    val surname: String,
    val email: String,
    val birthDate: String, // formato ISO (yyyy-MM-dd)
    val specialty: String,
    val cpfOrNif: String,
    val username: String,
    val password: String,
    val firstLogin: Boolean = true,
    val isAdmin: Boolean = false,
    val creationDate: String? = null
)
