package com.example.services

import com.example.models.ModuleInstance
import com.example.models.tables.ModuleInstanceTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

class ModuleInstanceService {
    private val logger = LoggerFactory.getLogger(ModuleInstanceService::class.java)

    fun create(moduleInstance: ModuleInstance): Int = transaction {
        logger.info("Creating module instance for evaluation ${moduleInstance.evaluationId}")
        
        ModuleInstanceTable.insert {
            it[moduleId] = moduleInstance.moduleId
            it[evaluationId] = moduleInstance.evaluationId
            it[status] = moduleInstance.status
        }[ModuleInstanceTable.id]
    }

    fun getById(id: Int): ModuleInstance? = transaction {
        ModuleInstanceTable.selectAll().where { ModuleInstanceTable.id eq id }
            .map { rowToModuleInstance(it) }
            .singleOrNull()
    }

    fun getByEvaluationId(evaluationId: Int): List<ModuleInstance> = transaction {
        ModuleInstanceTable.selectAll().where { ModuleInstanceTable.evaluationId eq evaluationId }
            .map { rowToModuleInstance(it) }
    }

    fun exists(evaluationId: Int, moduleId: Int): Boolean = transaction {
        ModuleInstanceTable.selectAll()
            .where { (ModuleInstanceTable.evaluationId eq evaluationId) and (ModuleInstanceTable.moduleId eq moduleId) }
            .count() > 0
    }

    fun updateStatus(id: Int, newStatus: Int): Int = transaction {
        logger.info("Updating module instance $id status to $newStatus")
        ModuleInstanceTable.update({ ModuleInstanceTable.id eq id }) {
            it[status] = newStatus
        }
    }

    fun delete(id: Int): Int = transaction {
        logger.info("Deleting module instance ID: $id")
        ModuleInstanceTable.deleteWhere { ModuleInstanceTable.id eq id }
    }

    private fun rowToModuleInstance(row: ResultRow) = ModuleInstance(
        id = row[ModuleInstanceTable.id],
        moduleId = row[ModuleInstanceTable.moduleId],
        evaluationId = row[ModuleInstanceTable.evaluationId],
        status = row[ModuleInstanceTable.status]
    )
}
