package com.example.models.tables

import org.jetbrains.exposed.sql.Table

object EvaluationTable : Table("evaluations") {
    val id = integer("id").autoIncrement()
    val evaluatorId = varchar("evaluator_id", 36).references(EvaluatorTable.id)
    val participantId = integer("participant_id").references(ParticipantTable.id)
    val evaluationDate = varchar("evaluation_date", 50) // ISO datetime string
    val status = integer("status").default(1) // 1=Pending, 2=InProgress, 3=Completed
    val language = integer("language") // language code
    val creationDate = varchar("creation_date", 25).default("") // ISO 8601
    val completionDate = varchar("completion_date", 25).nullable() // ISO 8601

    override val primaryKey = PrimaryKey(id)
}

