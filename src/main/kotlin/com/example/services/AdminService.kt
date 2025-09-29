package com.example.services


import com.example.models.AdminDTO
import org.slf4j.LoggerFactory

class AdminService {
    private val logger = LoggerFactory.getLogger(AdminService::class.java)

    fun register(admin: AdminDTO): AdminDTO {
        // Apenas loga por enquanto
        logger.info("📥 Registrando admin: {}", admin)
        return admin
    }
}
