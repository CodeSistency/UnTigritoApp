package com.thecodefather.untigrito.domain.model

import com.thecodefather.untigrito.data.datasource.remote.SupabaseOffer

/**
 * Client Request model
 * Represents an offer made on a service posting
 */
data class ClientRequest(
    val id: String,
    val clientId: String = "",
    val servicePostingId: String = "",
    val postingId: String = "",
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

// ========== Extension Functions for Supabase Integration ==========

/**
 * Converts SupabaseOffer to ClientRequest domain model
 */
fun SupabaseOffer.toClientRequest(): ClientRequest {
    return ClientRequest(
        id = this.id,
        postingId = this.postingId,
        servicePostingId = this.postingId,
        professionalId = this.professionalId,
        proposedPrice = this.proposedPrice ?: this.price,
        description = this.message ?: "",
        status = this.status,
        createdAt = this.createdAt ?: ""
    )
}
