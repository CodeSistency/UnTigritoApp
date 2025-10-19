package com.thecodefather.untigrito.domain.model

import com.thecodefather.untigrito.data.datasource.remote.SupabaseServiceTransaction
import com.thecodefather.untigrito.data.datasource.remote.SupabaseUser

/**
 * Modelo de solicitud del cliente para un servicio profesional
 */
data class ServiceRequest(
    val id: String,
    val clientId: String,
    val clientName: String,
    val clientLocation: String?,
    val clientRating: Double,
    val requestedPrice: Double,
    val message: String?,
    val status: String,
    val createdAt: String
)

/**
 * Modelo que combina transacción y datos del cliente
 */
data class ServiceRequestWithClient(
    val transaction: SupabaseServiceTransaction,
    val client: SupabaseUser
)
