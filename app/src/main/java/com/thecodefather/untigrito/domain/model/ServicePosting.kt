package com.thecodefather.untigrito.domain.model

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
