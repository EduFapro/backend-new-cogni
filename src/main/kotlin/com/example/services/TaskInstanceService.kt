package com.example.services

import com.example.models.TaskInstance
import com.example.models.tables.TaskInstanceTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

class TaskInstanceService {
    private val logger = LoggerFactory.getLogger(TaskInstanceService::class.java)

    fun create(taskInstance: TaskInstance): Int = transaction {
        logger.info("Creating task instance for module instance ${taskInstance.moduleInstanceId}")
        
        TaskInstanceTable.insert {
            it[taskId] = taskInstance.taskId
            it[moduleInstanceId] = taskInstance.moduleInstanceId
            it[status] = taskInstance.status
            it[completingTime] = taskInstance.completingTime
        }[TaskInstanceTable.id]
    }

    fun getById(id: Int): TaskInstance? = transaction {
        TaskInstanceTable.selectAll().where { TaskInstanceTable.id eq id }
            .map { rowToTaskInstance(it) }
            .singleOrNull()
    }

    fun getByModuleInstanceId(moduleInstanceId: Int): List<TaskInstance> = transaction {
        TaskInstanceTable.selectAll().where { TaskInstanceTable.moduleInstanceId eq moduleInstanceId }
            .map { rowToTaskInstance(it) }
    }

    fun exists(moduleInstanceId: Int, taskId: Int): Boolean = transaction {
        TaskInstanceTable.selectAll()
            .where { (TaskInstanceTable.moduleInstanceId eq moduleInstanceId) and (TaskInstanceTable.taskId eq taskId) }
            .count() > 0
    }

    fun markAsCompleted(id: Int, duration: String?): Int = transaction {
        logger.info("Marking task instance $id as completed")
        TaskInstanceTable.update({ TaskInstanceTable.id eq id }) {
            it[status] = 3 // Completed
            if (duration != null) {
                it[completingTime] = duration
            }
        }
    }

    fun delete(id: Int): Int = transaction {
        logger.info("Deleting task instance ID: $id")
        TaskInstanceTable.deleteWhere { TaskInstanceTable.id eq id }
    }

    private fun rowToTaskInstance(row: ResultRow) = TaskInstance(
        id = row[TaskInstanceTable.id],
        taskId = row[TaskInstanceTable.taskId],
        moduleInstanceId = row[TaskInstanceTable.moduleInstanceId],
        status = row[TaskInstanceTable.status],
        completingTime = row[TaskInstanceTable.completingTime]
    )
}
