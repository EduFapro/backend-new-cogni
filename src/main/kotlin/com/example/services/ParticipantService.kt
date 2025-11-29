package com.example.services

import com.example.models.Participant
import com.example.models.tables.ParticipantTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

class ParticipantService {
    private val logger = LoggerFactory.getLogger(ParticipantService::class.java)

    fun create(participant: Participant): Int = transaction {
        logger.info("Creating participant: ${participant.name} ${participant.surname}")
        
        ParticipantTable.insert {
            it[name] = participant.name
            it[surname] = participant.surname
            it[birthDate] = participant.birthDate
            it[sex] = participant.sex
            it[educationLevel] = participant.educationLevel
            it[laterality] = participant.laterality
            it[evaluatorId] = participant.evaluatorId
        }[ParticipantTable.id]
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
