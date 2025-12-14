package com.example.services

import com.example.models.Evaluation
import com.example.models.tables.EvaluationTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

class EvaluationService {
    private val logger = LoggerFactory.getLogger(EvaluationService::class.java)

    fun create(evaluation: Evaluation): Int = transaction {
        logger.info("Creating evaluation for evaluator ${evaluation.evaluatorId} and participant ${evaluation.participantId}")
        
        EvaluationTable.insert {
            it[evaluatorId] = evaluation.evaluatorId
            it[participantId] = evaluation.participantId
            it[evaluationDate] = evaluation.evaluationDate
            it[status] = evaluation.status
            it[language] = evaluation.language
            it[creationDate] = java.time.LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS).toString()
        }[EvaluationTable.id]
    }


    fun getById(id: Int): Evaluation? = transaction {
        EvaluationTable.selectAll().where { EvaluationTable.id eq id }
            .map { rowToEvaluation(it) }
            .singleOrNull()
    }

    fun update(id: Int, evaluation: Evaluation): Int = transaction {
        logger.info("Updating evaluation ID: $id")
        
        EvaluationTable.update({ EvaluationTable.id eq id }) {
            it[evaluatorId] = evaluation.evaluatorId
            it[participantId] = evaluation.participantId
            it[evaluationDate] = evaluation.evaluationDate
            it[status] = evaluation.status
            it[language] = evaluation.language
        }
    }

    fun updateStatus(id: Int, newStatus: Int): Int = transaction {
        logger.info("Updating evaluation $id status to $newStatus")
        EvaluationTable.update({ EvaluationTable.id eq id }) {
            it[status] = newStatus
            if (newStatus == 3) {
                it[completionDate] = java.time.LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS).toString()
            }
        }
    }

    private fun rowToEvaluation(row: ResultRow): Evaluation = Evaluation(
        id = row[EvaluationTable.id],
        evaluatorId = row[EvaluationTable.evaluatorId],
        participantId = row[EvaluationTable.participantId],
        evaluationDate = row[EvaluationTable.evaluationDate],
        status = row[EvaluationTable.status],
        language = row[EvaluationTable.language],
        creationDate = row[EvaluationTable.creationDate],
        completionDate = row[EvaluationTable.completionDate]
    )
}

