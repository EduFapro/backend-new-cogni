package com.example.models.tables

import org.jetbrains.exposed.sql.Table

object ParticipantTable : Table("participants") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255) // stored as plain text (encrypted in Flutter)
    val surname = varchar("surname", 255)
    val birthDate = varchar("birth_date", 10) // ISO format yyyy-MM-dd
    val sex = integer("sex") // 1=Male, 2=Female, 3=Other
    val educationLevel = integer("education_level")
    val laterality = integer("laterality") // 1=Right, 2=Left, 3=Both
    val evaluatorId = varchar("evaluator_id", 36).references(EvaluatorTable.id)
    val creationDate = varchar("creation_date", 25).default("") // ISO 8601

    override val primaryKey = PrimaryKey(id)
}
