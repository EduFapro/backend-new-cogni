package com.example.models.tables

import org.jetbrains.exposed.sql.Table

object ModuleInstanceTable : Table("module_instances") {
    val id = integer("id").autoIncrement()
    val moduleId = integer("module_id") // reference to static module data (not in this DB)
    val evaluationId = integer("evaluation_id").references(EvaluationTable.id)
    val status = integer("status").default(1) // 1=Pending, 2=InProgress, 3=Completed
    val completionDate = varchar("completion_date", 25).nullable() // ISO 8601

    override val primaryKey = PrimaryKey(id)
}
