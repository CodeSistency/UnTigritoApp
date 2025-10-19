package com.thecodefather.untigrito.domain.model

import com.thecodefather.untigrito.data.datasource.remote.SupabaseServicePosting

/**
 * Service Posting model
 * Represents a service request posted by a client
 */
data class ServicePosting(
    val id: String,
    val clientId: String,
    val title: String,
    val description: String,
    val category: String, // PLOMERIA, ELECTRICIDAD, ALBANILERIA, LIMPIEZA, MUDANZA
    val budget: Double,
    val deadline: String? = null, // ISO datetime
    val status: String = "OPEN", // OPEN, IN_PROGRESS, COMPLETED, CANCELLED
    val location: String? = null,
    val locationLat: Double? = null,
    val locationLng: Double? = null,
    val createdAt: String = "",
    val updatedAt: String = ""
) {
    companion object {
        const val STATUS_OPEN = "OPEN"
        const val STATUS_IN_PROGRESS = "IN_PROGRESS"
        const val STATUS_COMPLETED = "COMPLETED"
        const val STATUS_CANCELLED = "CANCELLED"

        const val CATEGORY_PLOMERIA = "PLOMERIA"
        const val CATEGORY_ELECTRICIDAD = "ELECTRICIDAD"
        const val CATEGORY_ALBANILERIA = "ALBANILERIA"
        const val CATEGORY_LIMPIEZA = "LIMPIEZA"
        const val CATEGORY_MUDANZA = "MUDANZA"
    }
}

// ========== Extension Functions for Supabase Integration ==========

/**
 * Converts SupabaseServicePosting to ServicePosting domain model
 */
fun SupabaseServicePosting.toServicePosting(): ServicePosting {
    return ServicePosting(
        id = this.id,
        clientId = this.clientId,
        title = this.title,
        description = this.description,
        category = this.categoryId,
        budget = this.budget ?: 0.0,
        status = this.status,
        location = this.address,
        locationLat = this.locationLat,
        locationLng = this.locationLng,
        createdAt = this.createdAt ?: "",
        updatedAt = this.updatedAt ?: ""
    )
}
