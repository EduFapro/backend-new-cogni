package com.example.services

import com.example.models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class AdminService {
    fun register(admin: AdminDTO): AdminDTO {
        transaction {
            Admins.insert {
                it[fullName] = admin.fullName
                it[dateOfBirth] = admin.dateOfBirth
                it[specialty] = admin.specialty
                it[cpf] = admin.cpf
                it[username] = admin.username
            }
        }
        return admin
    }
}
