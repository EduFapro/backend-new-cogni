package com.example.models.tables

import org.jetbrains.exposed.sql.Table

object TaskInstanceTable : Table("task_instances") {
    val id = integer("id").autoIncrement()
    val taskId = integer("task_id") // reference to static task data (not in this DB)
    val moduleInstanceId = integer("module_instance_id").references(ModuleInstanceTable.id)
    val status = integer("status").default(1) // 1=Pending, 2=InProgress, 3=Completed
    val videoPath = varchar("video_path", 500).nullable()
    val executionDuration = varchar("execution_duration", 50).nullable()
    val completionDate = varchar("completion_date", 25).nullable() // ISO 8601

    override val primaryKey = PrimaryKey(id)
}
