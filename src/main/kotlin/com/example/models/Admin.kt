package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Admin(
    val name: String,
    val surname: String,
    val birthDate: String,
    val specialty: String,
    val cpfOrNif: String,
    val username: String,
    val password: String,
    val firstLogin: Boolean = true,
    val isAdmin: Boolean = true
)
