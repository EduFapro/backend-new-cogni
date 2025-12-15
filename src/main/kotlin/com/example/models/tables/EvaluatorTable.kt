package com.example.models.tables

import org.jetbrains.exposed.sql.Table

object EvaluatorTable : Table("evaluators") {
    val id = varchar("id", 36)
    val name = varchar("name", 100)
    val surname = varchar("surname", 100)
    val email = varchar("email", 150).uniqueIndex()
    val birthDate = varchar("birth_date", 10) // ISO format yyyy-MM-dd
    val specialty = varchar("specialty", 100)
    val cpfOrNif = varchar("cpf_or_nif", 50).uniqueIndex()
    val username = varchar("username", 50).uniqueIndex()
    val password = varchar("password", 255) // hashed
    val firstLogin = bool("first_login").default(true)
    val isAdmin = bool("is_admin").default(false)
    val creationDate = varchar("creation_date", 25).default("") // ISO 8601

    override val primaryKey = PrimaryKey(id)
}
