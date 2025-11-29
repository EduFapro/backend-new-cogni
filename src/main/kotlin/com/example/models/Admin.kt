package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Admin(
    val name: String?,
    val surname: String?,
    val email: String?,
    val birthDate: String?,
    val specialty: String?,
    val cpfOrNif: String?,
    val username: String?,
    val password: String?,
    val firstLogin: Boolean = true,
    val isAdmin: Boolean = true
)

fun Admin.validateRequiredFields(): List<String> {
    val missing = mutableListOf<String>()
    if (name.isNullOrBlank()) missing.add("name")
    if (surname.isNullOrBlank()) missing.add("surname")
    if (email.isNullOrBlank()) missing.add("email")
    if (birthDate.isNullOrBlank()) missing.add("birthDate")
    if (specialty.isNullOrBlank()) missing.add("specialty")
    if (cpfOrNif.isNullOrBlank()) missing.add("cpfOrNif")
    if (username.isNullOrBlank()) missing.add("username")
    if (password.isNullOrBlank()) missing.add("password")
    return missing
}
