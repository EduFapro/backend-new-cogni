package com.example.services

import com.example.models.RecordingFile
import com.example.models.tables.RecordingFileTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

class RecordingFileService {
    private val logger = LoggerFactory.getLogger(RecordingFileService::class.java)

    fun create(recordingFile: RecordingFile): Int = transaction {
        logger.info("Creating recording file for task instance ${recordingFile.taskInstanceId}")
        
        RecordingFileTable.insert {
            it[taskInstanceId] = recordingFile.taskInstanceId
            it[filePath] = recordingFile.filePath
        }[RecordingFileTable.id]
    }

    fun getById(id: Int): RecordingFile? = transaction {
        RecordingFileTable.selectAll().where { RecordingFileTable.id eq id }
            .map { rowToRecordingFile(it) }
            .singleOrNull()
    }

    fun getByTaskInstanceId(taskInstanceId: Int): RecordingFile? = transaction {
        RecordingFileTable.selectAll().where { RecordingFileTable.taskInstanceId eq taskInstanceId }
            .map { rowToRecordingFile(it) }
            .singleOrNull()
    }

    fun delete(id: Int): Int = transaction {
        logger.info("Deleting recording file ID: $id")
        RecordingFileTable.deleteWhere { RecordingFileTable.id eq id }
    }

    private fun rowToRecordingFile(row: ResultRow) = RecordingFile(
        id = row[RecordingFileTable.id],
        taskInstanceId = row[RecordingFileTable.taskInstanceId],
        filePath = row[RecordingFileTable.filePath]
    )
}
