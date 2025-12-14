package com.example.services

import com.example.models.Evaluator
import com.example.models.tables.EvaluatorTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

class EvaluatorService {
    private val logger = LoggerFactory.getLogger(EvaluatorService::class.java)

    fun create(evaluator: Evaluator): Int = transaction {
        logger.info("Creating evaluator: ${evaluator.username}")
        
        val authService = AuthService()
        val hashedPassword = authService.hashPassword(evaluator.password)

        EvaluatorTable.insert {
            it[name] = evaluator.name
            it[surname] = evaluator.surname
            it[email] = evaluator.email
            it[birthDate] = evaluator.birthDate
            it[specialty] = evaluator.specialty
            it[cpfOrNif] = evaluator.cpfOrNif
            it[username] = evaluator.username
            it[password] = hashedPassword
            it[firstLogin] = evaluator.firstLogin
            it[isAdmin] = evaluator.isAdmin
            it[creationDate] = java.time.LocalDateTime.now().toString()
        }[EvaluatorTable.id]
    }

    fun getById(id: Int): Evaluator? = transaction {
        EvaluatorTable.selectAll().where { EvaluatorTable.id eq id }
            .map { rowToEvaluator(it) }
            .singleOrNull()
    }

    fun update(id: Int, evaluator: Evaluator): Int = transaction {
        logger.info("Updating evaluator ID: $id")
        
        val authService = AuthService()
        val hashedPassword = authService.hashPassword(evaluator.password)

        EvaluatorTable.update({ EvaluatorTable.id eq id }) {
            it[name] = evaluator.name
            it[surname] = evaluator.surname
            it[email] = evaluator.email
            it[birthDate] = evaluator.birthDate
            it[specialty] = evaluator.specialty
            it[cpfOrNif] = evaluator.cpfOrNif
            it[username] = evaluator.username
            it[password] = hashedPassword
            it[firstLogin] = evaluator.firstLogin
            it[isAdmin] = evaluator.isAdmin
        }
    }

    fun delete(id: Int): Int = transaction {
        logger.info("Deleting evaluator ID: $id")
        EvaluatorTable.deleteWhere { EvaluatorTable.id eq id }
    }

    private fun rowToEvaluator(row: ResultRow) = Evaluator(
        id = row[EvaluatorTable.id],
        name = row[EvaluatorTable.name],
        surname = row[EvaluatorTable.surname],
        email = row[EvaluatorTable.email],
        birthDate = row[EvaluatorTable.birthDate],
        specialty = row[EvaluatorTable.specialty],
        cpfOrNif = row[EvaluatorTable.cpfOrNif],
        username = row[EvaluatorTable.username],
        password = row[EvaluatorTable.password],
        firstLogin = row[EvaluatorTable.firstLogin],
        isAdmin = row[EvaluatorTable.isAdmin],
        creationDate = row[EvaluatorTable.creationDate]
    )
}
