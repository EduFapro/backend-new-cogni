package com.example.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object Admins : Table("admins") {
    val id = integer("id").autoIncrement()
    val fullName = varchar("full_name", 100)
    val dateOfBirth = varchar("dob", 20)
    val specialty = varchar("specialty", 100)
    val cpf = varchar("cpf", 20).uniqueIndex()
    val username = varchar("username", 100)

    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class AdminDTO(
    val fullName: String,
    val dateOfBirth: String,
    val specialty: String,
    val cpf: String,
    val username: String
)
