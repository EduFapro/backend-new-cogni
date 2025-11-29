package com.example.models.tables

import org.jetbrains.exposed.sql.Table

object RecordingFileTable : Table("recording_files") {
    val id = integer("id").autoIncrement()
    val taskInstanceId = integer("task_instance_id").references(TaskInstanceTable.id)
    val filePath = varchar("file_path", 500) // path to stored file

    override val primaryKey = PrimaryKey(id)
}
