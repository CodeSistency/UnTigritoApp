package com.thecodefather.untigrito.domain.model

/**
 * Client Request model
 * Represents an offer made on a service posting
 */
data class ClientRequest(
    val id: String,
    val clientId: String,
    val servicePostingId: String,
    val professionalId: String? = null,
    val status: String = "PENDING", // PENDING, ACCEPTED, REJECTED, CANCELLED
    val proposedPrice: Double,
    val description: String,
    val estimatedDuration: Int = 0, // in minutes
    val createdAt: String = "",
    val updatedAt: String = ""
) {
    companion object {
        const val STATUS_PENDING = "PENDING"
        const val STATUS_ACCEPTED = "ACCEPTED"
        const val STATUS_REJECTED = "REJECTED"
        const val STATUS_CANCELLED = "CANCELLED"
    }
}
