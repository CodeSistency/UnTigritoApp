package com.thecodefather.untigrito.domain.model

import com.thecodefather.untigrito.data.datasource.remote.SupabaseService
import java.util.Date

data class Service(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val minPrice: Double,
    val maxPrice: Double,
    val serviceArea: String,
    val status: ServiceStatus,
    val images: List<String> = emptyList(),
    val createdAt: Date,
    val updatedAt: Date,
    val isActive: Boolean = true,
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val completedJobs: Int = 0
)

enum class ServiceStatus {
    ACTIVE,
    INACTIVE,
    DRAFT
}

/**
 * Professional Service model
 * Represents a service offered by a professional
 */
data class ProfessionalService(
    val id: String,
    val professionalId: String,
    val title: String,
    val description: String,
    val price: Double,
    val categoryId: String,
    val isActive: Boolean = true,
    val createdAt: String = "",
    val updatedAt: String = ""
)

// ========== Extension Functions for Supabase Integration ==========

/**
 * Converts SupabaseService to ProfessionalService domain model
 */
fun SupabaseService.toProfessionalService(): ProfessionalService {
    return ProfessionalService(
        id = this.id,
        professionalId = this.professionalId,
        title = this.title,
        description = this.description,
        price = this.price,
        categoryId = this.categoryId,
        isActive = this.isActive,
        createdAt = this.createdAt ?: "",
        updatedAt = this.updatedAt ?: ""
    )
}