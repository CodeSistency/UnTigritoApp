package com.thecodefather.untigrito.domain.model

import java.util.Date

data class Proposal(
    val id: String,
    val jobId: String,
    val jobTitle: String,
    val clientId: String,
    val clientName: String,
    val clientAvatar: String?,
    val proposedPrice: Double,
    val description: String,
    val estimatedDuration: Int, // en días
    val includesMaterials: Boolean,
    val offersWarranty: Boolean,
    val termsAndConditions: String?,
    val status: ProposalStatus,
    val createdAt: Date,
    val updatedAt: Date,
    val responseMessage: String? = null,
    val responseDate: Date? = null
)

enum class ProposalStatus {
    PENDING,
    ACCEPTED,
    IN_PROGRESS,
    REJECTED,
    WITHDRAWN,
    COMPLETED,
    CANCELLED,
    DISPUTED
}