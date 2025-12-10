package com.example.services

import com.example.models.Evaluation
import com.example.models.ModuleInstance
import com.example.models.TaskInstance
import com.example.models.Participant
import com.example.models.tables.ParticipantTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

class ParticipantService(
    private val evaluationService: EvaluationService,
    private val moduleInstanceService: ModuleInstanceService,
    private val taskInstanceService: TaskInstanceService,
    private val templateService: TemplateService
) {
    private val logger = LoggerFactory.getLogger(ParticipantService::class.java)

    fun create(participant: Participant, selectedModuleIds: List<Int>): Int = transaction {
        logger.info("Creating participant: ${participant.name} ${participant.surname} with modules: $selectedModuleIds")
        
        // 1. Create Participant
        val participantId = ParticipantTable.insert {
            it[name] = participant.name
            it[surname] = participant.surname
            it[birthDate] = participant.birthDate
            it[sex] = participant.sex
            it[educationLevel] = participant.educationLevel
            it[laterality] = participant.laterality
            it[evaluatorId] = participant.evaluatorId
        }[ParticipantTable.id]

        // 2. Create Evaluation (Status 1 = Pending)
        val evaluation = Evaluation(
            id = 0,
            evaluatorId = participant.evaluatorId,
            participantId = participantId,
            evaluationDate = LocalDateTime.now().toString(),
            status = 1,
            language = 1 // Default to Portuguese
        )
        val evaluationId = evaluationService.create(evaluation)
        logger.info("Created evaluation $evaluationId for participant $participantId")

        // 3. Create Module Instances & Task Instances
        val allModules = templateService.getAllModules()
        val modulesToCreate = if (selectedModuleIds.isNotEmpty()) {
            allModules.filter { it.id in selectedModuleIds }
        } else {
            allModules // Fallback: create all if none selected (or empty list if that's preferred, but keeping legacy behavior for safety)
        }

        for (module in modulesToCreate) {
            if (!moduleInstanceService.exists(evaluationId, module.id)) {
                val moduleInstance = ModuleInstance(
                    id = 0,
                    moduleId = module.id,
                    evaluationId = evaluationId,
                    status = 1 // Pending
                )
                val moduleInstanceId = moduleInstanceService.create(moduleInstance)
                
                // Create Tasks for this Module
                val tasks = templateService.getTasksByModuleId(module.id)
                for (task in tasks) {
                    if (!taskInstanceService.exists(moduleInstanceId, task.id)) {
                        val taskInstance = TaskInstance(
                            id = 0,
                            taskId = task.id,
                            moduleInstanceId = moduleInstanceId,
                            status = 1, // Pending
                            executionDuration = null
                        )
                        taskInstanceService.create(taskInstance)
                    }
                }
            }
        }

        participantId
    }

    fun getById(id: Int): Participant? = transaction {
        ParticipantTable.selectAll().where { ParticipantTable.id eq id }
            .map { rowToParticipant(it) }
            .singleOrNull()
    }

    fun getByEvaluatorId(evaluatorId: Int): List<Participant> = transaction {
        ParticipantTable.selectAll().where { ParticipantTable.evaluatorId eq evaluatorId }
            .map { rowToParticipant(it) }
    }

    fun update(id: Int, participant: Participant): Int = transaction {
        logger.info("Updating participant ID: $id")
        
        ParticipantTable.update({ ParticipantTable.id eq id }) {
            it[name] = participant.name
            it[surname] = participant.surname
            it[birthDate] = participant.birthDate
            it[sex] = participant.sex
            it[educationLevel] = participant.educationLevel
            it[laterality] = participant.laterality
            it[evaluatorId] = participant.evaluatorId
        }
    }

    fun delete(id: Int): Int = transaction {
        logger.info("Deleting participant ID: $id")
        ParticipantTable.deleteWhere { ParticipantTable.id eq id }
    }

    private fun rowToParticipant(row: ResultRow) = Participant(
        id = row[ParticipantTable.id],
        name = row[ParticipantTable.name],
        surname = row[ParticipantTable.surname],
        birthDate = row[ParticipantTable.birthDate],
        sex = row[ParticipantTable.sex],
        educationLevel = row[ParticipantTable.educationLevel],
        laterality = row[ParticipantTable.laterality],
        evaluatorId = row[ParticipantTable.evaluatorId]
    )
}
